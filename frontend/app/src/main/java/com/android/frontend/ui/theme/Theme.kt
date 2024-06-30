package com.android.frontend.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define your color palette for light and dark theme
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFB0B0B0), // Grigio chiaro
    onPrimary = Color(0xFFFFFFFF), // Bianco
    background = Color(0xFFFFFFFF), // Bianco
    onBackground = Color(0xFF000000), // Nero
    surface = Color(0xFFD0D0D0), // Grigio chiaro
    onSurface = Color(0xFF000000), // Nero
    primaryContainer = Color(0xFFC0C0C0), // Grigio medio
    onPrimaryContainer = Color(0xFF000000), // Nero
    secondary = Color(0xFFA0A0A0), // Grigio medio
    onSecondary = Color(0xFF000000), // Nero
    error = Color(0xFF808080), // Grigio scuro (usato per errori)
    onError = Color(0xFFFFFFFF) // Bianco
)


private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF606060), // Grigio medio
    onPrimary = Color(0xFF000000), // Nero
    background = Color(0xFF000000), // Nero
    onBackground = Color(0xFFFFFFFF), // Bianco
    surface = Color(0xFF303030), // Grigio scuro
    onSurface = Color(0xFFFFFFFF), // Bianco
    primaryContainer = Color(0xFF404040), // Grigio medio-scuro
    onPrimaryContainer = Color(0xFFFFFFFF), // Bianco
    secondary = Color(0xFF505050), // Grigio medio
    onSecondary = Color(0xFF000000), // Nero
    error = Color(0xFF808080), // Grigio (usato per errori)
    onError = Color(0xFFFFFFFF) // Bianco
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
