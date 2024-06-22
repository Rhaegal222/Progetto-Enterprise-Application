package com.example.frontend.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.frontend.view_models.SignUpViewModel
import com.example.frontend.R
import com.example.frontend.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignUpScreen(navController: NavHostController) {
    val viewModel: SignUpViewModel = viewModel()
    val context = LocalContext.current

    var isObscured by remember { mutableStateOf(true) }
    val density = LocalDensity.current
    val isDarkMode = isSystemInDarkTheme()
    val inputBorderColor = Color.Gray
    val textColor = Color.Black
    val iconColor = Color.Black

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = iconColor)
                    }
                },
                title = {}
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 32.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Form Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
                Text(
                    text = "REGISTRAZIONE",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Registration Form
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = viewModel.username,
                    onValueChange = { viewModel.username = it },
                    label = { Text("username", color = textColor) },
                    leadingIcon = { Icon(Icons.Default.Face, contentDescription = null, tint = iconColor) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = inputBorderColor,
                        unfocusedBorderColor = inputBorderColor,
                        cursorColor = textColor
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label = { Text("email", color = textColor) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = iconColor) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = inputBorderColor,
                        unfocusedBorderColor = inputBorderColor,
                        cursorColor = textColor
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    label = { Text("password", color = textColor) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = iconColor) },
                    trailingIcon = {
                        IconButton(onClick = { isObscured = !isObscured }) {
                            Icon(
                                imageVector = if (isObscured) Icons.Default.Close else Icons.Default.Done,
                                contentDescription = null,
                                tint = iconColor
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (isObscured) PasswordVisualTransformation() else VisualTransformation.None,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = inputBorderColor,
                        unfocusedBorderColor = inputBorderColor,
                        cursorColor = textColor
                    ),
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.registerUser { success, errorMessage ->
                            if (success) {
                                Toast.makeText(context, "Registrazione avvenuta con successo", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.LoginScreen.route)
                            } else {
                                Toast.makeText(context, "Registrazione fallita: $errorMessage", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "REGISTRATI")
                }

            }

            Spacer(modifier = Modifier.height(32.dp))

            // OR Section
            Text("OPPURE", color = textColor)

            Spacer(modifier = Modifier.height(16.dp))

            // Google Sign In Button
            OutlinedButton(
                onClick = { /* Handle Google Sign-In */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.googlelogo),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = iconColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Accedi con Google", color = textColor)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Login Section
            TextButton(
                onClick = { navController.navigate(Screen.LoginScreen.route) }
            ) {
                Text(
                    buildAnnotatedString {
                        append(text = "Hai già un account?")
                        withStyle(style = SpanStyle(color = Color.Blue)) {
                            append(text = " Accedi")
                        }
                    },
                    color = textColor
                )
            }
        }
    }
}
