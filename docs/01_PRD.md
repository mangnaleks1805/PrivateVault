# Product Requirements Document

**Product goals, scope, user requirements and release acceptance criteria**

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

## 1. Executive summary

Build a native Android application that captures personal photos and videos through a transparent, foreground camera interface and protects them in an encrypted, app-private local vault. The application is designed as a general-purpose personal privacy product, not a hidden-camera, surveillance, social-media or sexual-content product.

The initial release should prove one thing exceptionally well: a user can capture, encrypt, find, view and delete personal media locally without the media entering the public gallery or any cloud media store. Account creation is not required for the local vault.

> **Product risk:** Google Play approval is not guaranteed by describing the app as a general-purpose vault. Google evaluates the app’s actual functionality, content, positioning, store listing and foreseeable use. The app must not promote sexual gratification, hidden-camera behaviour, voyeurism, non-consensual content or child exploitation. Obtain a policy review before committing to a Play launch; do not market the product as an intimate/sex-video recorder.

## 2. Product vision and value proposition

Vision: give users a trustworthy place to capture and manage sensitive personal media while minimizing collection, exposure and unnecessary data processing.

Value proposition: “Capture privately. Store locally. Control access.” The product differentiates through local-first storage, per-file encryption, minimal permissions, no advertising, no media analytics and clear user control.

## 3. Problem statement

- Ordinary camera apps typically save media into shared gallery locations where it may appear in backups, gallery indexes, file pickers or other apps.
- Users who keep sensitive personal media need stronger separation between ordinary camera-roll content and a private collection.
- Traditional cloud vaults require users to trust a service provider with highly sensitive media. This product avoids a media-storage backend.
- Private sharing, if introduced, needs recipient-specific controls and clear limits rather than unrestricted file forwarding.

## 4. Goals and non-goals

| Goals | Non-goals |
| --- | --- |
| Foreground capture with clear camera/recording indicators. | Hidden, covert, disguised, background or screen-off recording. |
| Encrypted local storage protected by Android Keystore-backed key wrapping. | Cloud media storage, automatic backup or automatic upload. |
| Local vault usable without an account. | Public profiles, feeds, discovery, comments or social graph. |
| Strong app lock and sensitive-screen protections. | A promise that displayed media can never be copied externally. |
| Later recipient-specific, view-only remote streaming with revocation. | Unrestricted file transfer, public share links or in-app forwarding. |
| Minimal account/security metadata for optional remote sharing. | Ads, ad networks, media-inspecting analytics or unnecessary SDKs. |

## 5. Target users and primary use cases

| Persona | Need | Primary journey |
| --- | --- | --- |
| Privacy-conscious Android user | Store sensitive personal media away from the normal gallery. | Open app → unlock → capture → encrypt → view in vault → delete when no longer needed. |
| Adult sender | Allow one trusted adult to view one selected recording. | Create invite → recipient accepts → grant view-only access → sender streams → revoke/expire. |
| Adult recipient | View an explicitly shared item without receiving an unrestricted MP4 file. | Accept invite → authenticate → join viewing session → view in app → access ends on expiry/revocation. |
| Privacy-conscious buyer | Pay for useful advanced controls without ads or media collection. | Try local vault → review Premium features → purchase through Google Play Billing. |

## 6. Product principles

- Local-first by default: The camera and encrypted vault work without account creation. Accounts are required only for remote sharing and account-linked subscription capabilities, if those are implemented.
- No media cloud: The service does not provide cloud media storage, automatic media upload, cloud backup, public media links or server-side media indexing.
- Visible consensual capture only: Recording starts only after an explicit user action in a visible camera screen. No hidden, covert, disguised, background or screen-off recording.
- Adult-only product intent: The app is designed for adults. A self-declared 18+ screen is an eligibility gate, not proof of age. Any stronger age assurance must be decided through legal and Play policy review.
- Private recipient viewing: The later sharing concept is controlled, recipient-specific viewing streamed from the sender device. The sender must be online. TURN may relay encrypted packets transiently; it must not persist media.
- Revocation boundary: Revocation prevents future authorization and subsequent sessions. It cannot reverse prior viewing, erase a recipient’s memory, or prevent an external camera/compromised device from capturing displayed content.
- No export in the product flow: The app does not offer media export, system share-sheet forwarding, public links or recipient re-sharing. This is a product control, not an absolute anti-copy guarantee.
- No advertising or media analytics: No ads, ad SDKs, media-inspecting analytics, third-party media processors, or media content in crash logs. Essential account/security telemetry is minimized.
- Separate account and local-vault deletion: Deleting a remote account revokes remote access and deletes server account data according to disclosed retention. Users are clearly offered a separate choice to delete local vault contents; account deletion must not silently destroy local-only media.
- Security claims are bounded: Do not claim “100% leak-proof”, “impossible to copy” or “military-grade” without substantiation. Describe concrete controls and limitations.

## 7. Scope by release

| Release | Included | Explicitly excluded / gated |
| --- | --- | --- |
| MVP 1 - Local vault | Onboarding, 18+ eligibility screen, permission education, CameraX capture, encrypted local storage, vault listing/playback, app lock, delete, backup exclusion. | Accounts, remote sharing, cloud storage, ads, social features, public links, subscription complexity. |
| MVP 2 - Account foundation | Optional account for remote sharing, device registration, passkey-capable authentication, account deletion, minimal backend metadata. | Any media upload endpoint or server media storage. |
| MVP 3 - Sharing proof of concept | One-to-one device pairing, recipient acceptance, P2P connection proof, encrypted live viewing, session expiry/revocation. | Production launch until independent security review and Android capture-protection testing pass. |
| V1 - Controlled sharing | Recipient-specific grants, session limits, expiry, revoke, session auditing, no in-app export/forward. | Group sharing, public URLs, permanent recipient copies. |
| Monetization | Free and Premium local/security features; later sharing entitlement if policy-cleared. | Ads, monetized sexual-content promotion, pay-to-share explicit content positioning. |

## 8. Functional requirements

| ID | Requirement | Priority | Acceptance criteria |
| --- | --- | --- | --- |
| FR-001 | The user can complete onboarding and confirm adult eligibility. | Must | Age gate is explicit; age declaration is versioned; it is not labelled as verified identity. |
| FR-002 | The user can capture photos/videos from a visible foreground camera screen. | Must | Capture requires explicit action; recording indicator and timer are visible; stop action works. |
| FR-003 | Media is encrypted before durable storage. | Must | Stored media is not playable as plaintext; encryption/decryption tests pass; no plaintext temp file remains after normal completion. |
| FR-004 | Media stays in app-private storage and out of public gallery. | Must | No MediaStore insertion; no gallery visibility; backup exclusion verified. |
| FR-005 | The user can unlock the vault securely. | Must | Biometric/device credential flow works; app relocks on configured lifecycle events. |
| FR-006 | The user can list, play, organize and delete local media. | Must | Vault actions work offline; metadata contains no plaintext media bytes. |
| FR-007 | No ad or analytics SDK can access media. | Must | Dependency/SBOM and network inspection show no ad SDK and no media telemetry. |
| FR-008 | Optional accounts support device registration and account deletion. | Later | In-app deletion path and external web deletion route exist; remote grants are revoked. |
| FR-009 | Sender can create recipient-specific viewing grants. | Later | Recipient must accept; each grant has expiry/revocation and no download/forward action. |
| FR-010 | Remote viewing streams from sender device without permanent media upload. | Later / gated | Sender must be online; no media upload endpoint exists; relay handles encrypted packets transiently. |
| FR-011 | Revocation blocks new or continuing authorized access promptly. | Later | Server and sender stop authorizing sessions; test documents propagation and already-rendered content limitation. |
| FR-012 | Users can purchase/restore eligible Premium entitlements. | Later | Play Billing purchase is verified; entitlement state is recoverable without a media account where supported. |

## 9. Non-functional requirements

| Area | Requirement / target |
| --- | --- |
| Security | Use reviewed cryptographic primitives and a documented key hierarchy; no custom cryptographic protocol; threat model and independent review before remote sharing launch. |
| Privacy | Collect only data needed for the stated feature; no media names, thumbnails, media bytes, decrypted content or decryption keys on backend. |
| Reliability | Interrupted recording must not corrupt existing vault items; use atomic file finalization and recovery metadata. |
| Performance | Vault list should load without decrypting every full-size video. Playback should decrypt/stream only required chunks. |
| Accessibility | Readable contrast, scalable text, screen-reader labels, touch targets and accessible permission explanations. |
| Compatibility | Define minimum Android API after checking CameraX, BiometricPrompt and Keystore requirements; test representative low/mid/high-end devices. |
| Observability | No media content in logs. Local diagnostics are opt-in; backend logs contain only minimal security/account metadata. |
| Maintainability | Modular Kotlin codebase, dependency inventory, automated tests, documented crypto file format and migrations. |

## 10. User journeys and success measures

- First-use completion: user understands local-only storage and grants camera/mic permissions only when initiating capture.
- Capture-to-vault success: a completed recording appears in the vault and never in the normal gallery.
- Offline use: core camera/vault features work with network disabled.
- Data-at-rest test: copying the app data directory does not reveal playable media or plaintext thumbnails.
- Deletion: delete action removes the media record and cryptographic key material according to the documented best-effort secure-deletion model.
- Sharing gate: remote sharing cannot be enabled until threat model, P2P prototype, permission tests and external security review pass.

## 11. Monetization requirements

Use a no-ad freemium model. Do not paywall basic security, consent safeguards, account deletion, local encryption or essential privacy controls. Premium may offer advanced vault organization, multiple vault spaces, security customization, device management or convenience features. A sharing tier is gated on Play policy review. Pricing is an experiment, not a committed requirement.

## 12. Risks, assumptions and open decisions

| Risk / decision | Impact | Mitigation / decision needed |
| --- | --- | --- |
| Google Play eligibility for the actual product and its foreseeable use | Launch rejection or later removal. | Keep general-purpose privacy positioning; do not promote sexual content; obtain current policy review before launch. |
| 18+ confirmation is not age verification | May be insufficient for product/legal context. | Treat as self-declaration only; obtain legal advice on proportionate age assurance. |
| Android device compromise / external camera capture | Cannot guarantee no copies. | Bound marketing claims; FLAG_SECURE and device integrity signals are defence-in-depth only. |
| No cloud backup means loss of device/key may mean loss of media | High user impact. | Warn during onboarding; design optional user-controlled encrypted migration only after key recovery review. |
| P2P live viewing compatibility and battery use | Sharing may fail on some networks/devices. | Build a time-boxed technical spike before committing to production scope. |
| Revocation after recipient has viewed content | Cannot undo prior viewing. | Use clear disclosure; short-lived session keys; no durable recipient file; stop future frames/sessions. |
| DPDP Act/Rules phased commencement and other Indian laws | Compliance duties vary by launch date and processing model. | Indian privacy counsel to prepare launch-date applicability matrix and notices/consent/deletion procedures. |

## 13. Launch acceptance gate

1. MVP security threat model and data-flow inventory approved.
2. No unencrypted media or thumbnail in persistent storage, logs, backups or crash reports.
3. App data excluded from Android backup/restore pathways.
4. Recording only occurs from clear foreground UI with visible status.
5. No media-network endpoint exists in local-only release.
6. Privacy policy, terms, age-screen wording and Play listing reviewed by Indian counsel and policy specialist.
7. Google Play Data safety answers match actual SDK/network behaviour.
8. Account deletion path (if account creation exists) is available in app and on a designated web page.
9. Independent review of cryptographic implementation completed before advertising strong security claims.

## References

- [Google Play Developer Program Policies (current policy page; includes restricted sexual content and child safety)](https://support.google.com/googleplay/android-developer/answer/18265346?hl=en)
- [Google Play account deletion requirements](https://support.google.com/googleplay/android-developer/answer/13327111?hl=en)
- [Google Play User Data policy](https://support.google.com/googleplay/android-developer/answer/10144311?hl=en)
- [Digital Personal Data Protection Rules, 2025 - official MeitY notification PDF](https://www.meity.gov.in/static/uploads/2025/11/53450e6e5dc0bfa85ebd78686cadad39.pdf)
- [DPDP Act commencement notification - Gazette of India, G.S.R. 843(E), 13 November 2025](https://egazette.gov.in/WriteReadData/2025/267647.pdf)
