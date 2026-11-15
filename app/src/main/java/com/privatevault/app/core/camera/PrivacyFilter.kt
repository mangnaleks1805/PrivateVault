package com.privatevault.app.core.camera

enum class PrivacyFilter(
    val id: String,
    val displayName: String,
    val badge: String,
    val description: String
) {
    NONE(
        id = "none",
        displayName = "Original",
        badge = "RAW",
        description = "Natural unfiltered capture"
    ),
    NOIR(
        id = "noir",
        displayName = "B&W Noir",
        badge = "B&W",
        description = "High-contrast monochrome discretion"
    ),
    VINTAGE(
        id = "vintage",
        displayName = "35mm Warm",
        badge = "WARM",
        description = "Warm golden hour film tone"
    ),
    COOL_NIGHT(
        id = "cool_night",
        displayName = "Night Tone",
        badge = "NIGHT",
        description = "Cool shadows & ambient discretion"
    ),
    PRIVACY_BLUR(
        id = "privacy_blur",
        displayName = "Soft Blur",
        badge = "BLUR",
        description = "Background softening for location privacy"
    ),
    PIXELATE(
        id = "pixelate",
        displayName = "Anonymize",
        badge = "MOSAIC",
        description = "Privacy mosaic pixelation"
    )
}
