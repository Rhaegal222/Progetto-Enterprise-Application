package com.android.frontend.ui.theme.colors

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object TextButtonColorScheme {

    // QUANDO IL BOTTONE NON é dentro nessun altro composable
    private val containerColorLight = Color.Transparent
    private val contentColorLight = Color(0xFF2A2B2C) // Un grigio più scuro per migliorare il contrasto
    private val disabledContainerColorLight = Color.Transparent
    private val disabledContentColorLight = Color(0xFF888888) // Un grigio più chiaro per disabilitato

    private val containerColorDark = Color.Transparent
    private val contentColorDark = Color(0xFFD5D4D3)
    private val disabledContainerColorDark = Color.Transparent
    private val disabledContentColorDark = Color(0xFF888888)

    // QUANDO IL BOTTONE È DENTRO UNA CARD
    private val containerColorLightCard = Color.Transparent
    private val contentColorLightCard = Color(0xFF000000) // Un grigio più scuro per migliorare il contrasto
    private val disabledContainerColorLightCard = Color(0xFFFFFFFF)
    private val disabledContentColorLightCard = Color(0xFF888888) // Un grigio più chiaro per disabilitato

    private val containerColorDarkCard = Color(0xFF000000)
    private val contentColorDarkCard = Color(0xFFD5D4D3)
    private val disabledContainerColorDarkCard = Color(0xFF000000)
    private val disabledContentColorDarkCard = Color(0xFF888888)

    @Composable
    fun textButtonColors(
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

    @Composable
    fun textButtonColorsCard(
        containerColor: Color = if (isSystemInDarkTheme()) containerColorDarkCard else containerColorLightCard,
        contentColor: Color = if (isSystemInDarkTheme()) contentColorDarkCard else contentColorLightCard,
        disabledContainerColor: Color = if (isSystemInDarkTheme()) disabledContainerColorDarkCard else disabledContainerColorLightCard,
        disabledContentColor: Color = if (isSystemInDarkTheme()) disabledContentColorDarkCard else disabledContentColorLightCard
    ) = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor
    )
}