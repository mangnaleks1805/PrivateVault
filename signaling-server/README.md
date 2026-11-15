# PrivateVault WebRTC Signaling Server

A lightweight, zero-cloud-media WebRTC signaling server for **PrivateVault**.

## Architectural Guarantees
1. **Zero Media Storage:** No photos, videos, or raw audio streams ever touch or pass through this server.
2. **Ephemeral Handshake Only:** Relays WebRTC SDP offers/answers, ICE candidates, and ephemeral ECDH public keys.
3. **Hardware-Isolated Access Control:** Validates access tokens, enforces expiration timestamps, limits viewing sessions, and processes instant revocation.

## How to Run Locally

```bash
cd signaling-server
npm install
npm start
```
Default port: `8080`.  
Health check endpoint: `http://localhost:8080/health`.

## Free 1-Click Cloud Deployment
Deploy for free to [Railway](https://railway.app), [Render](https://render.com), or [Fly.io](https://fly.io) using the included `Dockerfile`.
Set environment variable `PORT=8080`.
