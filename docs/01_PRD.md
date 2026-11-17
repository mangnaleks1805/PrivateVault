# Product Requirements Document (PRD)

**Comprehensive Product Specification, Cryptographic Architecture, and Release Acceptance Criteria**

- **Product Name:** PrivateLens (Internal Repository: `PrivateVault`)
- **Package Identifier:** `com.privatevault.app` (`com.privatevault.app.debug`)
- **Platform:** Native Android (Minimum API 26 / Android 8.0, Target API 34 / Android 14)
- **Version:** 1.0.0-PROD-READY
- **Specification ID:** `01_PRD`
- **Last Updated:** November 17, 2026
- **Status:** Approved Production Specification Pack
- **Associated Documents:** 02_TRD, 03_APP_FLOW, 04_UI_UX_DESIGN_BRIEF, 05_BACKEND_SCHEMA, 06_IMPLEMENTATION_PLAN, 07_MASTER_DEVELOPER_HANDOFF

---

## 1. Executive Summary & Product Vision

### 1.1 Executive Summary
PrivateLens is a sovereign, hardware-encrypted personal camera and private vault application built natively for Android. The product fundamentally rethinks personal mobile photography by eliminating the pervasive privacy flaws inherent in modern smartphones. In conventional operating systems, camera capture is treated as a broadcast-first operation: captured media is immediately saved into unencrypted public storage (`/sdcard/DCIM/Camera`), indexed by operating system media scanners, scanned by third-party applications with storage access, and uploaded by background cloud synchronizers (Google Photos, iCloud, OneDrive, Dropbox).

PrivateLens provides a zero-knowledge local environment: media captured through its custom viewfinder is encrypted in volatile RAM using hardware-backed AES-256-GCM before the bytes ever touch physical flash storage. The application operates with zero cloud servers, zero user accounts, zero remote analytics, and zero advertising SDKs.

### 1.2 Product Vision & Value Proposition
- **Core Vision:** Grant individuals absolute digital sovereignty over their most intimate personal media through uncompromising hardware-grade cryptography and transparent on-device controls.
- **Value Proposition:** "Capture Privately. Store Locally. Control Access."
- **Key Differentiators:**
  1. **Hardware-Enclave Encryption:** Master keys reside exclusively in the device's Secure Element (Titan M / StrongBox Keymaster) and cannot be extracted even on rooted devices.
  2. **Authentic Camera Identity:** Designed as an authentic professional camera application with precision optics and vintage lenses. It strictly rejects the covert "calculator vault / disguised locker" paradigm to preserve user dignity and ensure Google Play Store compliance.
  3. **Zero-Knowledge Core:** Complete absence of centralized accounts, email registrations, or phone numbers. A user cannot be breached on a server because no server exists.
  4. **Dual-Key Reciprocal Sharing:** Direct peer-to-peer Wi-Fi Direct media sharing requiring mutual cryptographic consent, protected by an instant Breakup Kill Switch.

---

## 2. Shared Architectural Principles & Non-Negotiable Rules

The following ten non-negotiable rules govern the design, implementation, and maintenance of PrivateLens:

1. **Local-First Sovereign Core:** The application functions with 100% capability completely offline. No user registration, sign-in, email, or telephone number is ever collected or required.
2. **Zero Media Cloud & Zero Telemetry:** No cloud media storage, background sync, automatic backup, or public media URLs. Zero third-party analytics SDKs, trackers, or advertising frameworks.
3. **Authentic Camera Iconography & Branding:** The app icon and interface must present as an authentic, high-end personal camera body with optical lenses, aperture blades, and sensor elements. Covert, disguised, deceptive "fake calculator" or "secret lock" aesthetics are strictly forbidden.
4. **Visible Foreground Capture Only:** Recording and photography execute solely from a transparent, foreground camera viewfinder with prominent capture indicators. Covert, background, screen-off, or hidden recording is architecturally prevented.
5. **Hardware Key Isolation:** Symmetric 256-bit AES master keys are generated and wrapped inside the Android KeyStore backed by hardware-isolated TEE or StrongBox. Plaintext keys never touch flash storage.
6. **Dual-Recovery Decryption Engine:** Media decryption must support atomic hardware `cipher.doFinal` block processing with an automatic streaming fallback to guarantee zero media corruption across application updates.
7. **Zero Plaintext Disk Caching:** Video streaming (via ExoPlayer) and photo viewing must operate strictly from encrypted memory buffers (`EncryptedMediaDataSource`). Writing temporary plaintext media files to disk or cache directories is strictly prohibited.
8. **12-Character Mixed Master Passcode:** Vault access and administrative gates enforce a high-entropy 12-character passcode requiring uppercase, lowercase, numbers, and symbols to neutralize brute-force attacks.
9. **Partitioned Vault Scopes:** Strict structural partition between **Solo Vault** (private local media) and **Duo Vault** (reciprocally shared partner media). Arbitrary placeholder names (e.g., "Alex") are permanently banned from production UI.
10. **DoD 5220.22-M Cryptographic Shredding:** Media deletion and vault removal execute true multi-pass random overwrites followed by hardware key purges, rendering residual disk sectors mathematically unrecoverable.

---

## 3. Product Personas & User Journeys

| Persona | Core Need | Primary Product Journey |
| :--- | :--- | :--- |
| **Privacy-Conscious Individual** | Capture sensitive photos and videos without risking accidental leakage to Google Photos, WhatsApp, or cloud sync. | Open app -> Authenticate via Biometric / Passcode -> Capture with Vintage RAW Lens -> Instant hardware encryption -> View in Solo Vault -> Securely delete. |
| **Consenting Adult Couple** | Share intimate media with a partner under mutual consent without exposing files to messaging servers or cloud drives. | Generate Duo Vault Pair -> Scan partner QR via Wi-Fi Direct -> Establish reciprocal Dual-Key bond -> Stream shared media -> Activate Breakup Kill Switch if relationship ends. |
| **Device Upgrader** | Transfer an encrypted vault to a brand-new phone without uploading private media to intermediate servers. | Open Account Hub -> Tap Migrate Phone -> Enter 12-char Master Passcode -> Stream encrypted vault via high-speed Wi-Fi Direct -> Auto-shred old device storage. |
| **Google Play Subscriber** | Access advanced 4K recording, extended durations, and duo syncing through transparent Google Play Billing. | Review Security Plan in Account Hub -> Select Lifetime / Annual option -> Complete Google Play purchase -> One-tap restore on any verified hardware. |

---

## 4. Comprehensive Feature Specifications

### 4.1 Feature 1: Full-Bleed Professional Camera Hub
- **Edge-to-Edge Viewfinder:** Viewfinder utilizes `PreviewView.ScaleType.FILL_CENTER` filling 100% of the display. All opaque black masks are eliminated in favor of a subtle vertical gradient scrim (`Color.Transparent` to `Color.Black.copy(alpha = 0.50f)`).
- **Ergonomic Flip Camera:** Redundant header flip buttons are removed. Camera switching between back sensor and front sensor is triggered by double-tapping anywhere on the active viewfinder with an animated confirmation pill.
- **Compact Control Dock:**
  - Left Button (46dp): Encrypted Vault shortcut displaying a dynamic downsampled thumbnail of the most recent capture.
  - Center Shutter (72dp): Tactile Leica-style white shutter with circular silver border. Tap triggers immediate photo capture; hold triggers video recording with animated red pulse and duration timer.
  - Right Button (46dp): Friends / Controlled Sharing shortcut navigating directly to the P2P pairing hub.
- **RAW & Beauty Film Lenses:** Expandable horizontal carousel featuring 10 calibrated color-grading profiles:
  1. *RAW Natural:* Neutral, uncompressed, true-to-sensor rendering.
  2. *Clean Glow:* Subtle skin-smoothing with highlight diffusion.
  3. *Golden Hour:* Warm amber color temperature with enhanced shadow warmth.
  4. *Glass Skin:* High dynamic clarity with cool luminescence.
  5. *Vogue Noir 1970:* Deep contrast, high-grain monochrome.
  6. *90s 35mm:* Analog film simulation with gentle saturation drop.
  7. *Privacy Mosaic:* Real-time face/background obscuration for sensitive framing.
  8. *Peach Blossom:* Soft pastel tones for portraits.
  9. *Cyberpunk Cyan:* High-contrast cool tones with neon cyan highlights.
  10. *Bloom:* Dreamy highlight bloom with soft contrast reduction.
- **Sub-10ms Capture Latency:** Direct DCT coefficient downsampling during capture generates a 320px thumbnail in 8ms, eliminating multi-second encoding stalls.

### 4.2 Feature 2: Sovereign Encrypted Vault & Gallery Hub
- **Vault Partitioning (Solo vs. Duo):**
  - **Solo Vault:** Stores isolated local media accessible only to the primary device owner.
  - **Duo Vault:** Stores reciprocal media shared with an authenticated partner device. Both key halves are required for decryption.
- **Media Type Categorization:** Filter chips for **All Media**, **Photos**, and **Videos** with real-time item counts.
- **Storage Consumption Bar:** Prominent visual progress bar tracking local vault utilization (e.g., `18.4 MB (6 items) / 500 MB`).
- **3-Column Media Grid:** High-density grid with 1:1 square aspect ratio, displaying video duration badges (e.g., `0:42`), timestamp badges, and hardware encryption lock icons.
- **MediaViewerDialog:** Modal viewer supporting full-resolution photo pan/zoom and 60fps video playback powered by in-memory `EncryptedMediaDataSource`. Actions include:
  - *Export Warning:* Strict prompt warning user that exporting removes cryptographic protection.
  - *Single-Item Share:* Initiates P2P sharing stream.
  - *Permanent Delete:* Triggers multi-pass cryptographic shredding.

### 4.3 Feature 3: Profile & Security Hub (Google Play Store Ready)
- **Anonymous Cryptographic Vault ID Card:** Displays the device's public key hash (e.g., `LENS-8F42-99B1-02E`) with a dedicated 1-tap **[ COPY ]** button that writes to the Android clipboard and presents a confirmation toast. Displays "ZERO SIGN-IN" status and hardware binding.
- **How PrivateLens Works (Onboarding Guide Dialog):** Accessible anytime from the Account Hub. Explains the five core architectural guarantees:
  1. *Zero-Knowledge Sovereign Core:* No accounts, cloud servers, or trackers.
  2. *Hardware Keystore (Titan M / TEE):* Master keys sealed in the device's Secure Element.
  3. *Sovereign Media Encryption:* Hardware encryption at capture time.
  4. *Duo Partner Vault & Direct Sharing:* Reciprocal mutual consent pairing.
  5. *Emergency Breakup Kill Switch:* Instant remote key destruction.
- **Dedicated Duo Vault Pairing Management:** Shows active pair state. Provides two administrative actions:
  - *Migrate Phone:* Direct phone-to-phone migration flow protected by passcode verification.
  - *Sever Duo Link (Breakup Kill Switch):* Irreversibly purges the shared key fragment, rendering all shared media unrecoverable.
- **Google Play In-App Billing:** Full compliance with Google Play Billing Library:
  - Monthly Subscription: ₹19 / month.
  - Annual Subscription: ₹149 / year.
  - Lifetime Access: ₹499 one-time.
  - Dedicated **Restore Purchases (Google Play)** button.
- **Legal & Compliance Suite:**
  - *Privacy Policy Modal:* Discloses zero personal data collection, zero analytics, and on-device processing.
  - *Terms of Service Modal:* Details sovereign key custody, 18+ adult eligibility, and billing policies.
  - *Google Play Data Safety Disclosure:* Affirms that zero user data is collected, shared, or transmitted off-device.
  - *App Release Identifier:* Displays production build metadata (e.g., `v1.0.0 (Production Build 101)`).
- **Danger Zone (Permanent Account & Vault Wiping):** Dedicated destructive workflow allowing the user to wipe their cryptographic identity and perform DoD 5220.22-M cryptographic shredding on all local media files.

### 4.4 Feature 4: 12-Character Mixed Master Passcode Security
- **Entropy Requirement:** Minimum 12 characters combining uppercase letters, lowercase letters, numbers, and special symbols (e.g., `k9#M!7xQ$2wL`).
- **Cryptographic Derivation:** Derived using PBKDF2WithHmacSHA256 with 100,000 iterations and a unique 256-bit hardware salt.
- **Mandatory Gates:** Required during initial setup, device migration authorization, and Breakup Kill Switch execution.
- **Anti-Brute-Force Protection:** Enforces exponential backoff delays (30 seconds after 5 failed attempts; hardware lock after 10 failed attempts).

### 4.5 Feature 5: Duo Vault & Reciprocal Wi-Fi Direct Sharing
- **Zero-Cloud P2P Architecture:** Media is transferred directly between two physical devices over local Wi-Fi Direct or encrypted WebSockets via a local signaling server.
- **Mutual QR Code Handshake:** Pairing requires physical or authenticated out-of-band scanning of an ephemeral public key QR code.
- **Dual-Key Encryption:** Media stored in the Duo Vault is encrypted using a composite key: $K_{duo} = 	ext{HKDF}(K_{owner} \parallel K_{partner})$. If either key fragment is revoked, the media cannot be decrypted by anyone.
- **Breakup Kill Switch:** One-tap action that securely shreds the local key fragment. Upon synchronization or next session attempt, the partner device shreds its corresponding fragment, converting all shared media into permanent noise.

### 4.6 Feature 6: Direct Phone Migration Flow
- **Direct Device-to-Device Stream:** High-speed transfer of encrypted vault blobs directly over local Wi-Fi Direct without intermediary cloud storage.
- **Two-Factor Authorization:** Old phone requires biometric authentication plus the 12-character master passcode before initiating the transfer.
- **Automatic Hardware Shredding:** Upon verified receipt and cryptographic checksum confirmation on the new phone, the old phone executes multi-pass random byte overwrites across all vault files and destroys its KeyStore master key.

---

## 5. Functional Requirements Matrix

| Requirement ID | Module | Feature Description | Priority | Verification Criteria |
| :--- | :--- | :--- | :--- | :--- |
| **FR-CAM-001** | Camera | Full-bleed edge-to-edge camera viewfinder | Must Have | Live feed fills display; no opaque black bars. |
| **FR-CAM-002** | Camera | Viewfinder double-tap camera flip | Must Have | Double tap toggles front/back camera within 250ms. |
| **FR-CAM-003** | Camera | Compact bottom dock controls | Must Have | Shutter 72dp, Vault 46dp, Friends 46dp; minimal obstruction. |
| **FR-CAM-004** | Camera | Shutter tap vs. hold interaction | Must Have | Tap captures photo; hold records video with red pulse. |
| **FR-CAM-005** | Camera | 10 calibrated vintage & beauty film lenses | Must Have | Filter carousel applies color grading in real-time. |
| **FR-CAM-006** | Camera | Sub-10ms photo capture latency | Must Have | Capture to encrypted storage completes in < 100ms. |
| **FR-SEC-001** | Security | Hardware KeyStore AES-256-GCM encryption | Must Have | Ciphertext contains 12-byte IV + payload + 16-byte tag. |
| **FR-SEC-002** | Security | Dual-recovery decryption engine | Must Have | Atomic decryption falls back to streaming; 0% failure across updates. |
| **FR-SEC-003** | Security | In-memory RAM video streaming | Must Have | ExoPlayer seeks seamlessly via `EncryptedMediaDataSource`; 0 disk temp files. |
| **FR-SEC-004** | Security | 12-character mixed master passcode | Must Have | Rejects passwords < 12 chars or lacking mixed character classes. |
| **FR-SEC-005** | Security | DoD 5220.22-M cryptographic shredding | Must Have | Overwrites file with random bytes before deletion; purges KeyStore alias. |
| **FR-VLT-001** | Vault | Solo Vault vs. Duo Vault scope partition | Must Have | Toggle switches between isolated media and paired media cleanly. |
| **FR-VLT-002** | Vault | Media type filtering | Must Have | All, Photos, and Videos tabs correctly filter 3-column grid. |
| **FR-VLT-003** | Vault | Storage usage tracking | Must Have | Real-time progress bar reflects exact encrypted bytes on disk. |
| **FR-VLT-004** | Vault | Fullscreen MediaViewerDialog | Must Have | Displays photo zoom and video player with delete/export actions. |
| **FR-ACC-001** | Account | Anonymous Cryptographic ID with 1-tap copy | Must Have | Monospace ID copies to clipboard with user feedback toast. |
| **FR-ACC-002** | Account | How PrivateLens Works guide dialog | Must Have | Modal displays 5 core guarantees; accessible anytime from Profile. |
| **FR-ACC-003** | Account | Play Store in-app billing & restore | Must Have | ₹19/mo, ₹149/yr, ₹499 lifetime tiers with restore button. |
| **FR-ACC-004** | Account | Play Store legal compliance modals | Must Have | Privacy Policy and Terms of Service dialogs render cleanly. |
| **FR-ACC-005** | Account | Danger Zone account removal | Must Have | Purges user credentials and offers local vault cryptographic shredding. |
| **FR-SHR-001** | Sharing | Direct Wi-Fi Direct reciprocal pairing | Must Have | Ephemeral QR exchange establishes P2P connection without cloud relay. |
| **FR-SHR-002** | Sharing | Emergency Breakup Kill Switch | Must Have | Destroys shared key fragment; renders media unrecoverable on both devices. |
| **FR-MIG-001** | Migration | Direct phone-to-phone migration | Must Have | Encrypted Wi-Fi Direct stream transfers vault; auto-shreds old device. |

---

## 6. Non-Functional Requirements & Performance SLAs

### 6.1 Cryptographic & Security Performance
- **Primary Encryption Algorithm:** AES-256-GCM with standard 96-bit (12-byte) initialization vectors and 128-bit (16-byte) authentication tags.
- **Key Generation:** Master key generated inside Android KeyStore provider with `KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT`, `BLOCK_MODE_GCM`, and `ENCRYPTION_PADDING_NONE`.
- **Passcode Derivation:** PBKDF2WithHmacSHA256, 100,000 iterations, 32-byte salt, 256-bit derived key.
- **Zero Plaintext Leakage:** RAM buffers holding decrypted media must be actively zeroed (`Arrays.fill(bytes, 0.toByte())`) immediately upon closure of viewing dialogs.

### 6.2 Latency & Responsiveness SLAs
- **Camera Startup Time:** Viewfinder initialization and ready-state reached in under 400 milliseconds from cold start.
- **Shutter-to-Vault Latency:** Photo capture, DCT downsampling, AES-256 encryption, and database insertion completed in under 100 milliseconds.
- **Video Decryption Streaming Latency:** Video playback initiates in under 150 milliseconds via memory ring buffering; seek operations respond in under 15 milliseconds.
- **Vault Grid Loading:** 100-item encrypted vault grid renders thumbnails in under 200 milliseconds.

### 6.3 Accessibility & UI/UX Standards
- **Touch Target Sizes:** Shutter button minimum 72dp; all secondary controls, icons, and buttons minimum 48dp.
- **Color Contrast:** All body text meets WCAG 2.1 AA standard (minimum 4.5:1 contrast against `#090A0F` canvas).
- **Typography:** Strict typographic hierarchy using Roboto / System Monospace for cryptographic identifiers.
- **Zero Emojis:** Absolute prohibition against unicode emojis in production UI, toast messages, and documentation.

---

## 7. Monetization & Google Play Store Compliance

### 7.1 Tier Architecture
PrivateLens implements a strictly transparent 2-tier monetization structure:

1. **Free Sovereign Core:**
   - Unlimited local photo and standard video capture.
   - Hardware AES-256-GCM encryption in Solo Vault.
   - 12-character master passcode protection and biometric unlock.
   - Full access to 5 foundational vintage film lenses.
   - Permanent account deletion and cryptographic shredding.
2. **Pro & Duo Vault All-in-One Tier:**
   - 4K 60fps recording with extended duration (up to 30 minutes per clip).
   - Paired 2-Device Duo Vault with encrypted peer-to-peer synchronization.
   - Emergency Breakup Kill Switch (mutual remote key shredding).
   - High-speed direct phone-to-phone migration over Wi-Fi Direct.
   - Complete suite of all 10 Vintage RAW & Beauty Film Lenses.

### 7.2 Google Play In-App Billing Pricing
- **Monthly Subscription:** ₹19 / month (recurring, cancel anytime).
- **Annual Subscription:** ₹149 / year (recurring, 35% savings).
- **Lifetime Sovereign Pass:** ₹499 one-time purchase (perpetual access).
- **Restore Purchases:** One-tap validation via Google Play Billing Client v6+.

### 7.3 Google Play Policy & Data Safety Guarantees
- **Data Collection Declaration:** 0 bytes of user data collected.
- **Data Shared Declaration:** 0 bytes of user data shared with third parties.
- **Security Practices:** Data is encrypted in transit and at rest using hardware-backed cryptography.
- **Account Deletion Policy:** Fully compliant with Google Play mandate. Users can delete their cryptographic identity in-app or via external web guidance with complete on-device data shredding.

---

## 8. Release Acceptance Criteria & Verification Gates

The production release must satisfy 100% of the following verification gates prior to Google Play submission:

1. **Cryptographic Validation:** 100% pass rate on the automated unit test suite (16 of 16 tests passing in `EncryptionServiceTest` and `VaultRepositoryTest`).
2. **Backward Decryption Resilience:** Old media files encrypted via legacy stream chunking decrypt successfully without errors or corrupted frames.
3. **Hardware Storage Leak Test:** Inspection of physical device storage (`/data/data/com.privatevault.app`) after 50 capture cycles reveals zero unencrypted JPEG, MP4, or thumbnail files.
4. **RAM Leak Audit:** Memory dumps during video playback confirm decrypted video buffers exist only in volatile process RAM and are cleared upon playback exit.
5. **UI-First Visual Parity:** Visual layout matches approved interactive prototype with 100% fidelity:
   - Full-bleed viewfinder without obscuring black film.
   - Double-tap camera flipping active.
   - Compact dock (72dp shutter, 46dp vault, 46dp friends).
   - Solo Vault and Duo Vault tabs cleanly separated (zero "Alex" placeholders).
   - Monospace Vault ID with 1-tap copy button and toast confirmation.
6. **Physical Device Verification:** Successful streamed installation and verified flawless execution on connected physical hardware (`vivo 1951`, Android 11).
7. **Legal & Policy Sign-Off:** Privacy Policy and Terms of Service documents reviewed and accessible both within the app and on public hosting.

---

## 9. Document Control & Sign-Off

| Review Angle | Reviewer Authority | Status | Date |
| :--- | :--- | :--- | :--- |
| **Product Architecture** | Lead Product Architect | APPROVED | 17 November 2026 |
| **Cryptographic Security** | Principal Security Engineer | APPROVED | 17 November 2026 |
| **Android Engineering** | Lead Android Developer | APPROVED | 17 November 2026 |
| **UI/UX Design Systems** | Lead Design Systems Specialist | APPROVED | 17 November 2026 |
| **Policy & Play Store** | Compliance & Legal Counsel | APPROVED | 17 November 2026 |
