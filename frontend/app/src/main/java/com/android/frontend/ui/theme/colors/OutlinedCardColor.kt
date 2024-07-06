package com.android.frontend.ui.theme.colors

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object OutlinedCardColorScheme {
    private val outlinedCardContainerLight = Color.Transparent
    private val outlinedCardContentLight = Color.Transparent
    private val outlinedCardContainerDark = Color.Transparent
    private val outlinedCardContentDark = Color.Transparent

    @Composable
    fun outlinedCardColors(
        containerColor: Color = if (isSystemInDarkTheme()) outlinedCardContainerDark else outlinedCardContainerLight,
        contentColor: Color = if (isSystemInDarkTheme()) outlinedCardContentDark else outlinedCardContentLight
    ) = CardDefaults.outlinedCardColors(
        containerColor = containerColor,
        contentColor = contentColor
    )

    @Composable
    fun outlinedCardBorder(): Color = if (isSystemInDarkTheme()) Color(0xFFFFFFFF) else Color(
        0xFF000000
    )

}