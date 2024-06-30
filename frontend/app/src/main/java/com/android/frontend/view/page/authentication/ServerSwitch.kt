package com.android.frontend.view.page.authentication

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.frontend.persistence.CurrentDataUtils

@Composable
fun ServerSwitch() {
    val colors = MaterialTheme.colorScheme
    val isDevelopment = remember { mutableStateOf(CurrentDataUtils.baseUrl == "http://10.0.2.2:8080/") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            modifier = Modifier.size(40.dp),
            checked = isDevelopment.value,
            onCheckedChange = { isChecked ->
                isDevelopment.value = isChecked
                CurrentDataUtils.baseUrl = if (isChecked) {
                    "http://10.0.2.2:8080/"
                } else {
                    "https://192.168.160.200:8080/"
                }
                Log.d("DEBUG", "BaseUrl changed to: ${CurrentDataUtils.baseUrl}")
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.primary,
                checkedTrackColor = colors.primary.copy(alpha = 0.5f),
                uncheckedThumbColor = colors.onBackground,
                uncheckedTrackColor = colors.onBackground.copy(alpha = 0.5f)
            ),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = if (isDevelopment.value) "Localhost" else "Gaetano",
            color = colors.onBackground,
            fontSize = 20.sp
        )
    }
}