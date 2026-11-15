# PRIVACY POLICY: PRIVATEVAULT

**Last Updated:** November 15, 2026  
**Effective Date:** November 15, 2026  
**Application Name:** PrivateVault (Secure Personal Camera & Private Vault)  
**Applicable Legal Frameworks:** India Digital Personal Data Protection (DPDP) Act 2023, Information Technology Act 2000 & Reasonable Security Practices Rules (Section 43A), Google Play Developer Program Policies (User Data & Privacy).

---

## 1. Core Privacy Philosophy & Architectural Guarantees

PrivateVault is engineered from the ground up on the principle of **Zero-Knowledge Architecture** and **Local-First Sovereign Storage**. 

Our primary operational sequence is:  
**Capture → Encrypt → Store → Control Access → Share Privately → Revoke Access → Delete**

### Fundamental Assurances:
1. **Zero Cloud Media Backups:** PrivateVault does NOT maintain or operate cloud storage servers (e.g. AWS S3, Google Cloud Storage, Firebase Storage) for user media. All photos and videos captured inside the application remain strictly on your physical device.
2. **Exclusion from OS Cloud Sync:** PrivateVault explicitly configures Android Auto Backup and Google Drive device backup exclusions (`android:allowBackup="false"`, `dataExtractionRules`, and `fullBackupContent`), preventing your encrypted media vault or cryptographic keys from syncing to Google Cloud.
3. **No Advertising or Surveillance SDKs:** The application contains ZERO third-party advertising SDKs, ad networks, behavioural tracking libraries, or analytics SDKs that inspect or parse user media.
4. **Isolated Private Storage:** All media files are saved in application-sandboxed private internal storage (`Context.getFilesDir()`). Media is never written to the public `MediaStore` or standard Gallery directories.

---

## 2. Information We Process & Data Minimization

To provide security and optional peer-to-peer sharing, PrivateVault processes only the minimum data strictly required:

| Data Type | Storage Location | Processing Purpose | Third-Party Disclosure |
| :--- | :--- | :--- | :--- |
| **Captured Photos & Videos** | Local internal device memory only | Encrypted viewing & organization | Never transmitted or stored on cloud servers |
| **Media Encryption Keys (DEKs)** | Local internal Room database wrapped by Android Keystore | AES-256-GCM hardware-backed encryption | Never exported or transmitted |
| **Biometric Credentials** | Local Android BiometricPrompt hardware abstraction layer | Application unlock gate | Never accessible to app code or stored |
| **Anonymous Account Identifier** | Local DataStore / Minimal Signalling Database | P2P routing and subscription entitlement verification | Used only for signaling handshake |
| **Device Fingerprint** | Local DataStore | Unique device authorization | Never sold or linked to personal identities |
| **Sharing Permissions** | Local Room database & signaling records | Expiration & session access control | Shared only between sender and designated recipient |

---

## 3. Cryptographic Architecture & Technical Security Safeguards

PrivateVault implements robust, industry-standard cryptographic primitives complying with NIST guidelines:

1. **Hardware-Backed Master Key:** Master keys are generated directly inside the **Android Keystore System** (`AndroidKeyStore`) with key isolation backed by hardware StrongBox / TEE (Trusted Execution Environment).
2. **Envelope Encryption:** Every captured photo and video generates a unique, cryptographically random 256-bit Data Encryption Key (DEK). The DEK is encrypted (wrapped) using the Keystore Master Key via AES-256-GCM.
3. **Authenticated Streaming Encryption:** Media files are encrypted with authenticated AES-GCM ciphers, verifying file integrity and preventing unauthorized tampering or bit-flipping attacks.
4. **Secure In-Memory Decryption:** During playback, video streams are decrypted on-the-fly directly into RAM buffers via custom Media3 `EncryptedMediaDataSource`. Decrypted plaintext files are NEVER written to flash storage or disk caches.
5. **Secure Cryptographic Erasure:** Temporary capture files are overwritten with pseudo-random zeros across their allocated byte footprint before unlinking from the filesystem.
6. **Screen Protection (FLAG_SECURE):** Sensitive UI screens (Camera, Vault, Media Viewer, Lock Screen) enforce Android's `WindowManager.LayoutParams.FLAG_SECURE`, preventing standard screenshots, recent task thumbnail caching, and screen-recording captures.

---

## 4. Controlled Private Sharing & Peer-to-Peer Transmission

When a user initiates Controlled Private Sharing:
- **No Permanent Server Storage:** Videos and photos are NOT uploaded to our servers. Transmission occurs directly via peer-to-peer streaming or temporary encrypted transit.
- **Ephemeral ECDH Forward Secrecy:** Devices negotiate a temporary session key using Elliptic Curve Diffie-Hellman (NIST P-256). The session key is ephemeral and discarded once the viewing session ends.
- **Recipient Restrictions:** The recipient application enforces view-only controls: no in-app export, no downloading, no media forwarding, and active `FLAG_SECURE` protection.
- **Instant Revocation:** Senders maintain sovereign control and may revoke access permissions at any moment with immediate effect.

---

## 5. Age Requirement & Adult Eligibility

PrivateVault is strictly intended for consenting adults aged **18 years and older** (or the age of legal majority in your jurisdiction). The application enforces an onboarding confirmation of adult eligibility. The app does not knowingly collect information from or market to minors under 18.

---

## 6. User Rights Under the DPDP Act 2023 & Indian IT Act

Users located in India and applicable jurisdictions possess the following sovereign rights:
1. **Right to Complete Erasure (Right to be Forgotten):** Users may permanently delete their account and invoke one-tap secure cryptographic shredding of all local vault contents from within **Settings → Account → Delete Account Permanently**.
2. **Right to Access & Portability:** All media items remain directly accessible to the user on their own device.
3. **Right to Withdraw Consent:** Consent for sharing permissions can be instantly revoked on an item-by-item basis.

---

## 7. Security Limitations & Disclaimer

While PrivateVault employs state-of-the-art software and hardware protections:
- No mobile operating system can physically prevent a third party from photographing a screen using an external physical camera.
- Compromised, rooted, or tampered operating systems may undermine platform-level security assurances.
- Users are advised to only share media with consenting, trusted individuals.

---

## 8. Contact & Grievance Redressal

In compliance with the Information Technology (Intermediary Guidelines and Digital Media Ethics Code) Rules 2021 and DPDP Act 2023, the Grievance Officer can be contacted at:  
**Grievance Officer:** Privacy & Security Legal Team  
**Email:** `privacy@privatevault.internal`  
**Address:** Data Protection Office, PrivateVault Security Group
