package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = NavyDarkest,
    primaryContainer = NavySurface,
    onPrimaryContainer = ElectricCyan,
    secondary = GoldElectric,
    onSecondary = NavyDarkest,
    secondaryContainer = NavyDeep,
    onSecondaryContainer = GoldElectric,
    tertiary = CyanAccent,
    onTertiary = Color.White,
    background = NavyDarkest,
    onBackground = TextPrimary,
    surface = NavyDeep,
    onSurface = TextPrimary,
    surfaceVariant = NavyCard,
    onSurfaceVariant = TextSecondary,
    outline = NavyBorder,
    error = RedFault,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFFD97706),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFFB45309),
    tertiary = Color(0xFF0D9488),
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    error = RedFault,
    onError = Color.White
)

@Composable
fun ElectraSenseTheme(
    darkTheme: Boolean = true, // Default to high-contrast dark electrical dashboard
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
