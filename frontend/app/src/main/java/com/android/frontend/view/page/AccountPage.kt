package com.android.frontend.view.page

import android.annotation.SuppressLint
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
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.frontend.view_models.ProfileViewModel
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors

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
    val profileImage by profileViewModel.profileImage

    var firstNameInput by remember { mutableStateOf(firstName) }
    var lastNameInput by remember { mutableStateOf(lastName) }
    var emailInput by remember { mutableStateOf(email) }
    var phoneNumberInput by remember { mutableStateOf(phoneNumber) }

    var isEditMode by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
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
                    painter = painterResource(id = profileImage),
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
                            profileViewModel.updateUserProfile(
                                firstNameInput,
                                lastNameInput,
                                emailInput,
                                phoneNumberInput
                            )
                            isEditMode = false
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
                            phoneNumberInput = phoneNumber
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
                colors = outlinedTextFieldColors(
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color.Black
                )
            )
        }
    }
}
