# Backend Schema Specification

**Backend entities, data dictionary, starter DDL, API and lifecycle rules**

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

## 1. Backend boundary

The backend is optional for local capture and vault use. It exists only for account identity, device registration, recipient invitations, share grants/sessions, account deletion and subscription verification. The backend must not accept or store media files, thumbnails, plaintext metadata about media content, or media decryption keys.

> **Hard rule:** There must be no /upload, /media, /files, /thumbnail or object-storage path for user recordings in this product. If remote viewing needs connectivity, transport is peer-to-peer with an optional transient encrypted TURN relay; the relay is not a media storage service.

## 2. Proposed backend stack

| Layer | Recommendation | Reason |
| --- | --- | --- |
| API | TypeScript/NestJS or Kotlin/Ktor modular monolith. | Keep the first backend small and auditable; choose one stack, not both. |
| Database | PostgreSQL. | Relational constraints for accounts, devices, grants and deletion state. |
| Ephemeral coordination | WebSocket signalling with in-memory/short-TTL store; Redis only if scaling requires it. | Avoid durable signalling history. |
| P2P connectivity | STUN/TURN service such as coturn. | Connectivity assistance; encrypted packets only; no recording/persistence. |
| Authentication | Passkeys/WebAuthn or established identity flow. | Avoid custom password handling; device keys remain on-device. |
| Payments | Google Play Billing with server-side verification where required. | Entitlement validation and purchase restoration. |
| Hosting | Small API + managed PostgreSQL; no media object storage bucket. | Minimize attack surface and data held by operator. |

## 3. Logical entity relationship

```text
ACCOUNT 1 ─── * DEVICE
ACCOUNT 1 ─── * PASSKEY_CREDENTIAL
ACCOUNT 1 ─── * SHARE_INVITATION (created_by)
ACCOUNT 1 ─── * SHARE_GRANT (owner)
ACCOUNT 1 ─── * SHARE_GRANT (recipient)
SHARE_GRANT 1 ─── * SHARE_SESSION
ACCOUNT 1 ─── * SUBSCRIPTION_ENTITLEMENT
ACCOUNT 1 ─── * SECURITY_EVENT
ACCOUNT 1 ─── * DELETION_REQUEST

NO MEDIA TABLE / NO MEDIA BLOB / NO DECRYPTION-KEY TABLE
```

## 4. Data dictionary

### 4.1 account

| Field | Type | Rules / notes |
| --- | --- | --- |
| id | UUID | Primary key; random opaque identifier. |
| status | ENUM | ACTIVE, SUSPENDED, DELETION_PENDING, DELETED. |
| adult_eligibility_status | ENUM | UNCONFIRMED, SELF_DECLARED_18_PLUS, REVIEW_REQUIRED. Not identity proof. |
| age_gate_version | VARCHAR | Version of age-screen wording/policy accepted. |
| age_gate_confirmed_at | TIMESTAMPTZ | Timestamp of self-declaration; do not store DOB by default. |
| created_at / updated_at | TIMESTAMPTZ | UTC timestamps. |
| deleted_at | TIMESTAMPTZ NULL | Set only after deletion workflow completion/retention handling. |

### 4.2 device

| Field | Type | Rules / notes |
| --- | --- | --- |
| id | UUID | Primary key. |
| account_id | UUID FK | Owner account; index. |
| device_label | VARCHAR | User-editable generic label; avoid collecting excessive fingerprint data. |
| platform | VARCHAR | Android plus supported OS/API metadata. |
| public_key | BYTEA/TEXT | Public device identity key only; private key never leaves device. |
| key_algorithm / key_version | VARCHAR | Versioned protocol metadata selected after crypto review. |
| status | ENUM | ACTIVE, REVOKED, LOST. |
| registered_at / last_seen_at | TIMESTAMPTZ | Operational timestamps. |
| revoked_at | TIMESTAMPTZ NULL | Revocation timestamp. |

### 4.3 passkey_credential

| Field | Type | Rules / notes |
| --- | --- | --- |
| id | UUID | Primary key. |
| account_id | UUID FK | Credential owner. |
| credential_id | BYTEA | Unique WebAuthn credential identifier. |
| public_key | BYTEA | Public key only. |
| sign_count | BIGINT | Authenticator counter when applicable; handle authenticator semantics correctly. |
| transports | JSONB | Minimal supported transport metadata. |
| created_at / last_used_at | TIMESTAMPTZ | Timestamps. |
| revoked_at | TIMESTAMPTZ NULL | Credential revocation. |

### 4.4 share_invitation

| Field | Type | Rules / notes |
| --- | --- | --- |
| id | UUID | Primary key. |
| created_by_account_id | UUID FK | Sender. |
| created_by_device_id | UUID FK | Sender device. |
| invite_token_hash | BYTEA | Store hash, not raw invite token. |
| intended_recipient_hint | VARCHAR NULL | Optional minimal hint; avoid phone book/contact scraping. |
| status | ENUM | PENDING, ACCEPTED, EXPIRED, REVOKED. |
| expires_at | TIMESTAMPTZ | Short TTL. |
| accepted_by_account_id | UUID FK NULL | Recipient after acceptance. |
| accepted_at / created_at | TIMESTAMPTZ | Timestamps. |

### 4.5 share_grant

| Field | Type | Rules / notes |
| --- | --- | --- |
| id | UUID | Primary key. |
| owner_account_id | UUID FK | Sender account. |
| owner_device_id | UUID FK | Device holding original media. |
| recipient_account_id | UUID FK | One specific recipient. |
| recipient_device_id | UUID FK NULL | Optional device restriction. |
| opaque_media_ref | VARCHAR | Random reference known to owner client; not a filename or content description. |
| permission | ENUM | VIEW_STREAM_ONLY. |
| status | ENUM | PENDING, ACTIVE, EXPIRED, REVOKED, SOURCE_DELETED. |
| created_at / expires_at | TIMESTAMPTZ | UTC timestamps. |
| max_sessions / sessions_used | INTEGER NULL/INTEGER | Optional limit; enforce transactionally. |
| revoked_at | TIMESTAMPTZ NULL | Revocation timestamp. |
| policy_version | VARCHAR | Version of sharing disclosures accepted. |

### 4.6 share_session

| Field | Type | Rules / notes |
| --- | --- | --- |
| id | UUID | Primary key; short-lived session ID. |
| grant_id | UUID FK | Associated grant. |
| owner_device_id / recipient_device_id | UUID FK | Both endpoints. |
| status | ENUM | REQUESTED, AUTHORIZED, CONNECTED, CLOSED, FAILED, REVOKED. |
| authorized_at / started_at / ended_at | TIMESTAMPTZ NULL | Operational lifecycle only. |
| end_reason | ENUM NULL | USER_ENDED, EXPIRED, REVOKED, NETWORK_FAILURE, DEVICE_REVOKED. |
| ephemeral_ticket_hash | BYTEA NULL | Hash of short-lived ticket if server issues one. |
| ttl_expires_at | TIMESTAMPTZ | Automatic expiry; no media data. |

### 4.7 subscription_entitlement

| Field | Type | Rules / notes |
| --- | --- | --- |
| id | UUID | Primary key. |
| account_id | UUID FK NULL | Null allowed if local-only purchase restore model does not require an account. |
| store | ENUM | GOOGLE_PLAY. |
| product_id | VARCHAR | Configured product identifier. |
| purchase_token_ciphertext | BYTEA NULL | If backend validation/reconciliation requires storage, encrypt with server KMS; never log. |
| purchase_token_hash | BYTEA | Uniqueness/lookup aid. |
| status | ENUM | PENDING, ACTIVE, GRACE, EXPIRED, REVOKED. |
| valid_from / expires_at | TIMESTAMPTZ NULL | Entitlement window. |
| last_verified_at | TIMESTAMPTZ | Latest store verification. |

### 4.8 security_event and deletion_request

| Entity | Fields | Retention / constraints |
| --- | --- | --- |
| security_event | id, account_id nullable, device_id nullable, event_type, result, created_at, correlation_id | No media metadata/content. Bounded, documented retention. Restrict access. |
| deletion_request | id, account_id, status, requested_at, completed_at, retention_reason_code nullable | Track deletion workflow; do not retain unnecessary request payloads. |
| consent_record (optional) | id, account_id nullable, notice_version, purpose, accepted_at, withdrawal_at | Record version and purpose; do not store full device data or media data. |

## 5. PostgreSQL starter DDL (illustrative)

This is a starting schema for review, not a migration to run unchanged. Add enum migrations, indexes, constraints, retention jobs and security review before production.

```text
CREATE TABLE account (
  id UUID PRIMARY KEY,
  status TEXT NOT NULL CHECK (status IN ('ACTIVE','SUSPENDED','DELETION_PENDING','DELETED')),
  adult_eligibility_status TEXT NOT NULL,
  age_gate_version TEXT,
  age_gate_confirmed_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  deleted_at TIMESTAMPTZ
);

CREATE TABLE device (
  id UUID PRIMARY KEY,
  account_id UUID NOT NULL REFERENCES account(id),
  device_label TEXT NOT NULL,
  platform TEXT NOT NULL DEFAULT 'ANDROID',
  public_key BYTEA NOT NULL,
  key_algorithm TEXT NOT NULL,
  key_version INTEGER NOT NULL,
  status TEXT NOT NULL CHECK (status IN ('ACTIVE','REVOKED','LOST')),
  registered_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  last_seen_at TIMESTAMPTZ,
  revoked_at TIMESTAMPTZ
);
CREATE INDEX ix_device_account_status ON device(account_id, status);

CREATE TABLE share_grant (
  id UUID PRIMARY KEY,
  owner_account_id UUID NOT NULL REFERENCES account(id),
  owner_device_id UUID NOT NULL REFERENCES device(id),
  recipient_account_id UUID NOT NULL REFERENCES account(id),
  recipient_device_id UUID REFERENCES device(id),
  opaque_media_ref TEXT NOT NULL,
  permission TEXT NOT NULL CHECK (permission = 'VIEW_STREAM_ONLY'),
  status TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  expires_at TIMESTAMPTZ NOT NULL,
  max_sessions INTEGER,
  sessions_used INTEGER NOT NULL DEFAULT 0,
  revoked_at TIMESTAMPTZ,
  policy_version TEXT NOT NULL
);
CREATE INDEX ix_grant_recipient_status ON share_grant(recipient_account_id, status, expires_at);
CREATE INDEX ix_grant_owner_status ON share_grant(owner_account_id, status);

-- Deliberately absent: media_blob, media_upload, thumbnail_blob, decryption_key.
```

## 6. API data contracts and authorization rules

- Every share-grant read/write is authorized against the authenticated account and registered device; never trust client-supplied owner IDs.
- Use opaque UUIDs and deny by default. Prevent IDOR by checking both owner and recipient relationships in every query.
- Invitation tokens are random, single-use, short-lived and stored only as hashes. Rate-limit create/accept/redeem.
- Share-session tickets are short-lived, audience-bound and device-bound. Prevent replay.
- Revocation is idempotent and immediately changes server authorization state. Notify connected peers so sender can terminate the stream.
- Signalling messages are size-limited, schema-validated, short-lived and never stored as recordings.
- No endpoint accepts file bytes or returns a media URL.

## 7. Data lifecycle and retention

| Data | When created | Retention rule |
| --- | --- | --- |
| Account | Optional signup. | Until user requests deletion, subject to defined legal/financial retention. |
| Device key/public metadata | Device registration. | Until revoked/account deletion; retain only required audit evidence. |
| Invitation | Share invite. | Short TTL; delete/expire after acceptance/expiry and minimal abuse window. |
| Grant | Owner creates permission. | Until revoked/expired and any short dispute/security retention period ends. |
| Session | Viewing session. | Ephemeral. Retain minimal timestamps/result only for short security period. |
| Security events | Relevant security actions. | Documented bounded retention; no media information. |
| Purchase token | Billing validation. | Retain only if needed for store reconciliation; encrypt and limit access. |
| Local media | Camera capture. | Remains local until user deletes it or selects account+vault wipe. No server retention. |

## 8. Account deletion sequence

1. Authenticate request and confirm intent.
2. Mark account DELETION_PENDING; block new sessions and invitations.
3. Revoke every active grant, invitation, device session and passkey.
4. Close active share sessions and invalidate tickets.
5. Delete or anonymize backend account/device/grant metadata according to documented retention and applicable law.
6. Cancel or retain purchase records only as required by billing/legal policy; do not claim subscriptions are cancelled unless the Play flow confirms it.
7. Offer separate local vault deletion choice in app. The external web deletion form must explain how to complete local data deletion when the device is unavailable.
8. Return completion status and retain only a minimal deletion receipt if lawful/necessary.

## 9. Security controls and operational requirements

- Database roles least privilege; no direct client database access.
- TLS for APIs and signalling; encryption at rest for metadata database and backups.
- Encrypt sensitive billing tokens with managed KMS; never log tokens.
- Secret manager for server credentials; rotate keys and restrict operators.
- Rate limits for auth, invitations, grant creation, signalling and deletion endpoints.
- Audit admin access and restrict production support access.
- No production media fixtures; no access path to user media because server does not store media.
- Data breach response process and incident contact documented.

## 10. Open decisions before schema freeze

1. Choose backend runtime and deployment model.
2. Choose passkey implementation and recovery model.
3. Select P2P library/protocol and authenticated device-key exchange after prototype and review.
4. Decide whether one recipient can use multiple authorized devices or exactly one device in V1.
5. Set concrete retention periods with Indian counsel.
6. Decide how Play purchase restoration works without requiring a remote account.
7. Confirm current Play account-deletion, Data Safety and DPDP obligations for release date.

## References

- [Google Play Developer Program Policies (current policy page; includes restricted sexual content and child safety)](https://support.google.com/googleplay/android-developer/answer/18265346?hl=en)
- [Google Play account deletion requirements](https://support.google.com/googleplay/android-developer/answer/13327111?hl=en)
- [Google Play User Data policy](https://support.google.com/googleplay/android-developer/answer/10144311?hl=en)
- [Digital Personal Data Protection Rules, 2025 - official MeitY notification PDF](https://www.meity.gov.in/static/uploads/2025/11/53450e6e5dc0bfa85ebd78686cadad39.pdf)
- [DPDP Act commencement notification - Gazette of India, G.S.R. 843(E), 13 November 2025](https://egazette.gov.in/WriteReadData/2025/267647.pdf)
