package com.android.frontend.view.page.authentication

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.RetrofitInstance
import com.android.frontend.navigation.Navigation
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
    val context = LocalContext.current

    val email = remember { mutableStateOf("") }
    val showDialog = remember { mutableStateOf(false) }

    val userService: UserService = RetrofitInstance.getUserApi(context)

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("OK")
                }
            },
            title = { Text("Email Inviata") },
            text = { Text("Controlla la tua email per recuperare la password.") }
        )
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.recover_password).uppercase()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Navigation.LoginPage.route) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
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
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.recover_password_text),
                fontSize = 16.sp,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    sendEmail(userService, email.value) {
                        showDialog.value = true
                    }
                },
            ) {
                Text(
                    text = stringResource(id = R.string.send_email).uppercase(),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun sendEmail(userService: UserService, email: String, onSuccess: () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        userService.resetPassword(email).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Gestisci l'errore come preferisci
            }
        })
    }
}
