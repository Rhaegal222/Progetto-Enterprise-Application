package com.android.frontend.ui.theme.colors

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object OutlinedTextFieldColorScheme {
    // Light Theme Colors
    private val focusedTextColorLight = Color(0xFF000000)
    private val unfocusedTextColorLight = Color(0xFF000000)
    private val disabledTextColorLight = Color(0xFF9E9E9E)
    private val errorTextColorLight = Color(0xFFB00020)
    private val focusedContainerColorLight = Color.Transparent
    private val unfocusedContainerColorLight = Color.Transparent
    private val disabledContainerColorLight = Color(0xFFF5F5F5)
    private val errorContainerColorLight = Color.Transparent
    private val cursorColorLight = Color(0xFF1976D2)
    private val errorCursorColorLight = Color(0xFFB00020)
    private val textSelectionColorsLight = TextSelectionColors(
        handleColor = Color(0xFF1976D2),
        backgroundColor = Color(0x401976D2)
    )
    private val focusedIndicatorColorLight = Color(0xFF000000)
    private val unfocusedIndicatorColorLight = Color(0xFF000000)
    private val disabledIndicatorColorLight = Color(0xFFBDBDBD)
    private val errorIndicatorColorLight = Color(0xFFB00020)
    private val focusedLeadingIconColorLight = Color(0xFF000000)
    private val unfocusedLeadingIconColorLight = Color(0xFF000000)
    private val disabledLeadingIconColorLight = Color(0xFFBDBDBD)
    private val errorLeadingIconColorLight = Color(0xFFB00020)
    private val focusedTrailingIconColorLight = Color(0xFF000000)
    private val unfocusedTrailingIconColorLight = Color(0xFF000000)
    private val disabledTrailingIconColorLight = Color(0xFFBDBDBD)
    private val errorTrailingIconColorLight = Color(0xFFB00020)
    private val focusedLabelColorLight = Color(0xFF000000)
    private val unfocusedLabelColorLight = Color(0xFF000000)
    private val disabledLabelColorLight = Color(0xFFBDBDBD)
    private val errorLabelColorLight = Color(0xFFB00020)
    private val focusedPlaceholderColorLight = Color(0xFF757575)
    private val unfocusedPlaceholderColorLight = Color(0xFF757575)
    private val disabledPlaceholderColorLight = Color(0xFFBDBDBD)
    private val errorPlaceholderColorLight = Color(0xFFB00020)
    private val focusedSupportingTextColorLight = Color(0xFF757575)
    private val unfocusedSupportingTextColorLight = Color(0xFF757575)
    private val disabledSupportingTextColorLight = Color(0xFFBDBDBD)
    private val errorSupportingTextColorLight = Color(0xFFB00020)
    private val focusedPrefixColorLight = Color(0xFF757575)
    private val unfocusedPrefixColorLight = Color(0xFF757575)
    private val disabledPrefixColorLight = Color(0xFFBDBDBD)
    private val errorPrefixColorLight = Color(0xFFB00020)
    private val focusedSuffixColorLight = Color(0xFF757575)
    private val unfocusedSuffixColorLight = Color(0xFF757575)
    private val disabledSuffixColorLight = Color(0xFFBDBDBD)
    private val errorSuffixColorLight = Color(0xFFB00020)

    // Dark Theme Colors
    private val focusedTextColorDark = Color(0xFFFFFFFF)
    private val unfocusedTextColorDark = Color(0xFFFFFFFF)
    private val disabledTextColorDark = Color(0xFF757575)
    private val errorTextColorDark = Color(0xFFCF6679)
    private val focusedContainerColorDark = Color.Transparent
    private val unfocusedContainerColorDark = Color.Transparent
    private val disabledContainerColorDark = Color(0xFF1F1F1F)
    private val errorContainerColorDark = Color.Transparent
    private val cursorColorDark = Color(0xFF64B5F6)
    private val errorCursorColorDark = Color(0xFFCF6679)
    private val textSelectionColorsDark = TextSelectionColors(
        handleColor = Color(0xFF64B5F6),
        backgroundColor = Color(0x4064B5F6)
    )
    private val focusedIndicatorColorDark = Color(0xFF64B5F6)
    private val unfocusedIndicatorColorDark = Color(0xFFB3B3B3)
    private val disabledIndicatorColorDark = Color(0xFF424242)
    private val errorIndicatorColorDark = Color(0xFFCF6679)
    private val focusedLeadingIconColorDark = Color(0xFF64B5F6)
    private val unfocusedLeadingIconColorDark = Color(0xFFB3B3B3)
    private val disabledLeadingIconColorDark = Color(0xFF424242)
    private val errorLeadingIconColorDark = Color(0xFFCF6679)
    private val focusedTrailingIconColorDark = Color(0xFF64B5F6)
    private val unfocusedTrailingIconColorDark = Color(0xFFB3B3B3)
    private val disabledTrailingIconColorDark = Color(0xFF424242)
    private val errorTrailingIconColorDark = Color(0xFFCF6679)
    private val focusedLabelColorDark = Color(0xFF64B5F6)
    private val unfocusedLabelColorDark = Color(0xFFB3B3B3)
    private val disabledLabelColorDark = Color(0xFF424242)
    private val errorLabelColorDark = Color(0xFFCF6679)
    private val focusedPlaceholderColorDark = Color(0xFFB3B3B3)
    private val unfocusedPlaceholderColorDark = Color(0xFFB3B3B3)
    private val disabledPlaceholderColorDark = Color(0xFF424242)
    private val errorPlaceholderColorDark = Color(0xFFCF6679)
    private val focusedSupportingTextColorDark = Color(0xFFB3B3B3)
    private val unfocusedSupportingTextColorDark = Color(0xFFB3B3B3)
    private val disabledSupportingTextColorDark = Color(0xFF424242)
    private val errorSupportingTextColorDark = Color(0xFFCF6679)
    private val focusedPrefixColorDark = Color(0xFFB3B3B3)
    private val unfocusedPrefixColorDark = Color(0xFFB3B3B3)
    private val disabledPrefixColorDark = Color(0xFF424242)
    private val errorPrefixColorDark = Color(0xFFCF6679)
    private val focusedSuffixColorDark = Color(0xFFB3B3B3)
    private val unfocusedSuffixColorDark = Color(0xFFB3B3B3)
    private val disabledSuffixColorDark = Color(0xFF424242)
    private val errorSuffixColorDark = Color(0xFFCF6679)

    @Composable
    fun colors(
        focusedTextColor: Color = if (isSystemInDarkTheme()) focusedTextColorDark else focusedTextColorLight,
        unfocusedTextColor: Color = if (isSystemInDarkTheme()) unfocusedTextColorDark else unfocusedTextColorLight,
        disabledTextColor: Color = if (isSystemInDarkTheme()) disabledTextColorDark else disabledTextColorLight,
        errorTextColor: Color = if (isSystemInDarkTheme()) errorTextColorDark else errorTextColorLight,
        focusedContainerColor: Color = if (isSystemInDarkTheme()) focusedContainerColorDark else focusedContainerColorLight,
        unfocusedContainerColor: Color = if (isSystemInDarkTheme()) unfocusedContainerColorDark else unfocusedContainerColorLight,
        disabledContainerColor: Color = if (isSystemInDarkTheme()) disabledContainerColorDark else disabledContainerColorLight,
        errorContainerColor: Color = if (isSystemInDarkTheme()) errorContainerColorDark else errorContainerColorLight,
        cursorColor: Color = if (isSystemInDarkTheme()) cursorColorDark else cursorColorLight,
        errorCursorColor: Color = if (isSystemInDarkTheme()) errorCursorColorDark else errorCursorColorLight,
        selectionColors: TextSelectionColors = if (isSystemInDarkTheme()) textSelectionColorsDark else textSelectionColorsLight,
        focusedIndicatorColor: Color = if (isSystemInDarkTheme()) focusedIndicatorColorDark else focusedIndicatorColorLight,
        unfocusedIndicatorColor: Color = if (isSystemInDarkTheme()) unfocusedIndicatorColorDark else unfocusedIndicatorColorLight,
        disabledIndicatorColor: Color = if (isSystemInDarkTheme()) disabledIndicatorColorDark else disabledIndicatorColorLight,
        errorIndicatorColor: Color = if (isSystemInDarkTheme()) errorIndicatorColorDark else errorIndicatorColorLight,
        focusedLeadingIconColor: Color = if (isSystemInDarkTheme()) focusedLeadingIconColorDark else focusedLeadingIconColorLight,
        unfocusedLeadingIconColor: Color = if (isSystemInDarkTheme()) unfocusedLeadingIconColorDark else unfocusedLeadingIconColorLight,
        disabledLeadingIconColor: Color = if (isSystemInDarkTheme()) disabledLeadingIconColorDark else disabledLeadingIconColorLight,
        errorLeadingIconColor: Color = if (isSystemInDarkTheme()) errorLeadingIconColorDark else errorLeadingIconColorLight,
        focusedTrailingIconColor: Color = if (isSystemInDarkTheme()) focusedTrailingIconColorDark else focusedTrailingIconColorLight,
        unfocusedTrailingIconColor: Color = if (isSystemInDarkTheme()) unfocusedTrailingIconColorDark else unfocusedTrailingIconColorLight,
        disabledTrailingIconColor: Color = if (isSystemInDarkTheme()) disabledTrailingIconColorDark else disabledTrailingIconColorLight,
        errorTrailingIconColor: Color = if (isSystemInDarkTheme()) errorTrailingIconColorDark else errorTrailingIconColorLight,
        focusedLabelColor: Color = if (isSystemInDarkTheme()) focusedLabelColorDark else focusedLabelColorLight,
        unfocusedLabelColor: Color = if (isSystemInDarkTheme()) unfocusedLabelColorDark else unfocusedLabelColorLight,
        disabledLabelColor: Color = if (isSystemInDarkTheme()) disabledLabelColorDark else disabledLabelColorLight,
        errorLabelColor: Color = if (isSystemInDarkTheme()) errorLabelColorDark else errorLabelColorLight,
        focusedPlaceholderColor: Color = if (isSystemInDarkTheme()) focusedPlaceholderColorDark else focusedPlaceholderColorLight,
        unfocusedPlaceholderColor: Color = if (isSystemInDarkTheme()) unfocusedPlaceholderColorDark else unfocusedPlaceholderColorLight,
        disabledPlaceholderColor: Color = if (isSystemInDarkTheme()) disabledPlaceholderColorDark else disabledPlaceholderColorLight,
        errorPlaceholderColor: Color = if (isSystemInDarkTheme()) errorPlaceholderColorDark else errorPlaceholderColorLight,
        focusedSupportingTextColor: Color = if (isSystemInDarkTheme()) focusedSupportingTextColorDark else focusedSupportingTextColorLight,
        unfocusedSupportingTextColor: Color = if (isSystemInDarkTheme()) unfocusedSupportingTextColorDark else unfocusedSupportingTextColorLight,
        disabledSupportingTextColor: Color = if (isSystemInDarkTheme()) disabledSupportingTextColorDark else disabledSupportingTextColorLight,
        errorSupportingTextColor: Color = if (isSystemInDarkTheme()) errorSupportingTextColorDark else errorSupportingTextColorLight,
        focusedPrefixColor: Color = if (isSystemInDarkTheme()) focusedPrefixColorDark else focusedPrefixColorLight,
        unfocusedPrefixColor: Color = if (isSystemInDarkTheme()) unfocusedPrefixColorDark else unfocusedPrefixColorLight,
        disabledPrefixColor: Color = if (isSystemInDarkTheme()) disabledPrefixColorDark else disabledPrefixColorLight,
        errorPrefixColor: Color = if (isSystemInDarkTheme()) errorPrefixColorDark else errorPrefixColorLight,
        focusedSuffixColor: Color = if (isSystemInDarkTheme()) focusedSuffixColorDark else focusedSuffixColorLight,
        unfocusedSuffixColor: Color = if (isSystemInDarkTheme()) unfocusedSuffixColorDark else unfocusedSuffixColorLight,
        disabledSuffixColor: Color = if (isSystemInDarkTheme()) disabledSuffixColorDark else disabledSuffixColorLight,
        errorSuffixColor: Color = if (isSystemInDarkTheme()) errorSuffixColorDark else errorSuffixColorLight,
    ): TextFieldColors {
        return TextFieldColors(
            focusedTextColor = focusedTextColor,
            unfocusedTextColor = unfocusedTextColor,
            disabledTextColor = disabledTextColor,
            errorTextColor = errorTextColor,
            focusedContainerColor = focusedContainerColor,
            unfocusedContainerColor = unfocusedContainerColor,
            disabledContainerColor = disabledContainerColor,
            errorContainerColor = errorContainerColor,
            cursorColor = cursorColor,
            errorCursorColor = errorCursorColor,
            textSelectionColors = selectionColors,
            focusedIndicatorColor = focusedIndicatorColor,
            unfocusedIndicatorColor = unfocusedIndicatorColor,
            disabledIndicatorColor = disabledIndicatorColor,
            errorIndicatorColor = errorIndicatorColor,
            focusedLeadingIconColor = focusedLeadingIconColor,
            unfocusedLeadingIconColor = unfocusedLeadingIconColor,
            disabledLeadingIconColor = disabledLeadingIconColor,
            errorLeadingIconColor = errorLeadingIconColor,
            focusedTrailingIconColor = focusedTrailingIconColor,
            unfocusedTrailingIconColor = unfocusedTrailingIconColor,
            disabledTrailingIconColor = disabledTrailingIconColor,
            errorTrailingIconColor = errorTrailingIconColor,
            focusedLabelColor = focusedLabelColor,
            unfocusedLabelColor = unfocusedLabelColor,
            disabledLabelColor = disabledLabelColor,
            errorLabelColor = errorLabelColor,
            focusedPlaceholderColor = focusedPlaceholderColor,
            unfocusedPlaceholderColor = unfocusedPlaceholderColor,
            disabledPlaceholderColor = disabledPlaceholderColor,
            errorPlaceholderColor = errorPlaceholderColor,
            focusedSupportingTextColor = focusedSupportingTextColor,
            unfocusedSupportingTextColor = unfocusedSupportingTextColor,
            disabledSupportingTextColor = disabledSupportingTextColor,
            errorSupportingTextColor = errorSupportingTextColor,
            focusedPrefixColor = focusedPrefixColor,
            unfocusedPrefixColor = unfocusedPrefixColor,
            disabledPrefixColor = disabledPrefixColor,
            errorPrefixColor = errorPrefixColor,
            focusedSuffixColor = focusedSuffixColor,
            unfocusedSuffixColor = unfocusedSuffixColor,
            disabledSuffixColor = disabledSuffixColor,
            errorSuffixColor = errorSuffixColor
        )
    }
}