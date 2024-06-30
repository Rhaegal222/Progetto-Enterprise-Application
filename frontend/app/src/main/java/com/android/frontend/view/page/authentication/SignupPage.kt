package com.android.frontend.view.page.authentication

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.frontend.MainActivity
import com.android.frontend.R
import com.android.frontend.navigation.Navigation
import com.android.frontend.view_models.LoginViewModel
import com.android.frontend.view_models.SignUpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignupPage(navController: NavHostController) {

    val signUpViewModel: SignUpViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()

    val context = LocalContext.current

    var isObscured by remember { mutableStateOf(true) }

    val size = with(LocalDensity.current) {
        DpSize(
            width = LocalConfiguration.current.screenWidthDp.dp,
            height = LocalConfiguration.current.screenHeightDp.dp
        )
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.sign_up).uppercase()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Navigation.WelcomePage.route) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues).padding(8.dp, 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .height(size.height * 0.25f)
                    .fillMaxWidth()
            )

            TextButton(
                onClick = { navController.navigate(Navigation.LoginPage.route) }
            ) {
                Text(
                    buildAnnotatedString {
                        append( text = stringResource(id = R.string.already_have_account) + " ")
                        append(text = stringResource(id = R.string.login))
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OutlinedTextField(
                    value = signUpViewModel.firstname,
                    onValueChange = { signUpViewModel.firstname = it },
                    label = { Text(stringResource(id = R.string.firstname)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = signUpViewModel.lastname,
                    onValueChange = { signUpViewModel.lastname = it },
                    label = { Text(stringResource(id = R.string.lastname)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = signUpViewModel.email,
                    onValueChange = { signUpViewModel.email = it },
                    label = { Text(stringResource(id = R.string.email)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = signUpViewModel.password,
                    onValueChange = { signUpViewModel.password = it },
                    label = { Text(stringResource(id = R.string.password)) },
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
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        signUpViewModel.registerUser(context) { success, errorMessage ->
                            if (success) {
                                Toast.makeText(
                                    context,
                                    "Registrazione avvenuta con successo",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Registrazione fallita: $errorMessage",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.sign_up).uppercase())
                }

            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(stringResource(id = R.string.or).uppercase())

            Spacer(modifier = Modifier.height(16.dp))

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
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text( text = stringResource(id = R.string.sign_up_with_google) )
            }
        }
    }
}
