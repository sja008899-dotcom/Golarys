package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = HeritageGold,
    onPrimary = DeepNavy,
    primaryContainer = BotanicalGreenDark,
    onPrimaryContainer = HeritageGoldLight,
    secondary = BotanicalGreen,
    onSecondary = Color.White,
    tertiary = HeritageGold,
    background = DeepNavy,
    onBackground = SurfaceLight,
    surface = DeepNavyLight,
    onSurface = SurfaceLight,
    surfaceVariant = DeepNavy,
    onSurfaceVariant = HeritageGoldLight
)

private val LightColorScheme = lightColorScheme(
    primary = BotanicalGreen,
    onPrimary = Color.White,
    primaryContainer = SurfaceVariantLight,
    onPrimaryContainer = BotanicalGreenDark,
    secondary = HeritageGold,
    onSecondary = DeepNavy,
    secondaryContainer = HeritageGoldLight,
    onSecondaryContainer = DeepNavy,
    tertiary = BotanicalGreenDark,
    background = SurfaceLight,
    onBackground = TextPrimary,
    surface = Color.White,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder
)

@Composable
fun GolarysTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
