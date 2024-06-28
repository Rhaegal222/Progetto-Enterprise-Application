package com.android.frontend.view.page.authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.android.frontend.MainActivity
import com.android.frontend.R
import com.android.frontend.view_models.LoginViewModel
import com.android.frontend.navigation.Navigation

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavHostController) {

    val loginViewModel: LoginViewModel = viewModel()
    val context = LocalContext.current

    var isObscured by remember { mutableStateOf(true) }
    val inputBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium)
    val textColor = MaterialTheme.colorScheme.onSurface
    val iconColor = MaterialTheme.colorScheme.onSurface

    val size = with(LocalDensity.current) {
        DpSize(
            width = LocalConfiguration.current.screenWidthDp.dp,
            height = LocalConfiguration.current.screenHeightDp.dp
        )
    }


    val loginErrorString = stringResource(id = R.string.login_failed)
    val loginWithGoogleErrorString = stringResource(id = R.string.login_with_google_failed)


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.login).uppercase(),
                        color = textColor
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Navigation.WelcomePage.route) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = iconColor)
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
        ) {

            Spacer(modifier = Modifier.height(28.dp))

            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .height(size.height * 0.25f)
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = loginViewModel.username,
                onValueChange = { loginViewModel.username = it },
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
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = textColor,
                    focusedBorderColor = inputBorderColor,
                    unfocusedBorderColor = inputBorderColor,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = loginViewModel.password,
                onValueChange = { loginViewModel.password = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.password),
                        color = textColor
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = iconColor
                    )
                },
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
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = textColor,
                    focusedBorderColor = inputBorderColor,
                    unfocusedBorderColor = inputBorderColor,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.navigate(Navigation.ForgetPasswordPage.route) }
            ) {
                Text(
                    text = stringResource(id = R.string.forgot_password),
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    loginViewModel.loginUser(context) { success, errorMessage ->
                        if (success) {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(
                                context,
                                errorMessage ?: loginErrorString,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.login).uppercase()
                )
            }

            Text(
                text = stringResource(id = R.string.or).uppercase(),
                color = textColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    loginViewModel.signInWithGoogle(context) { success, message ->
                        if (success) {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.googlelogo),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(id = R.string.login_with_google),
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.navigate(Navigation.SignupPage.route) }
            ) {
                Text(
                    buildAnnotatedString {
                        append(
                            text = stringResource(id = R.string.not_registered_yet) + " "
                        )
                        withStyle(style = SpanStyle(color = Color.Blue)) {
                            append(
                                text = stringResource(id = R.string.sign_up_now)
                            )
                        }
                    },
                    color = textColor
                )
            }
        }
    }
}
