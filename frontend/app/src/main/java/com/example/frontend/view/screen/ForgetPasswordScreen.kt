package com.example.frontend.view.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.frontend.RetrofitInstance
import com.example.frontend.service.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(navController: NavHostController) {

    val success = rememberSaveable { mutableStateOf(false) }
    val email = rememberSaveable { mutableStateOf("") }
    val showToast = remember { mutableStateOf(false) }

    val userService: UserService = RetrofitInstance.api

    val context = LocalContext.current

    val textColor = MaterialTheme.colorScheme.onSurface
    val iconColor = MaterialTheme.colorScheme.primary
    val inputBorderColor = MaterialTheme.colorScheme.primary

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)) {
        if (success.value) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Color.Transparent), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("EMAIL INVIATA", fontSize = 22.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp)
                    .background(Color.Transparent), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Inserisci l'email del tuo account", fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)

                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email", color = textColor) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Black) },
                    singleLine = true,
                    modifier = Modifier
                        .padding(all = 20.dp)
                        .fillMaxWidth(),
                    colors = outlinedTextFieldColors(
                        focusedBorderColor = inputBorderColor,
                        unfocusedBorderColor = inputBorderColor,
                        cursorColor = textColor
                    ),
                )

                Button(
                    onClick = {
                        resetEmail(userService, email.value, success, showToast)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Invia")
                }
            }
        }
    }

    if (showToast.value) {
        showToast.value = false
        Toast.makeText(context, "Qualcosa è andato storto, riprova", Toast.LENGTH_SHORT).show()
    }
}

fun resetEmail(userService: UserService, email: String, success: MutableState<Boolean>, showToast: MutableState<Boolean>) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            userService.resetPassword(email).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        success.value = true
                    } else {
                        showToast.value = true
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    showToast.value = true
                }
            })
        } catch (e: Exception) {
            showToast.value = true
        }
    }
}
