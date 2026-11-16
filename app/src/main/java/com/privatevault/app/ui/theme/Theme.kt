package com.privatevault.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Titanium Monochrome Luxury color scheme (Apple Pro / Leica Darkroom aesthetic)
private val TitaniumDarkColorScheme = darkColorScheme(
    primary = TitaniumWhite,
    onPrimary = TitaniumTextDark,
    primaryContainer = TitaniumSurface,
    onPrimaryContainer = TitaniumWhite,
    secondary = TitaniumSilver,
    onSecondary = TitaniumTextDark,
    secondaryContainer = KeypadBg,
    onSecondaryContainer = TitaniumSilver,
    error = StatusError,
    onError = TitaniumWhite,
    background = TitaniumCanvas,
    onBackground = TextPrimary,
    surface = TitaniumSurface,
    onSurface = TextPrimary,
    surfaceVariant = TitaniumSurfaceGlass,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle
)

@Composable
fun PrivateVaultTheme(
    darkTheme: Boolean = true, // Force dark mode default for vault privacy
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TitaniumDarkColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
