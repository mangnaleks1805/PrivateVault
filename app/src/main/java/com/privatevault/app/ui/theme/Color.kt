package com.privatevault.app.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════════════
// OPTION 1: MONOCHROME TITANIUM DESIGN SYSTEM PALETTE
// Apple Pro & Leica Minimalist Luxury (Velvet Dark Gray, Crisp White, Brushed Silver)
// High contrast, OLED-friendly, tactile precision hardware aesthetics
// ═══════════════════════════════════════════════════════════════════════════

// Primary Canvas & Glass Surfaces (No real-time blur, 88-92% opaque for 60fps camera)
val TitaniumCanvas = Color(0xFF0D0E11)       // Deep velvet onyx base (AMOLED friendly, rich depth)
val TitaniumSurface = Color(0xFF15161A)      // Solid elevated surface for cards and toolbars
val TitaniumSurfaceGlass = Color(0xEB1A1B22) // 92% opaque dark frosted glass
val TitaniumCard = Color(0xFF181A20)         // Card background

// Titanium Accents & Pure Contrast
val TitaniumWhite = Color(0xFFFFFFFF)        // Primary CTA, active shutter, high-contrast headings
val TitaniumSilver = Color(0xFFE4E4E7)       // Brushed metal borders, icons, secondary highlights
val TitaniumPlatinum = Color(0xFFD4D4D8)     // Soft metallic badge/pill text
val TitaniumTextDark = Color(0xFF0D0E11)     // High-contrast dark text on TitaniumWhite buttons (18:1 AAA)

// Concentric Shutter Rings & Hardware Accents
val ShutterRingOuter = Color(0x80FFFFFF)    // Outer hairline concentric shutter ring
val ShutterRingInner = Color(0xFFFFFFFF)    // Inner physical tactile button surface

// Status Accents (Minimalist Leica Crimson & Emerald)
val LeicaRed = Color(0xFFEF4444)             // Subtle recording dot & active timer
val LeicaRedMuted = Color(0x26EF4444)        // Muted recording badge background
val StatusSuccess = Color(0xFF22C55E)        // Discreet encryption/verified check
val StatusWarning = Color(0xFFF59E0B)        // Discreet warning
val StatusError = Color(0xFFEF4444)          // Lockout & danger

// Monochromatic Text Hierarchy
val TextPrimary = Color(0xFFFFFFFF)          // Crisp white headers (18:1 contrast)
val TextSecondary = Color(0xFFA1A1AA)        // Silver/zinc body text & subtitles (6.5:1)
val TextTertiary = Color(0xFF71717A)         // Graphite technical metadata & subtle hints
val TextDisabled = Color(0xFF52525B)

// Precision Hairline Borders
val BorderSubtle = Color(0xFF2B2C35)         // 1px architectural borders
val BorderHighlight = Color(0x40FFFFFF)      // 1px top-rim metallic highlight
val KeypadBg = Color(0xFF1E2028)             // PIN keypad button surface

// Legacy aliases mapped cleanly to Titanium tokens
val ObsidianBase = TitaniumCanvas
val SurfaceGlass = TitaniumSurfaceGlass
val SurfaceElevated = TitaniumSurfaceGlass
val SurfaceOpaque = TitaniumSurface
val MintElectric = TitaniumWhite             // All primary CTAs become crisp Titanium White
val MintTextDark = TitaniumTextDark          // Dark text on white buttons
val MintMuted = Color(0x1AFFFFFF)
val AmberSafety = LeicaRed                   // Recording becomes Leica Crimson
val AmberMuted = LeicaRedMuted
val RoseDanger = StatusError
val AlertRed = StatusError
val Zinc100 = TextPrimary
val Zinc300 = TitaniumSilver
val Zinc400 = TextSecondary
val Zinc600 = TextTertiary
val Zinc800 = KeypadBg
val Charcoal900 = TitaniumCanvas
val Charcoal800 = TitaniumSurface
val Charcoal700 = TitaniumSurfaceGlass
val Charcoal600 = BorderSubtle
val AccentCyan = TitaniumWhite
val AccentCyanDark = TitaniumSilver
val AccentEmerald = TitaniumSilver
val AccentRose = LeicaRed
val TextPrimaryDark = TextPrimary
val TextSecondaryDark = TextSecondary
val TextTertiaryDark = TextTertiary
val LightBg = TitaniumCanvas
val LightSurface = TitaniumSurface
val LightSurfaceVariant = TitaniumSurfaceGlass
val TextPrimaryLight = TextPrimary
val TextSecondaryLight = TextSecondary
