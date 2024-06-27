package com.android.frontend.view.page.other

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.frontend.view_models.ProfileViewModel
import com.android.frontend.R

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AccountPage(navController: NavController, profileViewModel: ProfileViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        profileViewModel.fetchUserProfile()
    }

    val isDarkMode = isSystemInDarkTheme()

    val firstName by profileViewModel.firstName
    val lastName by profileViewModel.lastName
    val email by profileViewModel.email
    val phoneNumber by profileViewModel.phoneNumber
    val isLoading by profileViewModel.isLoading

    var firstNameInput by remember { mutableStateOf(firstName) }
    var lastNameInput by remember { mutableStateOf(lastName) }
    var emailInput by remember { mutableStateOf(email) }
    var phoneNumberInput by remember { mutableStateOf(if (phoneNumber == "Nessun numero di telefono") "" else phoneNumber) }

    var isEditMode by remember { mutableStateOf(false) }
    var showEmailChangeDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

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
        if (isLoading) {
            // Show a loading indicator
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFe3f2fd),
                                Color(0xFFbbdefb),
                                Color(0xFF90caf9),
                            )
                        )
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { Spacer(modifier = Modifier.height(50.dp)) }

                item {
                    Image(
                        painter = painterResource(id = R.drawable.user_image),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                    )
                }

                item { Spacer(modifier = Modifier.height(50.dp)) }

                item {
                    Text(
                        text = "Il tuo profilo",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        color = Color.Black
                    )
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }

                if (isEditMode) {
                    item { EditableProfileField(label = "Nome", value = firstNameInput, onValueChange = { firstNameInput = it }) }
                    item { EditableProfileField(label = "Cognome", value = lastNameInput, onValueChange = { lastNameInput = it }) }
                    item { EditableProfileField(label = "Email", value = emailInput, onValueChange = { emailInput = it }) }
                    item { EditableProfileField(label = "Numero di telefono", value = phoneNumberInput, onValueChange = { phoneNumberInput = it }) }

                    item { Spacer(modifier = Modifier.height(20.dp)) }

                    item {
                        Button(
                            onClick = {
                                if (emailInput != email) {
                                    showEmailChangeDialog = true
                                } else {
                                    profileViewModel.updateUserProfile(
                                        firstNameInput,
                                        lastNameInput,
                                        emailInput,
                                        phoneNumberInput
                                    )
                                    isEditMode = false
                                }
                            },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "Applica Cambiamenti")
                        }
                    }
                } else {
                    item { ReadOnlyProfileField(label = "Nome", value = firstName) }
                    item { ReadOnlyProfileField(label = "Cognome", value = lastName) }
                    item { ReadOnlyProfileField(label = "Email", value = email) }
                    item { ReadOnlyProfileField(label = "Numero di telefono", value = phoneNumber) }

                    item { Spacer(modifier = Modifier.height(20.dp)) }

                    item {
                        Button(
                            onClick = {
                                firstNameInput = firstName
                                lastNameInput = lastName
                                emailInput = email
                                phoneNumberInput = if (phoneNumber == "Nessun numero di telefono") "" else phoneNumber
                                isEditMode = true
                            },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "Modifica")
                        }
                    }
                }
            }
        }

        if (showEmailChangeDialog) {
            AlertDialog(
                onDismissRequest = { showEmailChangeDialog = false },
                title = { Text(text = "Cambio email") },
                text = { Text("Per poter modificare l'email devi rieffettuare l'accesso.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            profileViewModel.updateUserProfile(
                                firstNameInput,
                                lastNameInput,
                                emailInput,
                                phoneNumberInput
                            )
                            profileViewModel.logout(context)
                        }
                    ) {
                        Text("Continua")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showEmailChangeDialog = false
                            isEditMode = false
                        }
                    ) {
                        Text("Cancella")
                    }
                }
            )
        }
    }
}

@Composable
fun ReadOnlyProfileField(label: String, value: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(start = 15.dp, end = 15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableProfileField(label: String, value: String, onValueChange: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .padding(start = 15.dp, end = 15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color.Black
                )
            )
        }
    }
}
