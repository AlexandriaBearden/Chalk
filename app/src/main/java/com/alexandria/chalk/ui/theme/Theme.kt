package com.alexandria.chalk.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val ChalkColorTheme = lightColorScheme(
    primary = Charcoal,
    onPrimary = White,

    secondary = PowderBlue,
    onSecondary = Charcoal,

    background = Background,
    onBackground = Charcoal,

    surface = Background,
    onSurface = Charcoal,

    surfaceVariant = White,
    onSurfaceVariant = Slate
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