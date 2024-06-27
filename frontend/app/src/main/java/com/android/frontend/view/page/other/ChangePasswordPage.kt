package com.android.frontend.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.frontend.MainActivity
import com.android.frontend.R
import com.android.frontend.view_models.ChangePasswordViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordPage(navController: NavHostController) {
    val changePasswordViewModel: ChangePasswordViewModel = viewModel()
    val context = LocalContext.current as MainActivity

    val inputBorderColor = Color.Gray
    val textColor = Color.Black
    val iconColor = Color.Black

    var isObscured by remember { mutableStateOf(true) }
    val passwordChangedMessage = stringResource(R.string.passwordChangedSucc)

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
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
            Spacer(modifier = Modifier.height(50.dp))
            if (changePasswordViewModel.errorMessage != 0) {
                Text(
                    text = stringResource(id = changePasswordViewModel.errorMessage),
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            if (changePasswordViewModel.successMessage != 0) {
                Text(
                    text = stringResource(id = changePasswordViewModel.successMessage),
                    color = Color.Green,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            OutlinedTextField(
                value = changePasswordViewModel.oldPassword,
                onValueChange = { changePasswordViewModel.oldPassword = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.oldPassword),
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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = changePasswordViewModel.newPassword,
                onValueChange = { changePasswordViewModel.newPassword = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.newPassword),
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

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = changePasswordViewModel.repeatNewPassword,
                onValueChange = { changePasswordViewModel.repeatNewPassword = it },
                label = {
                    Text(
                        text = stringResource(id = R.string.repeatNewPassword),
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

            Button(
                onClick = {
                    changePasswordViewModel.changePassword(context) { success ->
                        if (success) {
                            Toast.makeText(context, passwordChangedMessage, Toast.LENGTH_LONG).show()
                            navController.popBackStack()
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
                    text = stringResource(id = R.string.apply),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password requirements text
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.passwordRequirements_line1),
                    color = Color.Gray
                )
                Text(
                    text = stringResource(R.string.passwordRequirements_line2),
                    color = Color.Gray
                )
                Text(
                    text = stringResource(R.string.passwordRequirements_line3),
                    color = Color.Gray
                )
                Text(
                    text = stringResource(R.string.passwordRequirements_line4),
                    color = Color.Gray
                )
            }
        }
    }
}
