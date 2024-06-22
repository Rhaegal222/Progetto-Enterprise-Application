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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.frontend.R
import com.example.frontend.navigation.Screen
import com.example.frontend.view_models.LoginViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val viewModel: LoginViewModel = viewModel()
    val context = LocalContext.current
    val size = with(LocalDensity.current) {
        DpSize(
            width = LocalConfiguration.current.screenWidthDp.dp,
            height = LocalConfiguration.current.screenHeightDp.dp
        )
    }
    val isDarkMode = isSystemInDarkTheme()

    var isObscured by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "LOGIN", color = if (isDarkMode) Color.White else Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = if (isDarkMode) Color.White else Color.Black)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier
                        .height(size.height * 0.25f)
                        .fillMaxWidth()
                )
                Text(
                    text = "ACCEDI",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "Accedi per acquistare i prodotti",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            val inputBorderColor = Color.Gray
            val textColor = Color.Black
            val iconColor = Color.Black

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = viewModel.username,
                    onValueChange = { viewModel.username = it },
                    label = { Text("E-mail", color = textColor) },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = iconColor)
                    },
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
                    label = { Text("Password", color = textColor) },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = iconColor) },
                    trailingIcon = {
                        IconButton(onClick = { isObscured = !isObscured }) {
                            Icon(
                                imageVector = if (isObscured) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
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

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { navController.navigate(Screen.ForgetPassword.route) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "Password dimenticata?", color = textColor)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.loginUser { success, errorMessage ->
                            if (success) {
                                Toast.makeText(context, "Login avvenuto con successo", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.HomeScreen.route)
                            } else {
                                Toast.makeText(context, "Login fallito: $errorMessage", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "login".uppercase())
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("OPPURE")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = {  },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.googlelogo),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = iconColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Accedi con Google", color = textColor)
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = { navController.navigate(Screen.SignUpScreen.route) }
                ) {
                    Text(
                        buildAnnotatedString {
                            append(text = "Non hai un account ?")
                            withStyle(style = SpanStyle(color = Color.Blue)) {
                                append(" Registrati ora")
                            }
                        },
                        color = textColor
                    )
                }
            }
        }
    }
}