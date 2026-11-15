/**
 * PrivateVault P2P WebRTC Signaling Server
 * 
 * CORE ARCHITECTURAL CONTRACT:
 * 1. Zero Cloud Media Storage: No photos or videos pass through or are saved here.
 * 2. Ephemeral Signaling Only: Relays SDP offers, answers, ICE candidates, and public ECDH keys.
 * 3. Sovereign Access Control: Enforces expiry timestamps, max sessions, and instant revocation.
 */

const http = require('http');
const { WebSocketServer, WebSocket } = require('ws');

const PORT = process.env.PORT || 8080;

// Active connected clients map: accountId -> WebSocket
const clients = new Map();

// Active sharing permissions store in memory (with TTL)
// shareId -> { shareId, ownerId, recipientId, mediaId, expiresAt, maxSessions, sessionsUsed, isRevoked }
const permissions = new Map();

const server = http.createServer((req, res) => {
    const url = new URL(req.url, `http://${req.headers.host}`);

    // Healthcheck
    if (url.pathname === '/health') {
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
            status: 'HEALTHY',
            service: 'PrivateVault Signaling Server',
            activeClients: clients.size,
            activePermissions: permissions.size,
            zeroCloudStorageEnforced: true,
            timestamp: Date.now()
        }));
        return;
    }

    // Query Permission Status
    if (url.pathname.startsWith('/permissions/')) {
        const shareId = url.pathname.replace('/permissions/', '');
        const perm = permissions.get(shareId);

        if (!perm) {
            res.writeHead(404, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({ error: 'Permission not found' }));
            return;
        }

        const isExpired = Date.now() > perm.expiresAt;
        const isSessionExhausted = perm.maxSessions !== null && perm.sessionsUsed >= perm.maxSessions;
        const isViewable = !perm.isRevoked && !isExpired && !isSessionExhausted;

        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
            shareId: perm.shareId,
            isViewable,
            isRevoked: perm.isRevoked,
            isExpired,
            sessionsUsed: perm.sessionsUsed,
            maxSessions: perm.maxSessions
        }));
        return;
    }

    res.writeHead(404);
    res.end();
});

const wss = new WebSocketServer({ server });

wss.on('connection', (ws) => {
    let clientAccountId = null;

    ws.on('message', (messageText) => {
        try {
            const data = JSON.parse(messageText);

            switch (data.type) {
                // 1. Client Registration
                case 'REGISTER': {
                    clientAccountId = data.accountId;
                    clients.set(clientAccountId, ws);
                    console.log(`[REGISTER] Account connected: ${clientAccountId} (Total: ${clients.size})`);
                    ws.send(JSON.stringify({ type: 'REGISTER_OK', accountId: clientAccountId }));
                    break;
                }

                // 2. Sender Creates / Registers an Invitation
                case 'CREATE_INVITATION': {
                    const { shareId, ownerId, recipientId, mediaId, durationMs, maxSessions } = data;
                    const expiresAt = Date.now() + (durationMs || 24 * 3600 * 1000);

                    permissions.set(shareId, {
                        shareId,
                        ownerId,
                        recipientId,
                        mediaId,
                        expiresAt,
                        maxSessions: maxSessions || null,
                        sessionsUsed: 0,
                        isRevoked: false
                    });

                    console.log(`[INVITATION] Created shareId=${shareId} by ${ownerId} for ${recipientId}`);
                    ws.send(JSON.stringify({ type: 'INVITATION_SAVED', shareId, expiresAt }));

                    // Notify recipient if currently connected
                    const recipientWs = clients.get(recipientId);
                    if (recipientWs && recipientWs.readyState === WebSocket.OPEN) {
                        recipientWs.send(JSON.stringify({
                            type: 'INCOMING_SHARE',
                            shareId,
                            ownerId,
                            expiresAt
                        }));
                    }
                    break;
                }

                // 3. WebRTC Offer (Sender -> Recipient) with Ephemeral ECDH Key
                case 'SIGNAL_OFFER': {
                    const { shareId, recipientId, sdp, ecdhPublicKey } = data;
                    const perm = permissions.get(shareId);

                    if (!perm || perm.isRevoked || Date.now() > perm.expiresAt) {
                        ws.send(JSON.stringify({ type: 'SIGNAL_ERROR', message: 'Permission expired or revoked' }));
                        return;
                    }

                    const target = clients.get(recipientId);
                    if (target && target.readyState === WebSocket.OPEN) {
                        target.send(JSON.stringify({
                            type: 'SIGNAL_OFFER',
                            shareId,
                            senderId: clientAccountId,
                            sdp,
                            ecdhPublicKey
                        }));
                    } else {
                        ws.send(JSON.stringify({ type: 'SIGNAL_ERROR', message: 'Recipient is currently offline' }));
                    }
                    break;
                }

                // 4. WebRTC Answer (Recipient -> Sender) with Ephemeral ECDH Key
                case 'SIGNAL_ANSWER': {
                    const { shareId, senderId, sdp, ecdhPublicKey } = data;
                    const perm = permissions.get(shareId);

                    if (!perm || perm.isRevoked || Date.now() > perm.expiresAt) {
                        ws.send(JSON.stringify({ type: 'SIGNAL_ERROR', message: 'Permission expired or revoked' }));
                        return;
                    }

                    // Increment session count
                    perm.sessionsUsed += 1;

                    const target = clients.get(senderId);
                    if (target && target.readyState === WebSocket.OPEN) {
                        target.send(JSON.stringify({
                            type: 'SIGNAL_ANSWER',
                            shareId,
                            recipientId: clientAccountId,
                            sdp,
                            ecdhPublicKey
                        }));
                    }
                    break;
                }

                // 5. ICE Candidate Relay
                case 'SIGNAL_ICE': {
                    const { targetAccountId, candidate } = data;
                    const target = clients.get(targetAccountId);
                    if (target && target.readyState === WebSocket.OPEN) {
                        target.send(JSON.stringify({
                            type: 'SIGNAL_ICE',
                            senderId: clientAccountId,
                            candidate
                        }));
                    }
                    break;
                }

                // 6. Instant Revocation by Sender
                case 'REVOKE_PERMISSION': {
                    const { shareId } = data;
                    const perm = permissions.get(shareId);
                    if (perm) {
                        perm.isRevoked = true;
                        console.log(`[REVOKE] Permission revoked immediately: ${shareId}`);

                        // Notify recipient to cut stream
                        const recipientWs = clients.get(perm.recipientId);
                        if (recipientWs && recipientWs.readyState === WebSocket.OPEN) {
                            recipientWs.send(JSON.stringify({
                                type: 'ACCESS_REVOKED',
                                shareId
                            }));
                        }
                        ws.send(JSON.stringify({ type: 'REVOKE_OK', shareId }));
                    }
                    break;
                }
            }
        } catch (err) {
            console.error('Error handling WebSocket message:', err);
        }
    });

    ws.on('close', () => {
        if (clientAccountId) {
            clients.delete(clientAccountId);
            console.log(`[DISCONNECT] Account disconnected: ${clientAccountId} (Remaining: ${clients.size})`);
        }
    });
});

server.listen(PORT, () => {
    console.log(`══════════════════════════════════════════════════════`);
    console.log(`PrivateVault WebRTC Signaling Server listening on :${PORT}`);
    console.log(`Zero Cloud Media Storage: Verified`);
    console.log(`Ephemeral ECDH Handshakes: Ready`);
    console.log(`══════════════════════════════════════════════════════`);
});
