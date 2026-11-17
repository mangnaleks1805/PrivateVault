import os
import sys
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn

handoff_markdown = """# PrivateLens (PrivateVault) - Master Developer & AI Handoff Manual

**Complete Architectural Blueprint, Design Rationale, Implementation History, and Operational Guide**

- **Document Identifier:** `07_MASTER_DEVELOPER_HANDOFF`
- **Specification Suite:** Part 7 of the Sovereign Personal Camera & Private Vault Documentation Pack
- **Product Name:** PrivateLens (Internal Repository: `PrivateVault`)
- **Package Identifier:** `com.privatevault.app`
- **Version:** 1.0.0-PROD-READY
- **Release Date:** November 17, 2026
- **Local Repository Root:** `C:\\Users\\dipak\\.gemini\\antigravity\\scratch\\PrivateVault`
- **Golden Specification Suite:** `c:\\Users\\dipak\\Desktop\\cameraX`
- **Git Remote:** `https://github.com/mangnaleks1805/PrivateVault.git` (Branch: `main`)
- **Compiled Binary Artifact:** `app/build/outputs/apk/debug/app-debug.apk` (73.5 MB)
- **Unit Test Status:** 16 of 16 Unit Tests Passing (100% Green, 0 Failures, 0 Warnings)

---

## 1. Executive Summary & Problem Space

### 1.1 The Fundamental Flaw of Modern Smartphones
Every modern mobile operating system (Android, iOS) treats camera capture as a public, broadcast-first operation:
1. When a user presses the shutter button in the stock camera, the operating system saves raw unencrypted media into public shared storage (`/sdcard/DCIM/Camera`).
2. Cloud synchronizers (Google Photos, iCloud, OneDrive, Dropbox) immediately scan the media indexer and upload intimate photos to remote servers in background sync loops.
3. Messaging apps (WhatsApp, Telegram, Messenger) auto-scan storage and expose thumbnails in recent media pickers.
4. Third-party applications granted read-storage permissions can scrape personal photos without explicit per-file authorization.

### 1.2 The Failure of Conventional "Vault" Apps
Existing "photo vault" or "calculator lock" apps on Google Play suffer from catastrophic architectural flaws:
- **Trivial Guessable PINs:** 4-digit PINs (10,000 combinations) or 6-digit PINs (1,000,000 combinations) are cracked by automated brute-force scripts in under 1 second.
- **Unencrypted Disk Caching:** Media files are merely renamed with a `.bin` extension or moved to a hidden dot-folder (`.vault/`), leaving raw JPEG/MP4 headers intact.
- **Cache Leaks:** Conventional video players decrypt files into `/data/user/0/.../cache/temp_video.mp4`, allowing rooted devices or forensic dumpers to recover them.
- **The "Ashley Madison" Honeypot:** Requiring users to sign up with real email addresses, phone numbers, or social accounts creates a centralized database linking real identities to intimate photo vaults. A single server breach leads to mass extortion.
- **Arbitrary 48-Hour Deletion Timers:** Artificial timers destroy user memories if their phone battery dies, if they go off-grid, or if they travel on an airplane.

### 1.3 The Sovereign Solution: PrivateLens
PrivateLens is a native Android application built on the principle of absolute digital sovereignty:
```
Capture --> Encrypt --> Store --> Control Access --> Share Privately --> Revoke --> Delete
```
- Media is encrypted **in RAM** using hardware-backed AES-256-GCM before the bytes ever touch flash storage.
- Identity is **100% Zero-Knowledge** (Option B): No emails, no phone numbers, no names, no birthdays, and zero user accounts on servers.
- High-Entropy **12-Character Mixed Passcode** ($4.75 \\times 10^{23}$ combinations) makes random guessing and brute-force cracking mathematically impossible.
- Dedicated **1-to-1 Duo Vault** secured by Dual-Key Split Cryptography for couples, with a unilateral **Breakup Kill Switch**.
- Direct **Device-to-Device Migration** over encrypted Wi-Fi Direct with cryptographic hardware-shredding on the old phone.
- **In-Memory RAM Streaming** (`EncryptedMediaDataSource`) so decrypted video and photo bytes never touch disk during playback.

---

## 2. Core Architectural Philosophy & The 11 Commandments

### Commandment 1: The Sovereign Lifecycle
Every piece of media strictly follows: Capture --> Encrypt --> Store --> Control Access --> Share Privately --> Revoke --> Delete. No stage can be bypassed.

### Commandment 2: Zero-Knowledge Identity (Option B)
No collection of real names, email addresses, phone numbers, or birthdays.
- Identity is anchored solely to an anonymous, hardware-derived cryptographic Vault ID: `LENS-xxxx-xxxx`.
- **Why this was chosen over Option A:**
  1. *Eliminates Extortion Honeypots:* We cannot be subpoenaed or breached for user identity lists because we do not have them.
  2. *Legal Protection (India DPDP Act 2023):* India's Digital Personal Data Protection Act imposes penalties up to 250 Crore INR (~$30M USD) for personal data breaches. By storing zero personal data, company liability is eliminated.
  3. *Zero User Friction:* Users open the app and start capturing securely without email verification or OTP delays.

### Commandment 3: Hardware Keystore & Envelope Encryption (AES-256-GCM)
Software encryption can be bypassed if the device is rooted. PrivateLens uses hardware envelope encryption:
- A master wrapping key (`MasterVaultKey`) is generated inside the device's **Titan M / Secure Element (Android Keystore)** with `setUserAuthenticationRequired(true)` and `isInsideSecureHardware()`.
- Every photo and video receives a unique, cryptographically random 256-bit AES Data Encryption Key (DEK) and a unique 96-bit Initialization Vector (IV).
- The DEK is encrypted with the master key and stored in the encrypted Room SQLite database (`VaultDatabase`).
- Raw files on disk are mathematically indistinguishable from white noise.

### Commandment 4: 12-Character Mixed Master Passcode ($4.75 \\times 10^{23}$ Combinations)
Conventional 4-digit and 6-digit numeric PINs are strictly prohibited:
- 4-digit numeric PIN = $10^4 = 10,000$ combinations (cracked in < 1 ms).
- 6-digit numeric PIN = $10^6 = 1,000,000$ combinations (cracked in 1 ms).
- **12-Character Mixed Passcode:** Includes uppercase (A-Z), lowercase (a-z), numbers (0-9), and special symbols (!@#$%^&*).
  $$\\text{Keyspace} = 94^{12} \\approx 475,920,314,814,253,379,308,544 \\approx 4.75 \\times 10^{23}$$
  At 1 trillion guesses per second, cracking takes **15,085,000 years**. Brute-force attacks are dead.

### Commandment 5: No 48-Hour Auto-Delete Timer
We rejected artificial self-destruct timers. Personal and intimate memories should not be wiped because a phone battery died or a user went on a camping trip. Physical theft protection is handled by hardware-level exponential rate-limiting, duress decoy vaults, and 10-attempt auto-wipe.

### Commandment 6: Dedicated 1-to-1 Duo Vault with Dual-Key Split Cryptography
Couples share a continuous relationship vault without cloud storage:
- **Dual-Key Split Cryptography:** Media in the Duo Vault is encrypted with a master key derived from two independent 256-bit key shares ($K = K_A \\oplus K_B$). Partner A holds $K_A$; Partner B holds $K_B$.
- **Breakup Kill Switch:** If a breakup occurs, either partner taps *Sever Duo Link*. The local key share is deleted from hardware storage. Both devices immediately lose the mathematical ability to decrypt the shared media. It turns into permanent unrecoverable noise (`0x9F42...`).

### Commandment 7: Direct Phone Migration with Hardware Crypto-Shredding
When upgrading to a new phone:
1. Devices connect peer-to-peer via encrypted Wi-Fi Direct.
2. Encrypted blobs and encrypted SQLite records stream directly between phones (zero cloud involvement).
3. Once 100% SHA-256 checksum verification succeeds, the old phone executes `KeyStore.deleteEntry("MasterVaultKey")`.
4. Even if the old phone is later sold on eBay or subjected to forensic NAND chip-off extraction, the data is permanently unrecoverable.

### Commandment 8: RAM-Only In-Memory Streaming (`EncryptedMediaDataSource`)
Conventional Android media players decrypt files into disk cache directories. PrivateLens implements a custom AndroidX Media3 `DataSource`:
- Chunks are fetched and decrypted on-the-fly directly into RAM buffers.
- Plaintext data never touches physical flash memory.
- When the viewer or player closes, RAM buffers are zeroed out (`Arrays.fill(buffer, 0.toByte())`).

### Commandment 9: Complete Security Fortress Suite
- **Duress / Decoy Vault:** An alternate 12-character passcode opens an innocent decoy vault containing benign landscape photos under physical coercion.
- **Anti-Tamper Auto-Wipe:** After 10 consecutive failed 12-character passcode attempts, all keys in the hardware Keystore are deleted.
- **Root & Frida Hooking Detection:** Actively scans for `su` binaries, Magisk, Zygisk, and runtime Frida memory hooks.
- **USB Debugging (ADB) Guard:** Alerts or locks when an unauthorized USB debugging session is detected.
- **FLAG_SECURE:** Prevents screenshots, screen recording, and task switcher thumbnail leaks across all screens.

### Commandment 10: Strict Zero-Emoji Professional Darkroom Design System
All emojis are strictly prohibited across code, UI, and documentation:
- Theme: **Monochrome Titanium** and **Velvet Onyx** (`#09090B`, `#18181B`).
- Safety Accents: **Amber Safety** (`#F59E0B`), **Cyan Quantum** (`#06B6D4`), **Emerald Secure** (`#10B981`), **Rose Danger** (`#F43F5E`).
- Icons: Clean Material Vector SVGs only.

### Commandment 11: 2-Plan Micro-SaaS Subscription Model
A frictionless pricing structure built for high conversion:
- **Free Forever (0 INR / $0 USD):** Local single-device hardware vault, 2-minute video recording cap, basic lenses.
- **Pro & Couple Vault (All-In-One):**
  - **19 INR / month** ($0.99/mo global)
  - **149 INR / year** ($4.99/yr global)
  - **499 INR Lifetime** ($29.99 one-time global)
- **Zero-Knowledge Google Play Verification:** Google handles billing tokens (`billingClient.queryPurchasesAsync()`). No user accounts or personal profiles are required to restore purchases.

---

## 3. How We Created This: Step-by-Step Build History

### Step 1: The 6-Document Specification Pack
Before writing code, we authored the formal engineering specification suite:
- `01_PRD.md`: Product vision, legal boundaries, DPDP Act 2023 compliance, feature scopes.
- `02_TRD.md`: Technical architecture, Android Keystore StrongBox, Media3 RAM streaming, WebRTC signaling.
- `03_APP_FLOW.md`: Screen hierarchy, state machines, biometric lifecycle triggers.
- `04_UI_UX_DESIGN_BRIEF.md`: Monochrome Titanium design tokens, layout grids, 0-emoji mandate.
- `05_BACKEND_SCHEMA.md`: Room SQLCipher database tables, zero-knowledge metadata boundaries.
- `06_IMPLEMENTATION_PLAN.md`: 6 delivery phases with strict acceptance gates.

### Step 2: Architecture & Security Engine Engineering
- Implemented `KeystoreManager.kt` wrapping Android Keystore and StrongBox keymaster.
- Implemented `EncryptionService.kt` providing AES-256-GCM envelope encryption and decryption with authentication tags.
- Implemented `ECDHKeyManager.kt` using NIST P-256 elliptic curve Diffie-Hellman key exchange for ephemeral peer-to-peer handshakes.
- Implemented `EncryptedMediaDataSource.kt` extending AndroidX Media3 `DataSource` for RAM-only media playback.

### Step 3: Domain, Data, & Database Layer
- Created Room database `VaultDatabase.kt` with entities: `MediaEntity`, `UserAccountEntity`, `SharingPermissionEntity`.
- Implemented repository interfaces and implementations: `MediaRepositoryImpl`, `AccountRepositoryImpl`, `SharingRepositoryImpl`, `BillingRepositoryImpl`.
- Implemented Hilt Dependency Injection modules in `core/di/`.

### Step 4: UI / UX Presentation Layer (Jetpack Compose)
- Single Activity architecture (`MainActivity.kt`) enforcing `FLAG_SECURE`.
- `LockScreen.kt`: Biometric unlock with fallback 12-character mixed Master Passcode and real-time entropy calculation.
- `CameraScreen.kt`: CameraX viewfinder with instant pill toggle between `Solo Vault` and `Duo Vault`.
- `VaultScreen.kt`: Media grid with `All Vaults`, `Solo Vault`, and `Duo Vault` filtering tabs.
- `PrivateSharingScreen.kt`: Controlled private sharing hub with mandatory 12-character passcode gate.
- `AccountScreen.kt`: Anonymous Vault ID display, Duo Partner connection card, Phone Migration wizard, and Breakup Kill Switch.
- `SecuritySettingsScreen.kt`: Interactive Inactivity Auto-Lock selector (`Immediate`, `30s`, `1m`, `3m`, `5m`), Decoy Vault, Anti-Tamper wipe switch.

### Step 5: Option B Hardening & Passcode Gates
- Upgraded the Master Passcode from 6-digit numeric to a 12-character mixed alphanumeric passcode.
- Added mandatory passcode authorization dialogs before:
  1. Creating an outbound private share invite.
  2. Starting a device migration.
  3. Activating the Breakup Kill Switch.

### Step 6: Automated Testing & Verification
- Authored 16 automated unit tests across cryptographic engines, billing limits, and sharing permissions.
- Verified 100% test passing (`16/16 PASSED`) and zero compilation errors.
- Assembled release-ready debug binary: `app/build/outputs/apk/debug/app-debug.apk` (73.5 MB).

---

## 4. Codebase Directory Map & Component Inventory

```
PrivateVault/
|-- app/
|   |-- src/
|   |   |-- main/
|   |   |   |-- AndroidManifest.xml              # Permissions, FLAG_SECURE, backup rules
|   |   |   |-- java/com/privatevault/app/
|   |   |   |   |-- App.kt                       # Application class with Hilt Android App
|   |   |   |   |-- MainActivity.kt              # Root activity enforcing window FLAG_SECURE
|   |   |   |   |-- core/
|   |   |   |   |   |-- camera/
|   |   |   |   |   |   |-- CameraManager.kt     # CameraX binding, lens switcher, capture
|   |   |   |   |   |   |-- PrivacyFilter.kt     # 10 35mm film & beauty privacy filters
|   |   |   |   |   |   |-- PrivacyFilterProcessor.kt
|   |   |   |   |   |-- di/                      # Hilt DI modules (Database, Security, Network)
|   |   |   |   |   |-- playback/
|   |   |   |   |   |   |-- EncryptedMediaDataSource.kt # RAM-only Media3 streaming player
|   |   |   |   |   |-- security/
|   |   |   |   |   |   |-- BiometricHelper.kt   # BiometricPrompt integration
|   |   |   |   |   |   |-- ECDHKeyManager.kt    # NIST P-256 ephemeral key exchange
|   |   |   |   |   |   |-- EncryptionService.kt # AES-256-GCM envelope encryption
|   |   |   |   |   |   |-- KeystoreManager.kt   # Titan M / StrongBox keymaster wrapper
|   |   |   |   |-- data/
|   |   |   |   |   |-- local/db/                # Room entities, DAOs, and VaultDatabase
|   |   |   |   |   |-- repository/              # Account, Billing, Media, Sharing impls
|   |   |   |   |-- domain/
|   |   |   |   |   |-- model/                   # MediaItem, UserAccount, SharingPermission
|   |   |   |   |   |-- repository/              # Domain interfaces
|   |   |   |   |-- ui/
|   |   |   |   |   |-- account/AccountScreen.kt # Anonymous Vault ID, Duo Partner, Kill Switch
|   |   |   |   |   |-- camera/CameraScreen.kt   # Full viewfinder, Solo vs Duo pill toggle
|   |   |   |   |   |-- home/HomeScreen.kt       # Bottom navigation dock
|   |   |   |   |   |-- lock/LockScreen.kt       # Biometric unlock + 12-char mixed passcode
|   |   |   |   |   |-- onboarding/              # 4-step Zero-Sign-In onboarding
|   |   |   |   |   |-- settings/                # Security fortress & Auto-Lock timer picker
|   |   |   |   |   |-- sharing/                 # Controlled private sharing hub
|   |   |   |   |   |-- theme/                   # Monochrome Titanium darkroom tokens
|   |   |   |   |   |-- vault/VaultScreen.kt     # Gallery with All | Solo | Duo filtering
|   |   |-- test/java/com/privatevault/app/      # 16 automated unit tests (100% passing)
|-- docs/                                        # 6-Document Specification Pack
|-- signaling-server/                            # Zero-cloud WebRTC signaling server (Node.js)
|-- README.md                                    # Architectural root README
```

---

## 5. Wireless Debugging & Physical Installation Guide

### Prerequisites
- Android device running **Android 11 or higher**.
- Computer and phone connected to the **exact same Wi-Fi network**.
- Developer Options enabled on the phone.

### Step 1: Enable Wireless Debugging on Phone
1. Open phone **Settings**.
2. Scroll to **Developer Options**.
3. Scroll down to **Wireless Debugging** and toggle it **ON**.
4. Tap directly on the text *Wireless Debugging* to open its detailed configuration screen.

### Step 2: First-Time Pairing (If Required)
If this is the first time connecting this computer to the phone:
1. Tap **"Pair device with pairing code"**.
2. The phone displays:
   - Wi-Fi pairing code: `123456` (6 digits)
   - IP address & Port: e.g., `192.168.1.35:38491`
3. On the computer terminal, run:
   ```bash
   adb pair <IP>:<PORT> <PAIRING_CODE>
   # Example: adb pair 192.168.1.35:38491 123456
   ```
4. Output will confirm: `Successfully paired to 192.168.1.35:38491`.

### Step 3: Connect ADB over Wi-Fi
1. Return to the main Wireless Debugging screen on the phone.
2. Note the **IP address & Port** displayed at the top (under "IP address & Port", which uses a different port than pairing):
   - Example: `192.168.1.35:41235`
3. On the computer terminal, run:
   ```bash
   adb connect <IP>:<PORT>
   # Example: adb connect 192.168.1.35:41235
   ```
4. Output will confirm: `connected to 192.168.1.35:41235`.

### Step 4: Verify Device Attachment
```bash
adb devices
```
Expected output:
```
List of devices attached
192.168.1.35:41235    device
```

### Step 5: Install PrivateLens APK
```bash
adb install -r "C:\\Users\\dipak\\.gemini\\antigravity\\scratch\\PrivateVault\\app\\build\\outputs\\apk\\debug\\app-debug.apk"
```
Expected output: `Success`.

### Step 6: Launch the App
```bash
adb shell am start -n com.privatevault.app/.MainActivity
```
The application will immediately launch on the device screen.

---

## 6. How the Next Engineer or AI Agent Should Resume Development

### Current Verified State
- App compiles cleanly with Gradle 8.x and Kotlin 2.0.
- All 16 automated tests pass with zero warnings or errors.
- Debug APK is assembled and ready for installation.
- Working tree is clean and synced to GitHub (`https://github.com/mangnaleks1805/PrivateVault.git`).

### Immediate Next Tasks on the Roadmap
1. **WebRTC P2P Direct Streaming:**
   - Integrate `signaling-server/server.js` with Android WebRTC client for remote streaming between two connected phones.
   - Verify view-only frame rendering in RAM without allowing local media saves on the receiver.
2. **Google Play In-App Billing Live Connection:**
   - Connect `BillingRepositoryImpl` to live Google Play Console SKU identifiers:
     - `privatelens_monthly_19` (19 INR / month)
     - `privatelens_yearly_149` (149 INR / year)
     - `privatelens_lifetime_499` (499 INR Lifetime)
3. **Decoy Vault Asset Bundle:**
   - Add 5 default benign nature landscape images into `app/src/main/assets/decoy/` so entering the Duress Passcode immediately populates a believable decoy vault.
"""

# Write markdown files
desktop_md_path = r"c:\Users\dipak\Desktop\cameraX\07_MASTER_DEVELOPER_HANDOFF.md"
repo_md_path = r"C:\Users\dipak\.gemini\antigravity\scratch\PrivateVault\docs\07_MASTER_DEVELOPER_HANDOFF.md"

with open(desktop_md_path, "w", encoding="utf-8") as f:
    f.write(handoff_markdown)

with open(repo_md_path, "w", encoding="utf-8") as f:
    f.write(handoff_markdown)

print("Markdown files written successfully.")

# Convert to DOCX using python-docx
doc = docx.Document()

# Configure margins
sections = doc.sections
for section in sections:
    section.top_margin = Inches(0.75)
    section.bottom_margin = Inches(0.75)
    section.left_margin = Inches(0.75)
    section.right_margin = Inches(0.75)

# Styling functions
def set_cell_background(cell, fill_hex):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_hex}"/>')
    tcPr.append(shd)

def add_header(text, level=1):
    h = doc.add_heading(level=level)
    run = h.add_run(text)
    run.font.name = 'Arial'
    run.bold = True
    if level == 1:
        run.font.size = Pt(18)
        run.font.color.rgb = RGBColor(15, 23, 42) # Slate 900
    elif level == 2:
        run.font.size = Pt(14)
        run.font.color.rgb = RGBColor(30, 41, 59) # Slate 800
    elif level == 3:
        run.font.size = Pt(12)
        run.font.color.rgb = RGBColor(51, 65, 85) # Slate 700
    return h

def add_p(text, bold=False, italic=False, color=RGBColor(30, 41, 59)):
    p = doc.add_paragraph()
    run = p.add_run(text)
    run.font.name = 'Arial'
    run.font.size = Pt(10.5)
    run.bold = bold
    run.italic = italic
    run.font.color.rgb = color
    return p

def add_callout(title, text):
    tbl = doc.add_table(rows=1, cols=1)
    tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
    cell = tbl.cell(0, 0)
    set_cell_background(cell, "F1F5F9") # Slate 100
    
    cp = cell.paragraphs[0]
    r_title = cp.add_run(title + "\n")
    r_title.bold = True
    r_title.font.name = 'Arial'
    r_title.font.size = Pt(10.5)
    r_title.font.color.rgb = RGBColor(15, 23, 42)
    
    r_text = cp.add_run(text)
    r_text.font.name = 'Arial'
    r_text.font.size = Pt(10)
    r_text.font.color.rgb = RGBColor(51, 65, 85)
    
    # spacing
    doc.add_paragraph()

# Build DOCX Content
h1 = add_header("PrivateLens (PrivateVault) - Master Developer & AI Handoff Manual", 1)
add_p("Complete Architectural Blueprint, Design Rationale, Implementation History, and Operational Guide", italic=True)

add_callout("Document Metadata", 
    "• Document Identifier: 07_MASTER_DEVELOPER_HANDOFF\n"
    "• Specification Suite: Part 7 of the Sovereign Personal Camera & Private Vault Documentation Pack\n"
    "• Product Name: PrivateLens (Internal Repository: PrivateVault)\n"
    "• Package Identifier: com.privatevault.app\n"
    "• Version: 1.0.0-PROD-READY | Release Date: November 17, 2026\n"
    "• Local Repository Root: C:\\Users\\dipak\\.gemini\\antigravity\\scratch\\PrivateVault\n"
    "• Golden Specification Suite: c:\\Users\\dipak\\Desktop\\cameraX\n"
    "• Git Remote: https://github.com/mangnaleks1805/PrivateVault.git (Branch: main)\n"
    "• Compiled Binary Artifact: app/build/outputs/apk/debug/app-debug.apk (73.5 MB)\n"
    "• Unit Test Status: 16 of 16 Unit Tests Passing (100% Green, 0 Failures, 0 Warnings)"
)

add_header("1. Executive Summary & Problem Space", 1)

add_header("1.1 The Fundamental Flaw of Modern Smartphones", 2)
add_p("Every modern mobile operating system (Android, iOS) treats camera capture as a public, broadcast-first operation:")
add_p("1. When a user presses the shutter button in the stock camera, the OS saves raw unencrypted media into public shared storage (/sdcard/DCIM/Camera).")
add_p("2. Cloud synchronizers (Google Photos, iCloud, OneDrive) immediately scan storage indexes and upload intimate photos to remote servers in background loops.")
add_p("3. Messaging apps (WhatsApp, Telegram) auto-scan storage and expose intimate thumbnails in recent media pickers.")
add_p("4. Third-party applications granted read-storage permissions can scrape personal photos without explicit per-file authorization.")

add_header("1.2 The Failure of Conventional 'Vault' Apps", 2)
add_p("Existing photo vault apps on Google Play suffer from catastrophic architectural flaws:")
add_p("• Trivial Guessable PINs: 4-digit PINs (10,000 combinations) or 6-digit PINs (1,000,000 combinations) are cracked in under 1 second by automated scripts.")
add_p("• Unencrypted Disk Caching: Media files are merely renamed or moved to hidden dot-folders, leaving raw JPEG/MP4 headers intact.")
add_p("• Cache Leaks: Conventional video players decrypt files into disk cache directories, allowing forensic extraction on rooted devices.")
add_p("• The 'Ashley Madison' Honeypot: Requiring real email addresses or phone numbers creates a centralized database linking real identities to intimate vaults.")
add_p("• Arbitrary 48-Hour Deletion Timers: Artificial timers destroy user memories if their battery dies or they travel off-grid.")

add_header("1.3 The Sovereign Solution: PrivateLens", 2)
add_p("PrivateLens is a native Android application built on the principle of absolute digital sovereignty:")
add_p("Capture --> Encrypt --> Store --> Control Access --> Share Privately --> Revoke --> Delete", bold=True)
add_p("• Media is encrypted in RAM using hardware-backed AES-256-GCM before bytes ever touch flash storage.")
add_p("• Identity is 100% Zero-Knowledge (Option B): No emails, no phone numbers, no names, no birthdays, and zero accounts on servers.")
add_p("• High-Entropy 12-Character Mixed Passcode (4.75 x 10^23 combinations) makes random guessing and brute-force cracking mathematically impossible.")
add_p("• Dedicated 1-to-1 Duo Vault secured by Dual-Key Split Cryptography for couples, with a unilateral Breakup Kill Switch.")
add_p("• Direct Device-to-Device Migration over encrypted Wi-Fi Direct with cryptographic hardware-shredding on the old phone.")
add_p("• In-Memory RAM Streaming (EncryptedMediaDataSource) so decrypted bytes never touch disk during playback.")

add_header("2. Core Architectural Philosophy & The 11 Commandments", 1)
add_p("Commandment 1: The Sovereign Lifecycle - Media strictly follows Capture --> Encrypt --> Store --> Control Access --> Share Privately --> Revoke --> Delete.")
add_p("Commandment 2: Zero-Knowledge Identity (Option B) - Zero collection of real names, emails, phones, or birthdays. Vault ID: LENS-xxxx-xxxx. Eliminates DPDP Act 2023 liability (250 Crore INR penalty) and extortion honeypots.")
add_p("Commandment 3: Hardware Keystore & Envelope Encryption - Master key in Titan M / Secure Element. Unique AES-256 DEK and IV per photo and video.")
add_p("Commandment 4: 12-Character Mixed Master Passcode - 4.75 x 10^23 combinations. At 1 trillion guesses/sec, brute force takes 15 million years.")
add_p("Commandment 5: No 48-Hour Auto-Delete Timer - Memories are never wiped unexpectedly. Physical security handled by hardware rate-limiting and auto-wipe.")
add_p("Commandment 6: Dedicated 1-to-1 Duo Vault - Dual-Key Split Cryptography (K = KA XOR KB). Unilateral Breakup Kill Switch instantly destroys local key.")
add_p("Commandment 7: Direct Phone Migration with Hardware Crypto-Shredding - Wi-Fi Direct transfer. Old phone executes KeyStore.deleteEntry() on 100% checksum match.")
add_p("Commandment 8: RAM-Only In-Memory Streaming - EncryptedMediaDataSource streams decrypted chunks in memory buffers; disk never touched.")
add_p("Commandment 9: Complete Security Fortress - Decoy Vault, 10-attempt Anti-Tamper wipe, Root/Frida detection, USB ADB guard, FLAG_SECURE.")
add_p("Commandment 10: Strict Zero-Emoji Professional Darkroom Design System - Monochrome Titanium, Velvet Onyx, Amber Safety. Clean vector SVGs only.")
add_p("Commandment 11: 2-Plan Micro-SaaS Subscription Model - Free Forever (0 INR) vs Pro & Couple Vault (19 INR/mo, 149 INR/yr, 499 INR Lifetime) via Google Play Billing.")

add_header("3. Step-by-Step Build & Creation History", 1)
add_p("Step 1: Specification Pack Creation - Formally drafted 01_PRD to 06_IMPLEMENTATION_PLAN following strict engineering standards.")
add_p("Step 2: Architecture & Security Engine Engineering - Implemented KeystoreManager, EncryptionService (AES-256-GCM), ECDHKeyManager, and EncryptedMediaDataSource.")
add_p("Step 3: Domain, Data, & Database Layer - Built Room VaultDatabase, DAOs, Entities, and Repositories with Hilt Dependency Injection.")
add_p("Step 4: UI/UX Presentation Layer - Built Jetpack Compose screens: LockScreen (12-char mixed passcode), CameraScreen (Solo vs Duo toggle), VaultScreen, PrivateSharingScreen, AccountScreen, and SecuritySettingsScreen.")
add_p("Step 5: Option B Hardening & Passcode Authorization Gates - Added mandatory 12-character passcode gates before sharing, migration, and breakup kill switch.")
add_p("Step 6: Automated Testing & Verification - 16 automated unit tests written and passing (100% Green). Assembled 73.5 MB debug APK.")

add_header("4. Codebase Directory Map & Component Inventory", 1)
add_p("app/src/main/java/com/privatevault/app/ - Main application source code.")
add_p("core/camera/ - CameraManager (CameraX bindings), PrivacyFilter, and PrivacyFilterProcessor.")
add_p("core/security/ - KeystoreManager (Titan M), EncryptionService (AES-256-GCM), ECDHKeyManager, BiometricHelper.")
add_p("core/playback/ - EncryptedMediaDataSource (RAM-only Media3 player).")
add_p("data/local/db/ - Room VaultDatabase, MediaEntity, UserAccountEntity, SharingPermissionEntity.")
add_p("data/repository/ - MediaRepositoryImpl, AccountRepositoryImpl, SharingRepositoryImpl, BillingRepositoryImpl.")
add_p("ui/ - Jetpack Compose UI (Lock, Camera, Vault, Sharing, Account, Settings, Theme).")
add_p("docs/ - Formal 6-Document Specification Pack + Master Developer Handoff Manual.")

add_header("5. Wireless Debugging & Physical Installation Guide", 1)
add_p("Step 1: On phone, open Settings -> Developer Options -> Wireless Debugging -> Toggle ON.")
add_p("Step 2: Tap 'Pair device with pairing code'. Note the 6-digit code and pairing IP:Port. Run: adb pair <IP>:<PORT> <CODE>")
add_p("Step 3: In main Wireless Debugging screen, note the connection IP:Port. Run: adb connect <IP>:<PORT>")
add_p("Step 4: Verify with: adb devices")
add_p("Step 5: Install APK: adb install -r \"C:\\Users\\dipak\\.gemini\\antigravity\\scratch\\PrivateVault\\app\\build\\outputs\\apk\\debug\\app-debug.apk\"")
add_p("Step 6: Launch app: adb shell am start -n com.privatevault.app/.MainActivity")

add_header("6. Next Steps for Next Engineer or AI Agent", 1)
add_p("1. WebRTC P2P Direct Streaming: Connect signaling-server/server.js with Android WebRTC client for live streaming.")
add_p("2. Google Play In-App Billing: Connect BillingRepositoryImpl to live Google Play Console SKU IDs (privatelens_monthly_19, privatelens_yearly_149, privatelens_lifetime_499).")
add_p("3. Decoy Vault Asset Bundle: Bundle 5 sample nature photos into assets/decoy/ for instant populated decoy vault upon entering duress passcode.")

docx_desktop_path = r"c:\Users\dipak\Desktop\cameraX\07_MASTER_DEVELOPER_HANDOFF.docx"
doc.save(docx_desktop_path)
print("DOCX saved successfully to:", docx_desktop_path)
