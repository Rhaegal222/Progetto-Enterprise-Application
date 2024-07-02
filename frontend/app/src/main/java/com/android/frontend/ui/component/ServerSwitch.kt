package com.android.frontend.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.frontend.persistence.CurrentDataUtils

@Composable
fun ServerSwitch() {
    val isDevelopment = remember { mutableStateOf(CurrentDataUtils.baseUrl == "http://10.0.2.2:8080/") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = isDevelopment.value,
            onCheckedChange = { isChecked ->
                isDevelopment.value = isChecked
                CurrentDataUtils.baseUrl = if (isChecked) {
                    "http://10.0.2.2:8080/"
                } else {
                    "http://192.168.169.200:8080/"
                }
                Log.d("DEBUG", "BaseUrl changed to: ${CurrentDataUtils.baseUrl}")
            }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = if (isDevelopment.value) "Localhost" else "Gaetano",
            modifier = Modifier.width(100.dp),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
    }
}
