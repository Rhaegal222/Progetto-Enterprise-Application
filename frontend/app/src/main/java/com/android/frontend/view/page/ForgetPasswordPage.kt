package com.android.frontend.view.page

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.RetrofitInstance
import com.android.frontend.navigation.Screen
import com.android.frontend.service.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordPage(navController: NavHostController) {

    val success = rememberSaveable { mutableStateOf(false) }
    val email = rememberSaveable { mutableStateOf("") }
    val showToast = remember { mutableStateOf(false) }

    val userService: UserService = RetrofitInstance.api

    val context = LocalContext.current

    val inputBorderColor = Color.Gray
    val textColor = Color.Black
    val iconColor = Color.Black

    val isDarkMode = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.recover_password).uppercase(),
                        color = if (isDarkMode) Color.White else Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = if (isDarkMode) Color.White else Color.Black)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.enter_email),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.recover_password_text),
                fontSize = 16.sp,
                textAlign = TextAlign.Justify,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email", color = textColor) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = iconColor) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = textColor,
                    focusedBorderColor = inputBorderColor,
                    unfocusedBorderColor = inputBorderColor,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    sendEmail(userService, email.value, success, showToast)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(id = R.string.send_email).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

        /*
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
    if (showToast.value) {
        showToast.value = false
        Toast.makeText(context, "Qualcosa è andato storto, riprova", Toast.LENGTH_SHORT).show()
    }

         */

fun sendEmail(userService: UserService, email: String, success: MutableState<Boolean>, showToast: MutableState<Boolean>) {
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
