package com.example.frontend.view

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.frontend.R
import com.example.frontend.model.CurrentDataUtils
import com.example.frontend.navigation.Screen
import com.example.frontend.view_models.LoginViewModel
import com.stevdzasan.onetap.OneTapSignInState
import com.stevdzasan.onetap.OneTapSignInWithGoogle
import com.stevdzasan.onetap.rememberOneTapSignInState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavHostController) {
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

    val loginViewModel: LoginViewModel = viewModel()

    val state = rememberOneTapSignInState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.login).uppercase(),
                        color = if (isDarkMode) Color.White else Color.Black)
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
                    label = {
                            Text(
                                text = stringResource(id = R.string.email),
                                color = textColor
                            )
                            },
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
                    label = {
                        Text(
                            text = stringResource(id = R.string.password),
                            color = textColor
                        )
                            },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = iconColor) },
                    trailingIcon = {
                        IconButton(onClick = { isObscured = !isObscured }) {
                            Icon(
                                imageVector = if (isObscured) Icons.Default.Visibility else Icons.Filled.VisibilityOff,
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
                    Text(
                        text = stringResource(id = R.string.forgot_password),
                        color = textColor
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.loginUser { success, errorMessage ->
                            if (success) {
                                Toast.makeText(context, "Login avvenuto con successo", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.MainScreen.route)
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
                    Text(
                        text = stringResource(id = R.string.login).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("OPPURE")
                Spacer(modifier = Modifier.height(16.dp))

                GoogleSignInButton(
                    state = state,
                    onTokenIdReceived = { tokenId ->
                        Log.d("LoginPage", "tokenId: $tokenId")
                        loginViewModel.authenticateGoogle(tokenId, onError = {
                            Toast.makeText(context, "Autenticazione Google fallita", Toast.LENGTH_SHORT).show()
                        }, onSuccess = {
                            CurrentDataUtils.goToHome.value = true
                        })
                    },
                    onDialogDismissed = { message ->
                        Log.d("LoginPage", "dismissed, message: $message")
                    }
                )

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

@Composable
fun GoogleSignInButton(
    state: OneTapSignInState,
    onTokenIdReceived: (String) -> Unit,
    onDialogDismissed: (String) -> Unit
) {
    val context = LocalContext.current
    val iconColor = Color.Black

    OutlinedButton(
        onClick = {
            Log.d("GoogleSignInButton", "Button clicked, opening state")
            state.open()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(id = R.drawable.googlelogo),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = iconColor
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Accedi con Google", color = iconColor)
    }

    OneTapSignInWithGoogle(
        state = state,
        clientId = stringResource(id = R.string.web_client_id),
        onTokenIdReceived = { tokenId ->
            Log.d("GoogleSignInButton", "Token ID Received: $tokenId")
            onTokenIdReceived(tokenId)
        },
        onDialogDismissed = { message ->
            Log.d("GoogleSignInButton", "Dialog Dismissed: $message")
            onDialogDismissed(message)
        }
    )
}

