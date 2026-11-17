# Secure Personal Camera & Private Vault (PrivateLens) - Specification Pack

Prepared November 2026 | Production Engineering Suite v1.0.0

## Documents (Read in this order)
1. `01_PRD` - Product purpose, scope, legal boundaries (DPDP Act 2023), and Option B Zero-Knowledge architecture.
2. `02_TRD` - Technical architecture, hardware Keystore wrapping, AES-256-GCM envelope encryption, and RAM streaming.
3. `03_APP_FLOW` - Complete screen map, biometric & 12-character passcode verification triggers, and lifecycle states.
4. `04_UI_UX_DESIGN_BRIEF` - Monochrome Titanium darkroom design tokens, 0-emoji mandate, and screen layouts.
5. `05_BACKEND_SCHEMA` - Encrypted Room database tables, zero-knowledge metadata boundaries, and P2P WebRTC schemas.
6. `06_IMPLEMENTATION_PLAN` - 6 gated implementation phases, QA checklists, and 16 automated unit test suites.
7. `07_MASTER_DEVELOPER_HANDOFF` - **Master Developer & AI Handoff Manual** containing complete build history, design decisions, code inventory, wireless debugging guide, and resume instructions.

Each document is provided as DOCX and Markdown. Markdown is convenient for Antigravity/agent context; DOCX is suitable for human review, printing, and archiving.

## Critical Product Decisions & Commandments
- **Option B Zero-Knowledge:** Absolute zero collection of real names, emails, phone numbers, or birthdays.
- **12-Character Mixed Passcode ($4.75 \times 10^{23}$ combinations):** Eliminates brute force and random guessing.
- **Dedicated 1-to-1 Duo Vault:** Dual-Key Split Cryptography ($K = K_A \oplus K_B$) with unilateral Breakup Kill Switch.
- **Direct Phone Migration with Hardware Crypto-Shredding:** Wi-Fi Direct direct transfer + old Keystore master key purge.
- **RAM-Only Playback:** `EncryptedMediaDataSource` decrypts chunks directly in RAM; plaintext never touches disk.
- **No 48-Hour Auto-Delete Timer:** Memories are never wiped unexpectedly. Hardware rate-limiting and auto-wipe protect physical theft.
- **Security Fortress:** Duress Decoy Vault, 10-attempt Anti-Tamper wipe, Root/Frida guard, USB guard, and `FLAG_SECURE`.
- **2-Plan Micro-SaaS:** Free Forever vs Pro & Couple Vault (₹19/mo, ₹149/yr, ₹499 Lifetime) verified via Google Play Billing.

## How to Resume Development (for Engineers and AI Agents)
Start by reading `07_MASTER_DEVELOPER_HANDOFF.md` (or open `07_MASTER_DEVELOPER_HANDOFF.docx`). It contains the complete architectural breakdown, codebase map, verification reports, and step-by-step instructions to connect to Android devices via Wi-Fi debugging and resume engineering.
