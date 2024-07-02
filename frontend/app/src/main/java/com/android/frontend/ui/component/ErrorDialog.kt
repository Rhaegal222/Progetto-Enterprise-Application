package com.android.frontend.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.android.frontend.R
import com.android.frontend.ui.theme.colors.TextButtonColorScheme

@Composable
fun ErrorDialog(title: String, errorMessage: String, onDismiss: () -> Unit, onRetry: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(errorMessage) },
        confirmButton = {
            TextButton(
                onClick = onRetry,
                colors = TextButtonColorScheme.textButtonColors()
            ) {
                Text(stringResource(id = R.string.retry))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = TextButtonColorScheme.textButtonColors()
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}