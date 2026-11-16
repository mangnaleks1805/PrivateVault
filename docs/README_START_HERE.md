# Secure Personal Camera & Private Vault - Specification Pack

Prepared 16 November 2026 | 0.9 - Draft for product, engineering and legal review

## Documents (read in this order)
1. 01_PRD - product purpose, scope and requirements
2. 02_TRD - technical architecture and security requirements
3. 03_APP_FLOW - screen map and journeys
4. 04_UI_UX_DESIGN_BRIEF - design direction and screen-level requirements
5. 05_BACKEND_SCHEMA - metadata-only backend design and API boundaries
6. 06_IMPLEMENTATION_PLAN - gated delivery plan and QA

Each document is provided as DOCX and Markdown. Markdown is convenient for Antigravity/agent context; DOCX is suitable for review and editing.

## Critical product decisions
- Local camera and encrypted vault must work without account creation.
- No cloud media storage, automatic uploads or cloud media backup.
- No hidden, covert, disguised, background or screen-off recording.
- Recording is explicit, visible and consensual.
- Product is intended for adults; self-declared 18+ is not verified age identity.
- No ads, public feed, public sharing links, media-inspecting analytics or third-party media access.
- Later sharing is recipient-specific, view-only in-app streaming from the sender device; sender must be online.
- Revocation blocks future access; it cannot undo prior viewing or prevent an external camera/compromised device.
- Backend stores account and permission metadata only; no media bytes, thumbnails or decryption keys.
- Google Play approval is not guaranteed; legal/policy review is a release gate.

## Recommended Antigravity sequence
1. Provide 01_PRD.md and ask the agent to summarize requirements and list conflicts/unknowns.
2. Provide 02_TRD.md and ask for architecture review and security questions.
3. Provide 03_APP_FLOW.md and 04_UI_UX_DESIGN_BRIEF.md for navigation and UI implementation.
4. Provide 05_BACKEND_SCHEMA.md only when account/sharing backend work begins.
5. Use 06_IMPLEMENTATION_PLAN.md to enforce phase gates.
6. Start with Phase 1 / local vault; do not let the agent jump to cloud, account or sharing features.

## Policy/legal references
- Google Play Developer Program Policies: https://support.google.com/googleplay/android-developer/answer/18265346?hl=en
- Google Play account deletion: https://support.google.com/googleplay/android-developer/answer/13327111?hl=en
- Google Play User Data: https://support.google.com/googleplay/android-developer/answer/10144311?hl=en
- Government of India DPDP Rules, 2025 official PDF: https://www.meity.gov.in/static/uploads/2025/11/53450e6e5dc0bfa85ebd78686cadad39.pdf
- Gazette commencement notification: https://egazette.gov.in/WriteReadData/2025/267647.pdf
