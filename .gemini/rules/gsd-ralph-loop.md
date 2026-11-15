# Get Shit Done (GSD) & Ralph Autonomous Execution Protocol

## 1. Operating Mindset
- **Bias for Action:** Execute complete, end-to-end solutions. Do not stop at stubs or partial implementations.
- **Ralph Autonomous Feedback Loop:** Every code modification must follow the rigorous loop:
  1. **Diagnose & Design:** Identify root cause and verify exact dependencies.
  2. **Implement:** Write clean, production-grade Kotlin/Compose code following Clean Architecture.
  3. **Compile:** Run `gradlew.bat assembleDebug` and inspect compiler output.
  4. **Deploy & Verify:** Push APK directly to connected Android devices via ADB (`adb install -r`) and verify process execution (`pidof`, `dumpsys window`).
  5. **Self-Heal:** If any error or warning arises, fix immediately without waiting for user prompts.

## 2. Non-Negotiable Privacy Safeguards
- **Zero Cloud Leakage:** Never include advertising SDKs, cloud backup sync, or public MediaStore writes.
- **Hardware Isolation:** All cryptographic operations must use the Android Keystore Master Key and authenticated AES-256-GCM.
- **Screen Protection:** Enforce `FLAG_SECURE` on all sensitive UI screens.

## 3. Toolchain & Environment
- **SDK Path:** `C:\Users\dipak\AppData\Local\Android\Sdk`
- **JDK:** `C:\Users\dipak\.jdk\jdk-17.0.21+9` (Temurin 17 LTS)
- **Target OS:** Android 11+ (`minSdk = 30`, `compileSdk = 35`, `targetSdk = 35`)
- **ADB Executable:** `C:\Users\dipak\AppData\Local\Android\Sdk\platform-tools\adb.exe`
