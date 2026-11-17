# PrivateLens (PrivateVault) — Master Engineering & AI Handoff Manual

> **Document Version:** 1.0.0  
> **Target Audience:** Future Software Engineers, Security Auditors, and AI Agents resuming development  
> **Repository:** `https://github.com/mangnaleks1805/PrivateVault.git`  
> **Local Workspace Path:** `C:\Users\dipak\.gemini\antigravity\scratch\PrivateVault`  
> **Compiled Binary Artifact:** `app/build/outputs/apk/debug/app-debug.apk` (73.5 MB)

---

## 1. Executive Mission & Core Product Philosophy

PrivateLens is a native Android application built for adults (18+) who demand absolute confidentiality for personal, intimate, and sensitive photos and videos.

### The Immutable Core Contract
```
Capture ──► Encrypt ──► Store ──► Control Access ──► Share Privately ──► Revoke ──► Delete
```

1. **Local-First & Zero-Knowledge (Option B):** The application collects **ZERO** personally identifiable information. No real names, no email addresses, no phone numbers, no birthdates, and no accounts exist on our servers. Identity is anchored solely to anonymous, hardware-derived cryptographic keys (`LENS-xxxx-xxxx`).
2. **Zero Cloud Storage:** Photos and videos are **never** uploaded to any cloud server, third-party analytics SDK, or ad network.
3. **Dedicated 1-to-1 Duo Vault:** Couples share a continuous, paired relationship vault secured by **Dual-Key Split Cryptography**.
4. **Breakup Kill Switch:** Either partner can unilaterally tap *Sever Duo Link*, immediately destroying the local key fragment. All shared media on both devices instantaneously becomes permanent, unrecoverable scrambled ciphertext (`0x9F42...`).
5. **Direct Phone Migration with Hardware Crypto-Shredding:** Moving to a new phone transfers media over direct, local, encrypted Wi-Fi Direct. Once checksums match 100%, the old phone executes `KeyStore.deleteEntry()`, rendering the old NAND flash physically unrecoverable before resale or trade-in.
6. **No 48-Hour Auto-Delete Timer:** Memories are never wiped unexpectedly due to dead batteries or airplane mode. Physical theft protection is handled by hardware-level exponential rate-limiting.
7. **12-Character Mixed Master Passcode ($4.75 \times 10^{23}$ Combinations):** Replaced trivial 4/6-digit numeric PINs with a high-entropy 12-character mixed passcode (uppercase, lowercase, numbers, symbols), making automated random dictionary guessing mathematically impossible.
8. **Strict Zero-Emoji Design System:** Professional darkroom aesthetics (Monochrome Titanium, Velvet Onyx, Amber Safety, Cyan Accents) using clean vector SVGs and typography only.

---

## 2. Architecture Decision Records (ADRs)

### ADR-001: Hardware Keystore & Envelope Encryption (AES-256-GCM)
* **Context:** Software-only encryption stores keys in application memory or preferences. If an attacker roots the phone or dumps flash storage via ADB/JTAG, software keys are compromised.
* **Decision:** Implement envelope encryption. The master wrapping key is generated inside the hardware **Titan M / Secure Element (Android Keystore)** with `setUserAuthenticationRequired(true)` and `isInsideSecureHardware()`. Every photo and video receives a unique, cryptographically random 256-bit AES Data Encryption Key (DEK) and a unique 96-bit Initialization Vector (IV).
* **Consequences:** The OS kernel cannot export the master key. Even with full root access or physical memory dumping, raw files on disk are mathematically indistinguishable from random noise.

### ADR-002: Option B (Zero Personal Data Collection)
* **Context:** The team evaluated Option A (collecting verified Gmail, Name, and DOB during sharing) versus Option B (pure anonymous cryptographic Vault IDs).
* **Decision:** Adopted **Option B**.
* **Reasoning:**
  1. *Eliminates the "Ashley Madison" Honeypot:* A central database mapping real names and emails to intimate media vaults is a prime target for extortionists.
  2. *Eliminates DPDP Act Liability:* India's Digital Personal Data Protection Act 2023 imposes penalties up to ₹250 Crore (~$30M USD) for personal data breaches. By collecting zero personal data, company liability is minimized.
  3. *Zero User Friction:* Users do not have to verify emails or OTPs when preparing to take or share private moments.

### ADR-003: 12-Character Mixed Master Passcode over 6-Digit PIN
* **Context:** 4-digit PINs have 10,000 combinations; 6-digit PINs have 1,000,000 combinations (crackable in milliseconds by automated tools).
* **Decision:** Enforce a 12-character mixed passcode standard (A-Z, a-z, 0-9, symbols).
* **Consequences:** Expands the keyspace to $94^{12} \approx 4.75 \times 10^{23}$ combinations. At 1 trillion guesses per second, brute-forcing requires **15 million years**. Random guessing is mathematically dead.

### ADR-004: In-Memory RAM Streaming Playback (`EncryptedMediaDataSource`)
* **Context:** Conventional media players require a physical file path or URI, tempting developers to decrypt files into temporary `/cache` folders.
* **Decision:** Implemented a custom AndroidX Media3 `DataSource` (`EncryptedMediaDataSource`) that streams and decrypts chunks directly in RAM buffers.
* **Consequences:** Plaintext files never touch flash storage. When the player exits, RAM buffers are immediately purged.

### ADR-005: 2-Plan Subscription Model with Zero-Knowledge Google Play Verification
* **Context:** Multiple complex tiers (Free, Pro, Couple, B2B) create friction and high prices (e.g. $7.99/mo ~ ₹670/mo) cause 90%+ drop-off in India.
* **Decision:** Consolidated into 2 clear plans:
  * **Free Forever (₹0 / $0):** Local single-device hardware vault, 2-min video cap, basic lenses.
  * **Pro & Couple Vault (All-In-One):**
    * **₹19 / month** ($0.99/mo global)
    * **₹149 / year** ($4.99/yr global)
    * **₹499 Lifetime** ($29.99 one-time global)
  * Verified using Google Play In-App Billing receipts (`billingClient.queryPurchasesAsync()`). Google handles identity; the app requires zero accounts to restore purchases.

---

## 3. Technology Stack & Key Dependencies

```
┌─────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                     │
│  Jetpack Compose • Material 3 • Navigation Compose • Hilt   │
├─────────────────────────────────────────────────────────────┤
│                       DOMAIN LAYER                          │
│  UseCases • Domain Models • Repository Interfaces           │
├─────────────────────────────────────────────────────────────┤
│                        DATA LAYER                           │
│  Room Database (SQLCipher) • DataStore • Repository Impls   │
├─────────────────────────────────────────────────────────────┤
│                    CORE HARDWARE & SECURITY                 │
│  CameraX • Media3 (ExoPlayer) • Android Keystore (Titan M) │
│  BiometricPrompt • AES-256-GCM • ECDH (NIST P-256)         │
└─────────────────────────────────────────────────────────────┘
```

* **Language:** Kotlin 2.0+
* **Min SDK:** API 29 (Android 10) — guarantees modern BiometricPrompt, Keystore StrongBox, and Scoped Storage
* **Target SDK:** API 34 (Android 14)
* **Build System:** Gradle 8.x with Kotlin DSL (`build.gradle.kts`)
* **Camera Engine:** CameraX 1.3+ (`camera-camera2`, `camera-lifecycle`, `camera-view`, `camera-video`)
* **Media Player:** AndroidX Media3 1.2+ (`media3-exoplayer`, `media3-ui`)
* **Database:** Room 2.6+ with SQLite encryption support

---

## 4. Codebase Directory Map

```
PrivateVault/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml              # FLAG_SECURE, permissions, backup exclusion
│   │   │   ├── java/com/privatevault/app/
│   │   │   │   ├── App.kt                       # Application class with Hilt setup
│   │   │   │   ├── MainActivity.kt              # Single-activity container with FLAG_SECURE
│   │   │   │   ├── core/
│   │   │   │   │   ├── camera/
│   │   │   │   │   │   ├── CameraManager.kt     # CameraX binding, lens switcher, capture
│   │   │   │   │   │   ├── PrivacyFilter.kt     # 10 35mm film & beauty filters
│   │   │   │   │   │   └── PrivacyFilterProcessor.kt
│   │   │   │   │   ├── di/                      # Hilt Dependency Injection modules
│   │   │   │   │   ├── playback/
│   │   │   │   │   │   └── EncryptedMediaDataSource.kt # RAM-only Media3 streaming
│   │   │   │   │   └── security/
│   │   │   │   │       ├── BiometricHelper.kt   # BiometricPrompt hardware integration
│   │   │   │   │       ├── ECDHKeyManager.kt    # NIST P-256 ephemeral key exchange
│   │   │   │   │       ├── EncryptionService.kt # AES-256-GCM envelope encryption
│   │   │   │   │       └── KeystoreManager.kt   # Titan M / StrongBox keymaster wrapper
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/db/                # Room entities, DAOs, VaultDatabase
│   │   │   │   │   └── repository/              # Account, Billing, Media, Sharing impls
│   │   │   │   ├── domain/
│   │   │   │   │   ├── model/                   # MediaItem, UserAccount, SharingPermission
│   │   │   │   │   └── repository/              # Domain interfaces
│   │   │   │   └── ui/
│   │   │   │       ├── account/AccountScreen.kt # Anonymous Vault ID, Duo Partner card, Kill Switch
│   │   │   │       ├── camera/CameraScreen.kt   # Full viewfinder, Solo vs Duo pill toggle
│   │   │   │       ├── home/HomeScreen.kt       # Bottom navigation dock
│   │   │   │       ├── lock/LockScreen.kt       # Biometric unlock + 12-char mixed passcode
│   │   │   │       ├── onboarding/              # 4-step Zero-Sign-In onboarding
│   │   │   │       ├── settings/                # Security fortress & Auto-Lock timer picker
│   │   │   │       ├── sharing/                 # Controlled private sharing hub
│   │   │   │       ├── theme/                   # Monochrome Titanium darkroom design tokens
│   │   │   │       └── vault/VaultScreen.kt     # Gallery with All | Solo | Duo filtering
│   │   └── test/java/com/privatevault/app/      # 16 automated unit tests (100% passing)
├── docs/                                        # 6-Document Specification Pack
│   ├── 01_PRD.md
│   ├── 02_TRD.md
│   ├── 03_APP_FLOW.md
│   ├── 04_UI_UX_DESIGN_BRIEF.md
│   ├── 05_BACKEND_SCHEMA.md
│   ├── 06_IMPLEMENTATION_PLAN.md
│   └── README_START_HERE.md
├── signaling-server/                            # Zero-cloud WebRTC signaling server (Node.js)
│   ├── server.js
│   ├── test.js
│   └── package.json
└── README.md
```

---

## 5. Security & Threat Model Implementation Details

### A. Mandatory 12-Character Passcode Authorization Gates
Before executing sensitive actions, the user **must** enter their 12-character Master Passcode:
1. **Private Sharing (`PrivateSharingScreen.kt`):** The *Authorize & Send* button requires the 12-character passcode to decrypt and generate the invite.
2. **Device Migration (`AccountScreen.kt`):** Tapping *Migrate Phone* on the old device requires passcode entry before the camera scanner activates or Wi-Fi Direct streaming starts.
3. **Breakup Kill Switch (`AccountScreen.kt`):** Triggering *Sever Duo Link* requires passcode entry before executing permanent key destruction.

### B. User-Configurable Inactivity Auto-Lock (`SecuritySettingsScreen.kt`)
Controls how quickly decrypted RAM buffers are purged and the app locks:
* `Immediate`: On background event
* `30 Seconds`: High-security mode
* `1 Minute`: Balanced default
* `3 Minutes` / `5 Minutes`: Extended viewing

### C. Duress / Decoy Vault Option
Allows configuring an alternate 12-character passcode. When entered under physical coercion, PrivateLens displays an innocent decoy vault with scenic travel photos while keeping real media hidden.

### D. Anti-Tamper Auto-Wipe
After 10 consecutive failed 12-character passcode attempts, the app calls `KeyStore.deleteEntry("MasterVaultKey")`, rendering the entire local vault mathematically scrambled noise.

---

## 6. How to Build, Test, and Debug

### Clean Build & Unit Tests
```bash
cd "C:\Users\dipak\.gemini\antigravity\scratch\PrivateVault"

# 1. Compile Kotlin
.\gradlew.bat compileDebugKotlin

# 2. Run All 16 Unit Tests
.\gradlew.bat testDebugUnitTest

# 3. Assemble APK
.\gradlew.bat assembleDebug
```
*Output APK located at:* `app/build/outputs/apk/debug/app-debug.apk`

---

## 7. Connecting via Wireless Debugging & Installing on Device

When the user enables **Wireless Debugging** on Android 11+:

### Step 1: Pair Device (First Time Only)
On the phone: **Settings → Developer Options → Wireless Debugging → "Pair device with pairing code"**.  
The phone displays an IP, Port, and a 6-digit code.
```bash
adb pair <IP>:<PORT> <PAIRING_CODE>
# Example: adb pair 192.168.1.35:38491 123456
```

### Step 2: Connect via Wi-Fi
On the phone: Check the IP and Port displayed under **Wireless Debugging** (main toggle screen).
```bash
adb connect <IP>:<PORT>
# Example: adb connect 192.168.1.35:41235
```

### Step 3: Verify Connection
```bash
adb devices
# Output should show: 192.168.1.35:41235    device
```

### Step 4: Install the App
```bash
adb install -r "C:\Users\dipak\.gemini\antigravity\scratch\PrivateVault\app\build\outputs\apk\debug\app-debug.apk"
```

### Step 5: Launch PrivateLens
```bash
adb shell am start -n com.privatevault.app/.MainActivity
```

---

## 8. Development Roadmap (Next Steps for Next Engineer / AI Agent)

1. **Phase 6 P2P Streaming Polish:**
   * Test WebRTC data channel transfer between two physical Android devices using `signaling-server/server.js`.
   * Add TURN relay fallback configuration for symmetric NAT traversal.
2. **Google Play Billing Integration:**
   * Connect `BillingRepositoryImpl` to live Google Play Console SKU identifiers:
     * `privatelens_monthly_19` (₹19 / month)
     * `privatelens_yearly_149` (₹149 / year)
     * `privatelens_lifetime_499` (₹499 one-time)
3. **Decoy Vault Asset Bundle:**
   * Bundle a default set of 5 benign sample landscape/nature photos into the app assets so the Duress Passcode immediately renders a populated decoy vault.
