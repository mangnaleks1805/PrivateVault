# Implementation Plan

**Gated phases, sprints, definition of done and release checklist**

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

## 1. Delivery strategy

Build in gated increments. The first product milestone is a reliable local encrypted vault, not accounts or sharing. Sharing is a separate engineering and policy gate because it introduces device identity, network transport, revocation and anti-abuse requirements.

## 2. Workstreams

| Workstream | Owner profile | Outputs |
| --- | --- | --- |
| Android app | Android/Kotlin engineer | Compose UI, CameraX, local storage, BiometricPrompt, Room, playback. |
| Cryptography/security | Mobile security engineer | Threat model, crypto file format, Keystore integration, test vectors, independent review. |
| Backend (later) | Backend engineer | Auth, device registration, invitation/grant service, deletion, billing verification. |
| P2P sharing (later) | Android + network/security engineer | WebRTC prototype, TURN setup, identity verification, session revocation. |
| Design | Product designer | Flows, design system, accessible UI and prototypes. |
| Compliance/release | Founder + Indian counsel + Play policy reviewer | Privacy notice, terms, age-assurance assessment, Play declarations and launch review. |

## 3. Phase plan and exit gates

| Phase | Scope / tasks | Deliverable | Exit gate |
| --- | --- | --- | --- |
| 0. Discovery & risk review | Confirm positioning, policy risk, target Android API, threat model, consent/age approach, no-cloud decision, data inventory. | Approved product assumptions and architecture decision records. | Policy/legal review identifies a viable launch position; unresolved high-risk points are documented. |
| 1. Project foundation | Create native Kotlin/Compose app, navigation, DI, module boundaries, CI build, static analysis and baseline tests. | Buildable app shell. | Clean build on CI and emulator/device; architecture documented. |
| 2. Onboarding & permissions | Welcome, privacy principles, 18+ self-declaration, camera/mic explanations, permission state handling. | First-run user journey. | No unnecessary permission requests; copy clearly distinguishes self-declaration from verification. |
| 3. Camera | CameraX preview, photo/video capture, visible indicators, foreground-only behaviour, lifecycle and low-storage handling. | Working capture feature. | No hidden/background capture; recordings reliably finalize; interruption tests pass. |
| 4. Cryptographic storage | Review file format, per-file DEK, Keystore wrapping key, chunked encryption, atomic finalize, backup exclusions. | Encrypted media repository. | Static inspection shows no plaintext durable media; crypto review and negative tests pass. |
| 5. Vault & lock | Room metadata, listing, playback, categories, deletion, BiometricPrompt, auto-lock, FLAG_SECURE. | Offline local vault MVP. | Works with network disabled; gallery/backup/log/cache tests pass. |
| 6. Private sharing spike | Select P2P model, pair two devices, prove authenticated encrypted live stream, TURN relay, no persistent media upload. | Technical spike and risk report. | Independent review of protocol assumptions; acceptable battery, quality and network behaviour. |
| 7. Accounts & production sharing | Passkeys, device registration, invitation, grant, expiry/revoke, session authorization, abuse controls, deletion. | Controlled one-recipient sharing. | IDOR/replay/revocation tests pass; no media API/storage; policy/legal gate passes. |
| 8. Monetization | Product catalogue, Play Billing, server validation where required, restore, entitlement UX. | Freemium/Premium flows. | No ads; entitlement and restore tests pass; pricing tested separately. |
| 9. Release hardening | Accessibility, localization, device matrix, penetration test, privacy policy/terms, Data Safety, store assets, staged release. | Release candidate. | All launch acceptance criteria signed off. |

## 4. Suggested sprint breakdown

| Sprint | Focus | Demo / output |
| --- | --- | --- |
| Sprint 0 | Product/technical discovery. | Approved PRD/TRD and decision log. |
| Sprint 1 | Project skeleton, Compose navigation, theming, CI. | App launches and navigation works. |
| Sprint 2 | Onboarding, age eligibility, permission education. | Complete first-run flow. |
| Sprint 3 | CameraX photo/video capture and foreground state. | Capture sample photo/video on real device. |
| Sprint 4 | Crypto design spike and Keystore integration. | Encrypted test file; plaintext inspection tests. |
| Sprint 5 | Encrypted recording integration and recovery. | Capture encrypted media with failure handling. |
| Sprint 6 | Vault list, playback, categories, deletion. | Offline encrypted vault demo. |
| Sprint 7 | Biometric lock, FLAG_SECURE, backup and privacy QA. | MVP security test report. |
| Sprint 8 | P2P architecture spike and two-device demo. | Connection feasibility and security findings. |
| Sprint 9+ | Backend, account, production sharing, billing and release. | Only schedule after gate review. |

## 5. Definition of done

- Code compiles and automated tests pass.
- Feature works on at least one physical Android device; device/API compatibility scope is recorded.
- Relevant threat cases and failure states are tested.
- No secrets, media content or sensitive paths appear in logs.
- No plaintext media remains in unintended storage, cache, thumbnail, notification, backup or crash-report locations.
- Permissions are least-privilege and requested at point of use.
- User-facing copy accurately explains storage, sharing and limitations.
- Documentation and ADRs updated; dependencies inventoried.
- Security review completed for features handling encryption, authentication or remote sharing.

## 6. Required test matrix

| Area | Test cases |
| --- | --- |
| Capture | Permission denied/granted, rotate camera, interruption, incoming call, low storage, app background, process death, repeated recording. |
| Crypto | Wrong key, modified chunk, wrong nonce, corrupted header, truncated file, restart during encryption, key invalidation, version migration. |
| Storage | Gallery scan, file manager, app backup, Android transfer, cache inspection, thumbnail inspection, app uninstall behaviour. |
| Authentication | Biometric cancel/failure, no biometric enrollment, device credential fallback, auto-lock, rapid background/foreground. |
| Sharing (later) | Wrong recipient, invite replay, expiry race, grant revoke mid-session, sender offline, recipient device revoked, NAT failure, TURN relay. |
| Backend | IDOR, rate limiting, account deletion, duplicate invite, concurrency on session cap, expired tokens, admin privilege review. |
| Privacy | Network capture in local-only mode, logs, crash reports, analytics/ad SDK scan, Data Safety declaration verification. |

## 7. Release checklist

1. Legal/policy: Indian counsel review of relevant laws, DPDP phased commencement, terms, privacy notice, consent, age-assurance and deletion workflow.
2. Google Play: current Developer Program Policy, restricted content, user data, Data Safety, account deletion, target API and billing requirements rechecked.
3. Security: threat model, key management review, penetration test and dependency/SBOM review.
4. Privacy: data inventory, retention schedule, vendor list, breach response, user rights and deletion workflow.
5. Android: backup exclusions, FLAG_SECURE, permission tests, app signing, release build and device matrix.
6. Operations: support contact, abuse reporting process (if sharing exists), incident response, monitoring and database backup policy.
7. Marketing: no sexual-content positioning, no hidden-camera language, no absolute no-leak/security claims.

## 8. Project tracking template

| Field | Description |
| --- | --- |
| Epic / feature ID | PRD requirement reference such as FR-003. |
| Owner | Named engineer/designer/reviewer. |
| Status | Not started / In progress / Blocked / In review / Done. |
| Dependencies | API, security decision, legal review or design dependency. |
| Acceptance evidence | Test report, screenshot, build link, review result or ADR. |
| Risk / decision | Open issue, owner and date needed. |

## 9. Key decision log template

Create an Architecture Decision Record (ADR) for decisions that are expensive to reverse. Minimum ADRs: media encryption format; KeyStore/key recovery behaviour; app backup strategy; account authentication; device identity; P2P media transport; revocation semantics; backend retention; subscription entitlement model.

## 10. First 10 engineering tasks

1. Create repository and native Android project using Kotlin/Compose.
2. Record minimum SDK/target SDK decision and test device plan.
3. Set up CI to run Gradle build, unit tests, lint and dependency scan.
4. Create module/package structure and navigation shell.
5. Implement onboarding copy and privacy principles screen.
6. Implement self-declared 18+ eligibility gate with versioned consent record.
7. Implement permission rationale screens and point-of-use permission requests.
8. Create CameraX proof of concept with visible foreground recording indicator.
9. Write crypto threat model and compare reviewed libraries/file formats before storing real media.
10. Build an encrypted-file proof of concept with Android Keystore and negative tests before integrating into full camera capture.

## References

- [Google Play Developer Program Policies (current policy page; includes restricted sexual content and child safety)](https://support.google.com/googleplay/android-developer/answer/18265346?hl=en)
- [Google Play account deletion requirements](https://support.google.com/googleplay/android-developer/answer/13327111?hl=en)
- [Google Play User Data policy](https://support.google.com/googleplay/android-developer/answer/10144311?hl=en)
- [Digital Personal Data Protection Rules, 2025 - official MeitY notification PDF](https://www.meity.gov.in/static/uploads/2025/11/53450e6e5dc0bfa85ebd78686cadad39.pdf)
- [DPDP Act commencement notification - Gazette of India, G.S.R. 843(E), 13 November 2025](https://egazette.gov.in/WriteReadData/2025/267647.pdf)
