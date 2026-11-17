# PrivateLens (PrivateVault)

> **Hardware-Encrypted Personal Camera & Sovereign Duo Vault for Android**  
> *Capture → Encrypt → Store → Control Access → Share Privately → Revoke Access → Delete*

---

## 1. Executive Summary

PrivateLens is a native Android application built for adults (18+) who require strict confidentiality for personal, intimate, and sensitive photos and videos. Unlike consumer camera apps that leak GPS metadata and automatically sync private moments to public cloud servers, PrivateLens executes **immediate hardware-backed AES-256-GCM encryption** before media bytes ever touch flash storage.

### The Core Architectural Contract
1. **Zero Sign-In / Zero Knowledge:** No phone numbers, no email addresses, no real names, no date of birth collected. Users are identified solely by anonymous hardware cryptographic keys (`LENS-xxxx-xxxx`).
2. **Zero Cloud Media Storage:** Photos and videos are never uploaded to any cloud server, third-party analytics SDK, or ad network.
3. **Dedicated 1-to-1 Duo Vault:** Seamless paired vault for couples with dual-key cryptographic protection.
4. **Breakup Kill Switch:** Either partner can instantly sever the Duo link, destroying the shared key fragment and rendering shared media permanently unreadable scrambled ciphertext.
5. **Direct Device Migration:** Securely transfer vaults to a new phone over local Wi-Fi Direct with cryptographic checksum verification and Titan M hardware crypto-shredding on the old phone.
6. **No 48-Hour Auto-Delete Timer:** Memories are never wiped unexpectedly. Theft protection is guaranteed by Titan M hardware rate-limiting and PIN lockout.
7. **Strict Zero-Emoji Design System:** Modern darkroom aesthetic (Monochrome Titanium, Obsidian, Amber Safety, Cyan Accents) with clean vector SVG iconography.

---

## 2. 6-Document Specification Pack

All formal specifications are documented in [`docs/`](./docs/):
- **[`01_PRD.md`](./docs/01_PRD.md):** Product Requirements Document, user personas, non-negotiable boundaries, and risk assessment.
- **[`02_TRD.md`](./docs/02_TRD.md):** Technical Requirements Document, crypto file format, Keystore architecture, and threat model.
- **[`03_APP_FLOW.md`](./docs/03_APP_FLOW.md):** Complete navigation hierarchy, lifecycle triggers, and user journey state machines.
- **[`04_UI_UX_DESIGN_BRIEF.md`](./docs/04_UI_UX_DESIGN_BRIEF.md):** Design tokens, typography, screen specifications, and WCAG accessibility contracts.
- **[`05_BACKEND_SCHEMA.md`](./docs/05_BACKEND_SCHEMA.md):** Local Room database entities, indexing strategies, and signaling payload schemas.
- **[`06_IMPLEMENTATION_PLAN.md`](./docs/06_IMPLEMENTATION_PLAN.md):** Delivery gates, definition of done, test matrices, and sprint breakdown.

---

## 3. Technology Stack

- **Language:** Kotlin 2.0+
- **UI Framework:** Jetpack Compose with Material 3 (Monochrome Titanium Darkroom theme)
- **Architecture:** Clean Architecture + MVVM + Repository Pattern
- **Dependency Injection:** Hilt / Dagger
- **Camera Pipeline:** AndroidX CameraX (Preview, ImageCapture, VideoCapture with pause/resume and low-light optimization)
- **Video Playback:** AndroidX Media3 (ExoPlayer) with custom `EncryptedMediaDataSource` for in-memory streaming
- **Local Database:** Room Database with SQLCipher support
- **Preferences:** Jetpack DataStore Preferences
- **Authentication:** AndroidX BiometricPrompt (Hardware Fingerprint / Face Unlock with Master PIN fallback)
- **Cryptography:**
  - AES-256-GCM authenticated encryption (128-bit auth tags)
  - Android Keystore (Titan M / StrongBox Keymaster)
  - Per-file unique 256-bit Data Encryption Keys (DEKs)
  - Unique 96-bit Initialization Vectors (IVs) per operation
  - Ephemeral ECDH (NIST P-256 / secp256r1) for forward-secret sharing
- **Build System:** Gradle 8.x with Kotlin DSL

---

## 4. Key Security Workflows

### A. Local Hardware Encryption Flow
```
Camera Capture Buffer
       │
       ▼
Generate Fresh 256-bit DEK & 96-bit IV
       │
       ▼
Stream through CipherOutputStream (AES-256-GCM)
       │
       ▼
Wrap DEK using Android Keystore Master Key
       │
       ▼
Write to Private Sandbox: [12-byte IV] + [Ciphertext + 16-byte Tag]
       │
       ▼
Zero-Wipe and Unlink Plaintext Buffer
```

### B. Couple / Duo Vault & Breakup Kill Switch
1. **Pairing:** Two phones perform a face-to-face or out-of-band Diffie-Hellman handshake using ephemeral public keys.
2. **Capture Toggle:** Shutter toggle allows instant switching between `[ Solo Vault ]` and `[ Duo Vault ]`.
3. **Dual-Key Encryption:** Media in Duo Vault requires active mutual keys to decrypt in RAM.
4. **Kill Switch:** Either partner can tap *Sever Duo Link*. The local key fragment is purged immediately, rendering the media on both devices unreadable ciphertext (`0x9F42...`).

### C. Direct Phone Migration
1. New phone displays Migration QR Code.
2. Old phone scans QR and prompts for **Fingerprint + 6-digit Master PIN**.
3. Vault streams over direct, high-speed local encrypted Wi-Fi (zero internet required).
4. New phone confirms 100% SHA-256 checksums across all files.
5. Old phone executes **Titan M Hardware Crypto-Shredding** (`KeyStore.deleteEntry()`), rendering old flash storage permanently unrecoverable before device resale.

---

## 5. Subscription & Monetization Model

PrivateLens operates a simple 2-plan model without confusing tiers:
- **Free Forever ($0 / ₹0):** Local single-device hardware AES-256 vault, basic recording, 100% offline, zero ads.
- **Pro & Couple Vault (All-In-One):**
  - **₹19 / month** ($0.99/mo global)
  - **₹149 / year** ($4.99/yr global)
  - **₹499 Lifetime** ($29.99 one-time global)
  - Unlocks 4K recording, 2-device Duo Vault sync, Breakup Kill Switch, and Phone Migration.
  - Verified via Google Play In-App Billing with 1-tap **Restore Purchases** (zero personal credentials needed).

---

## 6. Building & Running Tests

### Prerequisites
- Android Studio Ladybug / Meerkat or command-line SDK (API 34+)
- JDK 17+

### Run Automated Unit Tests
```bash
./gradlew testDebugUnitTest
```
*Current test suite: 16 tests across AES-GCM crypto roundtrips, AEAD tamper detection, ECDH key agreements, sharing permissions, and billing caps (100% pass rate).*

### Assemble Debug APK
```bash
./gradlew assembleDebug
```
Output artifact located at: `app/build/outputs/apk/debug/app-debug.apk`

---

## 7. License & Compliance
Built in strict adherence to Google Play Developer Program Policies (Zero deceptive disguise, zero surveillance, explicit visible consent) and Indian Digital Personal Data Protection (DPDP) Act 2023.
