# Application Flow Specification

**Screen map, user journeys, sharing sequence and exception paths**

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

## 1. Navigation model

```text
FIRST LAUNCH
  Welcome → Privacy principles → Adult eligibility → Permission education
                                      |
                                      v
HOME (LOCAL-ONLY MODE)
  [Private Camera] [Vault] [Security] [Settings]
       |               |        |          |
       v               v        v          v
  Camera preview    Vault list  Lock      Account (optional)
       |               |        |          |
  Start/Stop        Item detail  Biometrics Account create/login
       |               |                   |
  Encrypt/finalize   Play/Delete             Share (later)
                                           |
                                   Invite → Accept → Grant
                                           |
                                   P2P live-view session
                                           |
                                   Expire / Revoke / End
```

## 2. First-launch flow

1. Welcome screen: describe app as a secure personal camera and local private vault. No sexual-content positioning.
2. Privacy principles screen: local-only storage, no cloud backup, no ads, no hidden recording, no public sharing.
3. Adult eligibility screen: user confirms they are 18+. Record policy version and timestamp; do not label this identity verification.
4. Permission education: explain camera access and request CAMERA only when user selects camera. Request RECORD_AUDIO only if audio recording is chosen.
5. Security setup: explain biometric/device credential lock and ask the user to configure it.
6. Land on Home in local-only mode. Account creation is not a prerequisite.

## 3. Capture flow

1. User taps Private Camera.
2. App requests CAMERA permission only if not already granted; optional microphone permission is requested only for audio capture.
3. Camera preview opens with camera mode and clear recording controls.
4. User explicitly taps Start. Visible timer/status indicates recording.
5. User taps Stop. App finalizes and encrypts the recording.
6. On success, show saved confirmation and open item detail or return to camera.
7. On failure/low storage/interruption, show a clear error and recovery action. Never upload the recording.

## 4. Vault flow

1. User opens Vault and unlocks with biometric/device credential.
2. Vault list displays safe metadata such as date, type and duration; no plaintext thumbnails are required.
3. User selects an item. The app authorizes key use and streams decrypted data to the player.
4. Sensitive playback screen uses FLAG_SECURE and disables external intents.
5. User can rename using an encrypted local metadata field, categorize, or delete.
6. Delete confirmation explains that key material and local encrypted file will be removed; physical overwrite on flash storage is not guaranteed.

## 5. Optional account flow

1. User selects Private Sharing or Account.
2. Explain that local camera/vault works without an account; account is only for recipient-specific sharing and related features.
3. User creates account using the selected authentication method (prefer passkeys).
4. User completes 18+ eligibility flow. If future legal review requires stronger assurance, invoke the approved provider flow without retaining identity documents unnecessarily.
5. Register device identity/public key. Provide clear device name and revoke-device controls.
6. Account settings show devices, sharing permissions, subscription and delete-account option.

## 6. Private sharing flow - sender

1. Sender selects a local media item and taps Share Privately.
2. App explains: recipient can view in-app; no in-app download or forwarding; sender device must remain online; external capture cannot be guaranteed against.
3. Sender creates one-time invite for a specific recipient. Prefer QR code or short-lived invitation code; do not create public URLs.
4. Recipient accepts invite and explicitly confirms consent to receive access.
5. Sender verifies recipient identity/device pairing (e.g., QR/safety-number verification).
6. Sender configures view permission, expiry, optional view-session limit and allowed recipient device.
7. Create share grant. The media remains only on sender device.

## 7. Private sharing flow - recipient

1. Recipient opens invitation in app and authenticates.
2. Recipient reviews sender identity, item access request and expiry; explicitly accepts.
3. Backend checks account status, device status, grant status and eligibility.
4. Sender device must be online and approve the session.
5. Establish authenticated P2P connection; TURN may relay encrypted packets transiently.
6. Recipient views stream in a protected in-app playback screen. No MP4 file is saved or exported by the app.
7. On end, expiry or revocation, close connection and destroy session keys/buffers best effort.

## 8. Expiry and revocation flow

```text
Owner taps Revoke
   → Grant state = REVOKED
   → API rejects new session tickets
   → Sender device stops active stream
   → Recipient receives revoked/expired state
   → Ephemeral session state and keys are cleared best effort
   → Audit record stores event metadata only

LIMIT: already viewed or externally captured content cannot be clawed back.
```

## 9. Account deletion flow

1. User opens Settings → Account → Delete account.
2. Show what will be deleted: account profile, devices, invitations, grants, sessions and account-linked server metadata subject to lawful retention.
3. Offer a clear separate option to also erase the local encrypted vault. Explain that preserving local vault means it becomes local-only and remote sharing is removed, if technically supported.
4. Require re-authentication/confirmation.
5. Revoke active sessions and grants immediately; queue/complete server deletion.
6. Provide status/confirmation and link to external web deletion route.

## 10. Key exceptions and alternate paths

| Scenario | Expected path |
| --- | --- |
| User denies camera | Explain permission is required for camera feature; do not loop permission prompts. Vault can still be accessed. |
| User denies microphone | Allow silent video capture; do not require microphone for camera/video without audio. |
| No biometric enrolled | Offer secure device credential or clearly explained app PIN policy; never bypass file encryption. |
| Device offline | Local camera/vault work; account/sharing pages show unavailable. No cloud fallback. |
| Sender offline | Recipient sees “Sender device unavailable”; no copy is fetched from a server. |
| Invite expired/used | Reject invite and offer sender to create a new one. |
| Owner deletes local media | Revoke associated grants; no source remains to stream. |
| Keystore key invalidated | Explain unrecoverable state unless a previously designed recovery method exists; do not silently reset keys. |

## 11. Core navigation and screen list

- Welcome / privacy principles / 18+ eligibility
- Home
- Camera preview / recording / capture complete
- Vault list / filters / item detail / protected player
- Vault category management
- App lock and biometric setup
- Settings / privacy controls / storage status
- Account onboarding / authentication / device management (later)
- Invite creation / recipient acceptance / identity verification (later)
- Share permissions / active shares / viewing session (later)
- Subscription / purchase / restore (later)
- Delete account / delete local vault / privacy policy / terms

## References

- [Google Play Developer Program Policies (current policy page; includes restricted sexual content and child safety)](https://support.google.com/googleplay/android-developer/answer/18265346?hl=en)
- [Google Play account deletion requirements](https://support.google.com/googleplay/android-developer/answer/13327111?hl=en)
- [Google Play User Data policy](https://support.google.com/googleplay/android-developer/answer/10144311?hl=en)
- [Digital Personal Data Protection Rules, 2025 - official MeitY notification PDF](https://www.meity.gov.in/static/uploads/2025/11/53450e6e5dc0bfa85ebd78686cadad39.pdf)
- [DPDP Act commencement notification - Gazette of India, G.S.R. 843(E), 13 November 2025](https://egazette.gov.in/WriteReadData/2025/267647.pdf)
