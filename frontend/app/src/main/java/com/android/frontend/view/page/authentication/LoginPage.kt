package com.android.frontend.view.page.authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.frontend.MainActivity
import com.android.frontend.R
import com.android.frontend.view_models.authentication.LoginViewModel
import com.android.frontend.navigation.Navigation
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.ui.theme.colors.OutlinedButtonColorScheme
import com.android.frontend.ui.theme.colors.OutlinedTextFieldColorScheme
import com.android.frontend.ui.theme.colors.TextButtonColorScheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPage(navController: NavHostController) {


    val loginViewModel: LoginViewModel = viewModel()
    val context = LocalContext.current

    var password = remember { mutableStateOf("") }
    var username = remember { mutableStateOf("") }

    var isObscured by remember { mutableStateOf(true) }

    val size = with(LocalDensity.current) {
        DpSize(
            width = LocalConfiguration.current.screenWidthDp.dp,
            height = LocalConfiguration.current.screenHeightDp.dp
        )
    }

    val loginErrorString = stringResource(id = R.string.login_failed)

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.login).uppercase(),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Navigation.WelcomePage.route) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues).padding(16.dp, 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .height(size.height * 0.25f)
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = loginViewModel.username,
                onValueChange = {
                    loginViewModel.username = it
                    username.value = it
                                },
                label = {
                    Text(
                        text = stringResource(id = R.string.email)
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldColorScheme.colors(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = loginViewModel.password,
                onValueChange = {
                    loginViewModel.password = it
                    password.value = it
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.password)
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isObscured = !isObscured }) {
                        Icon(
                            imageVector = if (isObscured) Icons.Default.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (isObscured) PasswordVisualTransformation() else VisualTransformation.None,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldColorScheme.colors(),
                keyboardOptions = if (password.value.length > 8 && username.value.length > 8){
                    KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                } else {
                    KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { navController.navigate(Navigation.ForgetPasswordPage.route) },
                colors = TextButtonColorScheme.textButtonColors(),
            ) {
                Text(
                    text = stringResource(id = R.string.forgot_password)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                modifier = Modifier.width(200.dp),
                colors = ButtonColorScheme.buttonColors()
            ) {
                Text(
                    text = stringResource(id = R.string.login).uppercase()
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text( text = stringResource(id = R.string.or).uppercase() )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedButton(
                onClick = {
                    loginViewModel.signInWithGoogle(context) { success, errorMessage ->
                        if (success) {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.width(200.dp),
                colors = OutlinedButtonColorScheme.outlinedButtonColors()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text( text = stringResource(id = R.string.login_with_google) )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { navController.navigate(Navigation.SignupPage.route) },
                colors = TextButtonColorScheme.textButtonColors()
            ) {
                Text(
                    buildAnnotatedString {
                        append( text = stringResource(id = R.string.not_registered_yet) + " " )
                        append( text = stringResource(id = R.string.sign_up_now) )
                    }
                )
            }

            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}
