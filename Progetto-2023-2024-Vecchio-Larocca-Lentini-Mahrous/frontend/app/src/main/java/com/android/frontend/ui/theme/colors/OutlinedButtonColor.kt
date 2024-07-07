package com.android.frontend.ui.theme.colors

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object OutlinedButtonColorScheme {
    private val outlinedButtonContainerLight = Color(0xFFFFFFFF) // Colore del contorno per OutlinedButton in tema chiaro
    private val outlinedButtonContentLight = Color(0xFF000000) // Colore del contenuto per OutlinedButton in tema chiaro
    private val outlinedButtonContainerDark = Color(0xFF000000) // Colore del contorno per OutlinedButton in tema scuro
    private val outlinedButtonContentDark = Color(0xFFFFFFFF) // Colore del contenuto per OutlinedButton in tema scuro

    @Composable
    fun outlinedButtonColors(
        containerColor: Color = if (isSystemInDarkTheme()) outlinedButtonContainerDark else outlinedButtonContainerLight,
        contentColor: Color = if (isSystemInDarkTheme()) outlinedButtonContentDark else outlinedButtonContentLight
    ) = ButtonDefaults.outlinedButtonColors(
        containerColor = containerColor,
        contentColor = contentColor
    )
}