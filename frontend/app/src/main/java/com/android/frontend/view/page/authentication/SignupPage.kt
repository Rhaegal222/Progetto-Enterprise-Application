package com.android.frontend.view.page.authentication

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
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
import com.android.frontend.view_models.SignUpViewModel
import com.android.frontend.R
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import com.android.frontend.navigation.Navigation


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignupPage(navController: NavHostController) {
    val viewModel: SignUpViewModel = viewModel()
    val context = LocalContext.current

    var isObscured by remember { mutableStateOf(true) }
    val isDarkMode = isSystemInDarkTheme()
    val inputBorderColor = Color.Gray
    val textColor = Color.Black
    val iconColor = Color.Black

    val size = with(LocalDensity.current) {
        DpSize(
            width = LocalConfiguration.current.screenWidthDp.dp,
            height = LocalConfiguration.current.screenHeightDp.dp
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.sign_up).uppercase(),
                        color = if (isDarkMode) Color.White else Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Navigation.WelcomePage.route) }) {
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
        ) {

            Spacer(modifier = Modifier.height(28.dp))

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
                        append(
                            text = stringResource(id = R.string.already_have_account) + " "
                        )
                        withStyle(style = SpanStyle(color = Color.Blue)) {
                            append(text = stringResource(id = R.string.login))
                        }
                    },
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OutlinedTextField(
                    value = viewModel.firstname,
                    onValueChange = { viewModel.firstname = it },
                    label = { Text(stringResource(id = R.string.firstname), color = textColor) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedTextFieldColors(
                        focusedBorderColor = inputBorderColor,
                        unfocusedBorderColor = inputBorderColor,
                        cursorColor = textColor
                    ),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.lastname,
                    onValueChange = { viewModel.lastname = it },
                    label = { Text(stringResource(id = R.string.lastname), color = textColor) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedTextFieldColors(
                        focusedBorderColor = inputBorderColor,
                        unfocusedBorderColor = inputBorderColor,
                        cursorColor = textColor
                    ),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label = { Text(stringResource(id = R.string.email), color = textColor) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedTextFieldColors(
                        focusedBorderColor = inputBorderColor,
                        unfocusedBorderColor = inputBorderColor,
                        cursorColor = textColor
                    ),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    label = { Text(stringResource(id = R.string.password), color = textColor) },
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
                    colors = outlinedTextFieldColors(
                        focusedBorderColor = inputBorderColor,
                        unfocusedBorderColor = inputBorderColor,
                        cursorColor = textColor
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.registerUser { success, errorMessage ->
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(id = R.string.sign_up).uppercase())
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(stringResource(id = R.string.or).uppercase(), color = textColor)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    // context.startSignIn()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.googlelogo),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Black
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(id = R.string.sign_up_with_google),
                    color = Color.Black
                )
            }
        }
    }
}
