# UI/UX Design Brief

**Information architecture, screen requirements, design principles and microcopy**

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

## 1. Design objective

Create a premium, calm, trustworthy Android experience for a secure personal camera and encrypted local vault. The interface must feel transparent and deliberate. It must not imitate a calculator, system utility, camera-off screen or any other deceptive interface.

## 2. Brand and positioning

| Attribute | Direction |
| --- | --- |
| Positioning | Privacy-first personal camera and encrypted media vault. |
| Tone | Clear, restrained, factual and reassuring. Avoid absolute security claims. |
| Visual style | Minimal, high-contrast, understated, modern Android-native patterns. |
| Avoid | Sensational adult imagery, sexual wording, voyeurism cues, hidden-camera aesthetics, fake system UI, stealth metaphors. |
| Suggested product copy | “Capture privately. Store locally. Control access.” |

## 3. Design principles

- Transparency first: show camera state, recording state, permission purpose, storage behaviour and sharing limits.
- Privacy by default: no media thumbnails on notification surfaces, lock-screen previews or recents screenshots where platform allows.
- Few permissions: ask only at point of use and explain why.
- Local-first clarity: visibly distinguish local vault features from optional online account/sharing features.
- User control: deletion, revocation, device management and privacy policy are easy to find.
- Honest boundaries: never promise that screenshots, external cameras or compromised devices are impossible.
- Accessible by default: support dynamic type, TalkBack, sufficient contrast and large touch targets.

## 4. Information architecture

| Destination | Purpose | Primary actions |
| --- | --- | --- |
| Home | Entry point and concise status of local vault. | Open camera, open vault, review storage/security state. |
| Camera | Transparent photo/video capture. | Switch camera, choose photo/video, start/stop, save. |
| Vault | Local encrypted media library. | Browse, filter, open, categorize, delete. |
| Sharing (later) | Recipient-specific access management. | Invite, approve, set expiry, revoke, view session status. |
| Security | App lock and device security controls. | Configure authentication, auto-lock, view devices, revoke device. |
| Settings | Account, privacy, help and legal surfaces. | Policy, permissions, account deletion, subscription, support. |

## 5. Screen-by-screen brief

### 5.1 Welcome and onboarding

- Headline: “A private place for your personal media.”
- Subtext: “Your vault is stored on this device. No cloud media backup.”
- Show three simple principles: local storage, encryption, visible consensual capture.
- Adult eligibility: “I confirm I am 18 or older.” Do not use “age verified” unless a separate approved verification process exists.
- Links to Privacy Policy and Terms must be visible before completion.

### 5.2 Home

- Primary action: “Open camera”. Secondary action: “Open vault”.
- Show local vault state and storage usage without showing sensitive media thumbnails.
- If network/account is enabled, label it clearly as optional and separate from the local vault.
- No promotional banners that reveal sensitive app usage in notification previews.

### 5.3 Camera

- Camera preview occupies the primary area.
- Mode selector: Photo / Video.
- Large, unmistakable capture button.
- Video mode has visible recording timer and clear stop control.
- Persistent foreground notification/status behaviour follows Android requirements; do not disguise recording.
- Permission rationale appears before request; permission is requested only when the corresponding feature is used.

### 5.4 Vault list and detail

- Default list uses generic icons/metadata rather than plaintext thumbnails.
- Sort by date/type; allow user-defined local categories.
- Show local storage size and item count.
- Item detail exposes Play, Rename/Organize, Share Privately (later, gated), Delete.
- No external share icon, download button or “Save to Gallery” action.

### 5.5 Protected playback

- Apply FLAG_SECURE to the player and other sensitive screens.
- Minimize lock-screen, recents and notification content.
- Offer in-app playback controls with clear owner and sharing status.
- For remote recipient viewing, show sender name/account identity, expiry, and a concise notice that external recording cannot be fully prevented.
- On expiry/revoke/disconnect, stop playback and show a clear state.

### 5.6 Security and account

- Biometric setup explains that biometrics unlock access but cannot recover lost encryption keys.
- Auto-lock choices should be simple and safe.
- Registered device list includes current device, device label, last activity and revoke action.
- Account deletion explains server-side deletion and separate local vault deletion choice.

## 6. Reusable components and states

| Component | States / behaviour |
| --- | --- |
| Primary button | Default, pressed, disabled, loading; destructive actions visually distinct. |
| Permission education sheet | Not requested, rationale shown, granted, denied, permanently denied. |
| Recording indicator | Idle, recording, paused only if supported, finalizing, saved, error. |
| Vault item | Encrypted/available, decrypting, playable, incomplete, unavailable key, deleting. |
| Share grant card | Pending, active, expiring, expired, revoked, sender offline, device revoked. |
| Connectivity banner | Offline/local-only, connecting, peer connected, session failed; never imply cloud fallback. |
| Security warning | Key invalidated, device untrusted, app outdated, integrity risk; avoid claiming device integrity is perfect. |

## 7. Content and microcopy guardrails

| Use | Avoid |
| --- | --- |
| “Encrypted on this device.” | “Impossible to hack.” |
| “No cloud media backup.” | “Your data can never leave your phone” if account/network metadata exists. |
| “View-only in this app; forwarding is disabled here.” | “Recipient cannot copy this video.” |
| “Revoking stops future access.” | “Revoking deletes what the recipient has already seen.” |
| “18+ eligibility confirmation.” | “Age verified” when only self-declaration is used. |
| “Designed to reduce unauthorized access.” | “100% leak-proof” or “unbreakable.” |

## 8. Accessibility and platform behaviour

- Use semantic Compose elements and meaningful content descriptions.
- Minimum touch target follows Android accessibility guidance; test with large fonts.
- Do not encode important states by color alone.
- Support dark/light theme with contrast checks.
- Use system biometric prompt and permission dialogs.
- Apply FLAG_SECURE to relevant activities/windows and validate on supported devices; do not present as a complete capture prevention guarantee.
- Ensure app switcher preview is blank/obscured for vault/player screens where supported.

## 9. Design deliverables expected from designer/agent

1. Low-fidelity user flow for each release phase.
2. High-fidelity screen designs for onboarding, Home, Camera, Vault, playback, Security, Settings and later Sharing.
3. Design system tokens: typography, spacing, color, elevation, shapes, icons and states.
4. Light and dark themes.
5. Empty, loading, error, permission-denied, offline, key-invalidated and deletion-confirmation states.
6. Clickable prototype for the local-vault MVP.
7. Accessibility review and Android device-size checks.

## 10. Visual QA checklist

- No screen suggests stealth or covert operation.
- Recording status is obvious whenever capture is active.
- No sensitive title/thumbnail appears in notifications or recents preview.
- Permission request happens at point of use.
- Offline local vault state is understandable.
- Delete and revoke actions explain consequences.
- Sharing disclosures are readable and not buried in terms.
- No false guarantee about anti-copy or revocation.

## References

- [Google Play Developer Program Policies (current policy page; includes restricted sexual content and child safety)](https://support.google.com/googleplay/android-developer/answer/18265346?hl=en)
- [Google Play account deletion requirements](https://support.google.com/googleplay/android-developer/answer/13327111?hl=en)
- [Google Play User Data policy](https://support.google.com/googleplay/android-developer/answer/10144311?hl=en)
- [Digital Personal Data Protection Rules, 2025 - official MeitY notification PDF](https://www.meity.gov.in/static/uploads/2025/11/53450e6e5dc0bfa85ebd78686cadad39.pdf)
- [DPDP Act commencement notification - Gazette of India, G.S.R. 843(E), 13 November 2025](https://egazette.gov.in/WriteReadData/2025/267647.pdf)
