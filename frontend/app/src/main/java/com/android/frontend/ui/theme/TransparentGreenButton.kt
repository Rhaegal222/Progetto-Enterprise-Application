package com.android.frontend.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TransparentGreenButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonName: String
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp) ,
        shape = MaterialTheme.shapes.small.copy(
            topStart = CornerSize(8.dp),
            topEnd = CornerSize(8.dp),
            bottomStart = CornerSize(8.dp),
            bottomEnd = CornerSize(8.dp)
        ),
        contentPadding = ButtonDefaults.ContentPadding,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ){
        Text(text = buttonName)
    }
}