package com.alexandria.chalk.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ChalkColorTheme = lightColorScheme(
    primary = PowderBlue,
    onPrimary = Charcoal,

    primaryContainer = PowderBlueLight,
    onPrimaryContainer = Charcoal,

    secondary = Charcoal,
    onSecondary = White,

    background = Background,
    onBackground = Charcoal,

    surface = White,
    onSurface = Charcoal,

    surfaceVariant = PowderBlueLight,
    onSurfaceVariant = Slate,

    outline = PowderBlue,
    outlineVariant = PowderBlueLight
)

@Composable
fun ChalkTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ChalkColorTheme,
        typography = ChalkTypography,
        content = content
    )
}