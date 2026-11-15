package com.privatevault.app.core.camera

/**
 * Top 10 most viral Snapchat-inspired aesthetic & discretion filters,
 * implemented 100% offline with zero cloud access, zero telemetry, and hardware isolation.
 */
enum class PrivacyFilter(
    val id: String,
    val displayName: String,
    val badge: String,
    val category: String,
    val description: String
) {
    ORIGINAL(
        id = "original",
        displayName = "Original",
        badge = "RAW",
        category = "Natural",
        description = "Pure authentic camera feed"
    ),
    CLEAN_GLOW(
        id = "clean_glow",
        displayName = "Clean Glow",
        badge = "GLOW",
        category = "Snapchat #1",
        description = "Bilateral skin smoothing, poreless clarity & eye brightness"
    ),
    GOLDEN_HOUR(
        id = "golden_hour",
        displayName = "Golden Hour",
        badge = "SUN",
        category = "Viral Trend",
        description = "Sun-kissed amber light, warm blush & radial vignette"
    ),
    GLASS_SKIN(
        id = "glass_skin",
        displayName = "Glass Skin",
        badge = "DEW",
        category = "K-Beauty",
        description = "Ultra-hydrated, reflective luminous finish"
    ),
    VINTAGE_90S(
        id = "vintage_90s",
        displayName = "90s Polaroid",
        badge = "35MM",
        category = "Retro Film",
        description = "Analog matte blacks, soft light leak & border vignette"
    ),
    VOGUE_NOIR(
        id = "vogue_noir",
        displayName = "Vogue Noir",
        badge = "NOIR",
        category = "High Fashion",
        description = "High-contrast silver highlights & velvety deep blacks"
    ),
    BLUSH_PEACH(
        id = "blush_peach",
        displayName = "Blush Peach",
        badge = "PEACH",
        category = "Soft Portrait",
        description = "Soft radiant cheek warmth with gentle complexion tone"
    ),
    CYBER_MOODY(
        id = "cyber_moody",
        displayName = "Teal & Orange",
        badge = "CYBER",
        category = "Cinematic",
        description = "Hollywood teal shadows with rich golden skin highlights"
    ),
    ETHEREAL_DREAM(
        id = "ethereal_dream",
        displayName = "Ethereal Bloom",
        badge = "BLOOM",
        category = "Dreamy Aura",
        description = "Diffused highlight bloom & soft-focus aesthetic glow"
    ),
    NIGHT_BOOST(
        id = "night_boost",
        displayName = "Night Vision",
        badge = "NIGHT",
        category = "Low-Light",
        description = "Ambient shadow lift & low-light clarity enhancement"
    ),
    ANONYMIZE(
        id = "anonymize",
        displayName = "Anonymize",
        badge = "MOSAIC",
        category = "Discretion",
        description = "Consensual privacy mosaic shield"
    )
}
