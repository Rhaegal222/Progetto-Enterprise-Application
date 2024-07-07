package com.android.frontend.ui.theme.colors

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object CardColorScheme {

    val containerColorLight = Color(0xFFFFFFFF) // Grigio chiaro per il contenitore
    val contentColorLight = Color(0xFF000000)   // Nero per il contenuto
    val disabledContainerColorLight = Color(0xFFB0B0B0) // Grigio medio per il contenitore disabilitato
    val disabledContentColorLight = Color(0xFF808080)   // Grigio scuro per il contenuto disabilitato
    val containerColorDark = Color(0xFF000000)
    val contentColorDark = Color(0xFFFFFFFF)
    val disabledContainerColorDark = Color(0xFF424242)
    val disabledContentColorDark = Color(0xFF757575)
    @Composable
    fun cardColors(
        containerColor: Color = if (isSystemInDarkTheme()) containerColorDark else containerColorLight,
        contentColor: Color = if (isSystemInDarkTheme()) contentColorDark else contentColorLight,
        disabledContainerColor: Color = if (isSystemInDarkTheme()) disabledContainerColorDark else disabledContainerColorLight,
        disabledContentColor: Color = if (isSystemInDarkTheme()) disabledContentColorDark else disabledContentColorLight
    ) = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor
    )
}