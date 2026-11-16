# Technical Requirements Document

**Android architecture, encryption, backend boundary and controlled-sharing design**

**Product:** Secure Personal Camera & Private Vault  
**Version:** 0.9 - Draft for product, engineering and legal review  
**Prepared:** 16 November 2026  
**Status:** Working specification - requires engineering, security, legal and policy review

## Shared product decisions

- **Local-first by default:** The camera and encrypted vault work without account creation. Accounts are required only for remote sharing and account-linked subscription capabilities, if those are implemented.
- **No media cloud:** The service does not provide cloud media storage, automatic media upload, cloud backup, public media links or server-side media indexing.
- **Visible consensual capture only:** Recording starts only after an explicit user action in a visible camera screen. No hidden, covert, disguised, background or screen-off recording.
- **Adult-only product intent:** The app is designed for adults. A self-declared 18+ screen is an eligibility gate, not proof of age. Any stronger age assurance must be decided through legal and Play policy review.
- **Private recipient viewing:** The later sharing concept is controlled, recipient-specific viewing streamed from the sender device. The sender must be online. TURN may relay encrypted packets transiently; it must not persist media.
- **Revocation boundary:** Revocation prevents future authorization and subsequent sessions. It cannot reverse prior viewing, erase a recipient’s memory, or prevent an external camera/compromised device from capturing displayed content.
- **No export in the product flow:** The app does not offer media export, system share-sheet forwarding, public links or recipient re-sharing. This is a product control, not an absolute anti-copy guarantee.
- **No advertising or media analytics:** No ads, ad SDKs, media-inspecting analytics, third-party media processors, or media content in crash logs. Essential account/security telemetry is minimized.
- **Separate account and local-vault deletion:** Deleting a remote account revokes remote access and deletes server account data according to disclosed retention. Users are clearly offered a separate choice to delete local vault contents; account deletion must not silently destroy local-only media.
- **Security claims are bounded:** Do not claim “100% leak-proof”, “impossible to copy” or “military-grade” without substantiation. Describe concrete controls and limitations.

## 1. Technical summary

The Android client is the trusted media boundary. Capture, encryption, key wrapping, local indexing, decryption and playback happen on-device. The optional backend handles account identity, registered device public keys, sharing permissions, ephemeral signalling and subscription entitlement verification. It has no media upload, download or storage API.

> **Architecture status:** Recommended target architecture. The private-sharing transport and key protocol require a proof of concept and independent security review. Do not treat a working WebRTC demo as a production security approval.

## 2. Architecture overview

```text
ANDROID CLIENT
  UI (Jetpack Compose)
    |
  ViewModels / Use Cases
    |
  CameraX Capture ----> Encrypted Media Pipeline ----> App-private encrypted files
    |                            |                              |
  Biometric Gate            Android Keystore              Room metadata DB

OPTIONAL NETWORK SERVICES (only when account/sharing is used)
  API backend: auth, devices, invitations, grants, account deletion, entitlements
  PostgreSQL: account and permission metadata only
  Signalling: short-lived session negotiation metadata
  STUN/TURN: transient transport relay for encrypted packets

PROHIBITED: media upload endpoint, object-storage bucket, plaintext media cache, ad SDK, media analytics SDK
```

## 3. Android client modules

| Module | Responsibilities | Dependencies / boundaries |
| --- | --- | --- |
| app | Application startup, dependency injection, navigation and lifecycle. | Compose UI; no direct file crypto logic. |
| feature-onboarding | Privacy disclosure, age declaration, runtime permission rationale. | No identity verification claims. |
| feature-camera | CameraX preview, explicit capture start/stop, recording state. | Sends finalized media stream to encrypted repository; never public storage. |
| feature-vault | Local media list, categories, playback and delete. | Reads metadata via repository; decrypts through media pipeline only when authorized. |
| core-crypto | DEK generation, key wrapping, chunk encryption, file header and versioning. | Use audited primitive/library; no custom algorithms. |
| core-storage | Private file paths, atomic writes, backup exclusions, cleanup. | Internal app storage / noBackupFilesDir; no MediaStore. |
| core-auth | BiometricPrompt, device credential and session lock state. | App lock is separate from cryptographic access control. |
| core-database | Room entities, DAOs and migrations. | Metadata only; no media bytes or plaintext names by default. |
| feature-account (later) | Passkey login, device registration, account settings and deletion. | Optional; not required by local vault. |
| feature-sharing (later) | Invitations, accepted recipients, live-view session and revoke UI. | Gated on P2P/E2EE review. |
| feature-billing (later) | Google Play Billing, entitlement state and restore. | No advertising or tracking SDK. |

## 4. Local media data flow

1. User explicitly starts capture on visible camera screen.
2. Prefer a capture pipeline that encrypts while writing. If the selected CameraX path requires a temporary plaintext file, it may exist only during an explicit foreground capture/finalization window inside app-private no-backup storage, protected by app-private file permissions, and must be removed immediately after successful encryption or on recovery. Evaluate a custom MediaCodec/MediaMuxer or encrypted output sink to avoid plaintext-at-rest. Any temporary-plaintext design requires documented threat-model approval; never place it in shared/public storage or leave it after finalization.
3. A per-media random Data Encryption Key (DEK) is generated using a cryptographically secure random source.
4. Encrypt media using an authenticated, chunk-capable format. For large media, use independently authenticated chunks with strict nonce uniqueness and authenticated metadata.
5. Wrap the DEK using a non-exportable Android Keystore key. Store only the wrapped DEK and crypto metadata in Room/file header.
6. Atomically finalize encrypted file and metadata. Remove temporary plaintext artifacts on success, failure and recovery.
7. Playback authenticates the user, unwraps the DEK, decrypts only the chunks needed for playback and feeds them to the player without creating a persistent plaintext export.
8. Delete removes metadata and encrypted file, then deletes per-file key material. Explain that flash storage wear-levelling means physical overwrite cannot be guaranteed; key destruction is the primary crypto-erasure mechanism.

## 5. Cryptographic design requirements

| Concern | Requirement |
| --- | --- |
| Primitive | Use authenticated encryption such as AES-256-GCM through a maintained, reviewed library/API. Avoid home-grown crypto. |
| Per-file key | Generate a fresh random DEK for each media asset. Never reuse a DEK across unrelated files. |
| Chunking | For large videos, define a versioned chunk format with unique nonce derivation/allocation, authenticated chunk index and authenticated file metadata. Prove nonce uniqueness across interruptions/resumes. |
| Key wrapping | Use Android Keystore to protect a wrapping key; store only wrapped DEKs. Keystore key is non-exportable where supported. StrongBox may be used when available, not required. |
| Authentication binding | Choose carefully whether key use requires recent biometric/device authentication. Biometric-enrollment changes and Keystore key invalidation need explicit recovery behaviour. |
| Recovery | No cloud backup means lost device or invalidated key may permanently lose media. MVP should state this clearly. Do not invent a server recovery key. |
| File format | Include magic bytes, format version, algorithm identifiers, random asset ID, chunk parameters, wrapped DEK reference and integrity metadata. Never include title or sensitive media metadata in cleartext. |
| Migration | Future migration must be user-initiated, encrypted end-to-end, recipient-device authenticated, and tested for interruption. No automatic export to cloud. |

> **Crypto implementation gate:** The specific chunked-encryption format, nonce strategy, authentication binding and recovery path must be reviewed by a qualified mobile security engineer. This document does not authorize inventing a custom cryptographic protocol.

> **Capture pipeline gate:** Do not claim “encrypted from the instant the camera sensor captures a frame” unless the implementation truly encrypts on the write path. CameraX output options may require a temporary app-private file. Prove the actual data path, cleanup behaviour and crash recovery on-device before release.

## 6. Local database and filesystem

Room stores opaque asset IDs, file references, size, media type, duration, creation time and encrypted metadata fields as needed. Avoid storing original filenames, descriptive titles, GPS coordinates or sensitive EXIF in plaintext. Prefer random filenames and directories. The vault must function offline.

- Use internal app-private storage; use noBackupFilesDir for files that must be excluded from Android cloud/device transfer backup.
- Configure Android backup/data-extraction rules to exclude vault files, wrapped keys, thumbnails, database files containing sensitive metadata and caches. Verify on supported Android versions.
- Do not publish media to MediaStore or shared external storage. Do not grant broad photo/video permissions to read the user gallery.
- Keep encrypted thumbnails or generate transient thumbnails in memory; never write plaintext thumbnails to disk.
- Use atomic rename/finalization and a startup recovery journal for incomplete recording/encryption.

## 7. Account and backend architecture

Account is optional. Local capture and vault functions do not call the backend. An account is required only for remote private sharing and any feature that inherently needs server-side entitlement or device identity. Prefer passkeys/Android Credential Manager or an established authentication flow; do not store user passwords in application tables.

| Backend component | Data handled | Data explicitly not handled |
| --- | --- | --- |
| Identity/API | Opaque user ID, passkey credential public keys, device public keys/status, account state. | Passwords in plaintext, media, media keys. |
| Permission service | Opaque media reference, owner/recipient IDs, view-only grant, expiry, session limits, revoke status. | Filename, thumbnail, media bytes, content description. |
| Signalling | Temporary session ID, peer negotiation messages, short TTL. | Persistent session recording or media archive. |
| STUN/TURN | Network addresses and transient encrypted packets required for connectivity. | Decrypted media or persistent media files. |
| Billing verification | Product ID, purchase token handled securely, entitlement status and expiry. | Media content. |
| Security audit | Minimal event type, actor/device IDs, timestamp, coarse result and correlation ID. | Media content, titles, thumbnails, intimate metadata. |

## 8. Private sharing technical model

### 8.1 Proposed user-visible model

Owner grants one recipient permission to view one selected item in-app. The source device remains the holder of the original. The recipient does not receive a saved MP4 or exportable file. Sender must remain online for a live session. If the source device is offline, the session is unavailable.

### 8.2 Transport approach

- Prototype WebRTC peer connection for one-to-one remote viewing. Evaluate a video-track approach versus an encrypted chunk/data-channel approach for playback quality, buffering, CPU, battery and resume behaviour.
- Use WebRTC transport encryption and a separately reviewed end-to-end media/device-authentication design. Do not rely on an unauthenticated signalling server to establish peer identity.
- Authenticate device public keys. Use QR code or safety-number comparison as a user-visible out-of-band verification option for initial pairing.
- STUN/TURN services may assist connectivity. TURN is a transient packet relay; configure no recording, no media persistence and minimal logs.
- The backend issues short-lived authorization/session tickets, not media decryption keys. Sender device enforces local owner permission and encrypts outbound content for the authenticated recipient session.
- No cloud fallback. If P2P/TURN setup fails, show a clear connection failure and do not upload/store the video elsewhere.

### 8.3 Grant and revocation semantics

| Control | Expected behaviour | Limit |
| --- | --- | --- |
| Recipient identity | Grant is bound to a recipient account and specific registered device(s). | Compromised/borrowed recipient device remains a risk. |
| View-only | No download/export/share actions in UI or API. | Does not prevent external camera capture or OS compromise. |
| Expiry | New sessions are denied after expiry; active session policy is explicit. | Already displayed/buffered frames may remain visible briefly. |
| Revoke | Owner revokes grant; backend denies new sessions and notifies sender/recipient; sender terminates active stream. | Cannot erase content already seen or captured externally. |
| Session cap | Optional max view sessions enforced server-side and sender-side. | Offline owner device cannot validate a new session. |
| No forwarding | Recipient cannot create a child grant or invite another account. | Cannot control sharing outside the application by external means. |

## 9. Authentication and device identity

- Use passkeys/Android Credential Manager or another reviewed authentication approach; avoid custom password storage.
- Every registered device has a device identity key pair protected by platform secure hardware where available. Public keys are registered with the backend; private keys never leave the device.
- New-device enrolment requires an authenticated approval flow and clear notification. Lost devices can be revoked.
- Biometrics unlock local vault access; they are not a substitute for account authentication or remote device identity.
- Account recovery must not silently expose locally encrypted media. With no cloud backup, remote account recovery does not restore lost local encryption keys.

## 10. Security/privacy controls

| Control | Implementation expectation |
| --- | --- |
| Screen capture | Apply FLAG_SECURE to vault/playback/sharing screens; test Android OS behaviour. State that physical camera and rooted/compromised devices remain outside guarantee. |
| Clipboard/intents | Do not copy media or secrets to clipboard. No external file/share intents for vault media. |
| Logs | Redact account tokens, invitation codes, media IDs where linkable, media names, paths, URLs with secrets and crypto key material. |
| Network | Local-only mode must not send media or media metadata. TLS for APIs; media P2P must use authenticated encryption. |
| SDK policy | No ads; no analytics SDK with media access; dependency allowlist and SBOM. Any crash reporting requires review and must exclude sensitive values. |
| Permissions | Request CAMERA/MIC only when needed. No contacts/location/storage permissions unless future justified feature approved. |
| Rate limits | Rate-limit invitation creation, authentication, device registration, grant creation and session joins. |
| Abuse controls | Block/report recipient account and revoke links/grants; publish abuse contact and response process if remote sharing exists. |

## 11. API surface (later sharing release)

| Method / route | Purpose | Notes |
| --- | --- | --- |
| POST /v1/auth/challenge | Issue one-time auth challenge. | Short TTL; anti-replay. |
| POST /v1/auth/verify | Verify passkey/authenticator assertion. | Rate-limited; no password plaintext. |
| POST /v1/devices | Register a device public key. | Authenticated; device revocation supported. |
| DELETE /v1/devices/{deviceId} | Revoke a registered device. | Terminates/rejects future sessions. |
| POST /v1/invitations | Create single-use invitation. | Store token hash, TTL and intended use only. |
| POST /v1/invitations/{id}/accept | Recipient accepts invitation. | Recipient account/device must pass eligibility checks. |
| POST /v1/share-grants | Create recipient-specific grant. | Owner authorization required; no media upload. |
| DELETE /v1/share-grants/{grantId} | Revoke grant. | Idempotent; notify sender/recipient and stop sessions. |
| POST /v1/share-sessions/authorize | Authorize one viewing session. | Short-lived ticket; owner device must approve/serve. |
| POST /v1/share-sessions/{id}/close | Close viewing session. | Store minimal session outcome. |
| WS /v1/signalling | Exchange temporary connection negotiation. | No media persistence; schema allowlist and short TTL. |
| POST /v1/billing/verify | Verify Play purchase. | Protect purchase token; no token in logs. |
| DELETE /v1/account | Start account deletion. | Revokes grants and deletes account data subject to lawful retention. |

## 12. Error handling and lifecycle

- Interrupted recording: mark as incomplete, clean temporary material safely and offer recover/delete; never show a corrupted file as complete.
- Low storage: warn before capture where possible; stop recording gracefully; preserve already-finalized items.
- Keystore key invalidated: do not silently regenerate and pretend old files are recoverable; show clear data-loss explanation and support only documented recovery.
- App backgrounded/locked: stop or pause capture according to explicit product policy; never continue hidden capture. Lock vault on timeout and sensitive lifecycle transitions.
- Network unavailable: local features remain fully available; remote viewing shows unavailable and does not cloud-fallback.
- Revocation while viewing: sender stops stream and session key use; recipient UI returns to expired/revoked state.

## 13. Test strategy

| Test layer | Examples |
| --- | --- |
| Unit | Crypto file header parsing, nonce uniqueness, key wrapping, expiry logic, permission transitions, metadata sanitization. |
| Instrumentation | Camera permission flows, visible foreground capture, biometric lock, backup exclusion, FLAG_SECURE, app lifecycle. |
| Storage/security | Inspect app data, gallery indexing, media files, thumbnails, temp files, backup/restore output and logs. |
| Network | Packet capture in local-only mode; ensure no media egress. Sharing mode: verify TURN sees encrypted packets only and no persistent media object is created. |
| Abuse/security | Invitation replay, brute-force, cross-account IDOR, revoked devices, recipient mismatch, expiry races, session hijack attempts. |
| Compatibility | Different Android versions, OEM camera behaviours, storage pressure, battery saver, Wi-Fi/mobile networks, NAT/TURN conditions. |
| External review | Independent mobile application security and cryptography review before production sharing. |

## 14. Deployment and operations

- Backend runs as a small API service with PostgreSQL metadata store; no object storage/bucket for media.
- Signalling service keeps ephemeral state with strict TTL. TURN service uses no recording and minimal operational logs.
- Secrets stored in server secret manager; least-privilege service identities; TLS and database encryption at rest.
- Separate dev/staging/prod environments. No production user media in test fixtures.
- Database backups may contain account metadata only; backup retention and deletion implications are documented.
- Monitoring may track availability, errors and abuse rates, but not media payloads or content descriptions.

## 15. Decisions still requiring proof or review

1. Select and review the exact chunk-encryption format/library.
2. Decide biometric-bound key invalidation and loss-recovery UX.
3. Prove encrypted local recording without persistent plaintext temp artifacts.
4. Prototype WebRTC live viewing and compare track/data-channel approaches.
5. Select authenticated device-key exchange and identity verification protocol; do not invent one.
6. Confirm account/passkey implementation and age-assurance needs with counsel.
7. Obtain Indian legal and Google Play policy review for the actual build, store listing and target audience.

## References

- [Google Play Developer Program Policies (current policy page; includes restricted sexual content and child safety)](https://support.google.com/googleplay/android-developer/answer/18265346?hl=en)
- [Google Play account deletion requirements](https://support.google.com/googleplay/android-developer/answer/13327111?hl=en)
- [Google Play User Data policy](https://support.google.com/googleplay/android-developer/answer/10144311?hl=en)
- [Digital Personal Data Protection Rules, 2025 - official MeitY notification PDF](https://www.meity.gov.in/static/uploads/2025/11/53450e6e5dc0bfa85ebd78686cadad39.pdf)
- [DPDP Act commencement notification - Gazette of India, G.S.R. 843(E), 13 November 2025](https://egazette.gov.in/WriteReadData/2025/267647.pdf)
