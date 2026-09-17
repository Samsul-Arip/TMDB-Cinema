package com.samsul.moviedb.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private val CinemaDarkColorScheme = darkColorScheme(
    primary = CinemaGold,
    onPrimary = Color(0xFF151200),
    primaryContainer = CinemaGoldDark,
    onPrimaryContainer = Color(0xFFFFF4D4),
    secondary = CinemaGoldLight,
    onSecondary = Color(0xFF151200),
    background = CinemaBackground,
    onBackground = CinemaTextPrimary,
    surface = CinemaSurface,
    onSurface = CinemaTextPrimary,
    surfaceVariant = CinemaSurfaceVariant,
    onSurfaceVariant = CinemaTextSecondary,
    outline = CinemaCardBorder,
    error = CinemaError,
    onError = Color.White
)

@Composable
fun TechnicalTestAndroidTheme(
    content: @Composable () -> Unit
) {
    val currentDensity = LocalDensity.current
    val customDensity = remember(currentDensity.density) {
        Density(
            density = currentDensity.density,
            fontScale = 1.0f
        )
    }

    CompositionLocalProvider(
        LocalDensity provides customDensity
    ) {
        MaterialTheme(
            colorScheme = CinemaDarkColorScheme,
            typography = Typography,
            content = content
        )
    }
}