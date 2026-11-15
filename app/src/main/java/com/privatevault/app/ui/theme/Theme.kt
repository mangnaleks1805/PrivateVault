package com.privatevault.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = Charcoal900,
    primaryContainer = Charcoal700,
    onPrimaryContainer = AccentCyan,
    secondary = AccentEmerald,
    onSecondary = Charcoal900,
    error = AccentRose,
    onError = Charcoal900,
    background = Charcoal900,
    onBackground = TextPrimaryDark,
    surface = Charcoal800,
    onSurface = TextPrimaryDark,
    surfaceVariant = Charcoal700,
    onSurfaceVariant = TextSecondaryDark,
    outline = Charcoal600
)

private val LightColorScheme = lightColorScheme(
    primary = AccentCyanDark,
    onPrimary = LightSurface,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = TextPrimaryLight,
    secondary = AccentEmerald,
    onSecondary = LightSurface,
    error = AccentRose,
    onError = LightSurface,
    background = LightBg,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = Charcoal600
)

@Composable
fun PrivateVaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Default to dark palette to emphasize vault privacy and security
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
