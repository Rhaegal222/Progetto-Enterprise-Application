package com.android.frontend.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFB0B0B0), // Grigio chiaro
    onPrimary = Color(0xFFFFFFFF), // Bianco
    primaryContainer = Color(0xFFC0C0C0), // Grigio medio
    onPrimaryContainer = Color(0xFF000000), // Nero
    inversePrimary = Color(0xFF606060), // Grigio scuro
    secondary = Color(0xFFA0A0A0), // Grigio medio
    onSecondary = Color(0xFF000000), // Nero
    secondaryContainer = Color(0xFFD0D0D0), // Grigio chiaro
    onSecondaryContainer = Color(0xFF000000), // Nero
    tertiary = Color(0xFFE0E0E0), // Grigio chiaro
    onTertiary = Color(0xFF000000), // Nero
    tertiaryContainer = Color(0xFFF0F0F0), // Grigio molto chiaro
    onTertiaryContainer = Color(0xFF000000), // Nero
    background = Color(0xFFFFFFFF), // Bianco
    onBackground = Color(0xFF000000), // Nero
    surface = Color(0xFFD0D0D0), // Grigio chiaro
    onSurface = Color(0xFF000000), // Nero
    surfaceVariant = Color(0xFFE0E0E0), // Grigio chiaro
    onSurfaceVariant = Color(0xFF000000), // Nero
    surfaceTint = Color(0xFFB0B0B0), // Grigio chiaro
    inverseSurface = Color(0xFF000000), // Nero
    inverseOnSurface = Color(0xFFFFFFFF), // Bianco
    error = Color(0xFF808080), // Grigio scuro (usato per errori)
    onError = Color(0xFFFFFFFF), // Bianco
    errorContainer = Color(0xFFB0B0B0), // Grigio chiaro
    onErrorContainer = Color(0xFF000000), // Nero
    outline = Color(0xFF000000), // Nero
    outlineVariant = Color(0xFFD0D0D0), // Grigio chiaro
    scrim = Color(0xFF000000), // Nero
    surfaceBright = Color(0xFFF0F0F0), // Grigio molto chiaro
    surfaceContainer = Color(0xFFE0E0E0), // Grigio chiaro
    surfaceContainerHigh = Color(0xFFD0D0D0), // Grigio chiaro
    surfaceContainerHighest = Color(0xFFC0C0C0), // Grigio medio
    surfaceContainerLow = Color(0xFFB0B0B0), // Grigio chiaro
    surfaceContainerLowest = Color(0xFFA0A0A0), // Grigio medio
    surfaceDim = Color(0xFF909090) // Grigio scuro
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF606060), // Grigio medio
    onPrimary = Color(0xFFFFFFFF), // Bianco
    primaryContainer = Color(0xFF404040), // Grigio medio-scuro
    onPrimaryContainer = Color(0xFFFFFFFF), // Bianco
    inversePrimary = Color(0xFFB0B0B0), // Grigio chiaro
    secondary = Color(0xFF505050), // Grigio medio
    onSecondary = Color(0xFFFFFFFF), // Bianco
    secondaryContainer = Color(0xFF404040), // Grigio medio-scuro
    onSecondaryContainer = Color(0xFFFFFFFF), // Bianco
    tertiary = Color(0xFF303030), // Grigio scuro
    onTertiary = Color(0xFFFFFFFF), // Bianco
    tertiaryContainer = Color(0xFF202020), // Grigio molto scuro
    onTertiaryContainer = Color(0xFFFFFFFF), // Bianco
    background = Color(0xFF000000), // Nero
    onBackground = Color(0xFFFFFFFF), // Bianco
    surface = Color(0xFF303030), // Grigio scuro
    onSurface = Color(0xFFFFFFFF), // Bianco
    surfaceVariant = Color(0xFF404040), // Grigio medio-scuro
    onSurfaceVariant = Color(0xFFFFFFFF), // Bianco
    surfaceTint = Color(0xFF606060), // Grigio medio
    inverseSurface = Color(0xFFFFFFFF), // Bianco
    inverseOnSurface = Color(0xFF000000), // Nero
    error = Color(0xFF808080), // Grigio (usato per errori)
    onError = Color(0xFFFFFFFF), // Bianco
    errorContainer = Color(0xFF606060), // Grigio medio
    onErrorContainer = Color(0xFFFFFFFF), // Bianco
    outline = Color(0xFFFFFFFF), // Bianco
    outlineVariant = Color(0xFF303030), // Grigio scuro
    scrim = Color(0xFF000000), // Nero
    surfaceBright = Color(0xFF505050), // Grigio medio
    surfaceContainer = Color(0xFF404040), // Grigio medio-scuro
    surfaceContainerHigh = Color(0xFF303030), // Grigio scuro
    surfaceContainerHighest = Color(0xFF202020), // Grigio molto scuro
    surfaceContainerLow = Color(0xFF101010), // Grigio molto scuro
    surfaceContainerLowest = Color(0xFF000000), // Nero
    surfaceDim = Color(0xFF101010) // Grigio molto scuro
)

@Composable
fun FrontendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}
