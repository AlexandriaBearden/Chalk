package com.alexandria.chalk.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ChalkColorScheme = lightColorScheme(
    primary = PowderBlueDark,
    onPrimary = White,

    primaryContainer = PowderBlueLight,
    onPrimaryContainer = Charcoal,

    secondary = PowderBlue,
    onSecondary = Charcoal,

    secondaryContainer = SurfaceSoft,
    onSecondaryContainer = Charcoal,

    background = Background,
    onBackground = Charcoal,

    surface = Surface,
    onSurface = Charcoal,

    surfaceVariant = SurfaceSoft,
    onSurfaceVariant = Slate,

    error = Error,
    onError = White,

    outline = Border,
    outlineVariant = Divider,

    scrim = Scrim
)

@Composable
fun ChalkTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ChalkColorScheme,
        typography = ChalkTypography,
        shapes = ChalkShapes,
        content = content
    )
}