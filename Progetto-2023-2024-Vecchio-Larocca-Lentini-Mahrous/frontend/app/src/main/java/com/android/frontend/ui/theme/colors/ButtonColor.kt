package com.android.frontend.ui.theme.colors

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object ButtonColorScheme {
    private val containerColorLight = Color(0xFF000000)
    private val contentColorLight = Color(0xFFFFFFFF)
    private val disabledContainerColorLight = Color(0xFFE0E0E0)
    private val disabledContentColorLight = Color(0xFF9E9E9E)
    private val containerColorDark = Color(0xFFFFFFFF)
    private val contentColorDark = Color(0xFF000000)
    private val disabledContainerColorDark = Color(0xFF424242)
    private val disabledContentColorDark = Color(0xFF757575)
    @Composable
    fun buttonColors(
        containerColor: Color = if (isSystemInDarkTheme()) containerColorDark else containerColorLight,
        contentColor: Color = if (isSystemInDarkTheme()) contentColorDark else contentColorLight,
        disabledContainerColor: Color = if (isSystemInDarkTheme()) disabledContainerColorDark else disabledContainerColorLight,
        disabledContentColor: Color = if (isSystemInDarkTheme()) disabledContentColorDark else disabledContentColorLight
    ) = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor
    )
}
