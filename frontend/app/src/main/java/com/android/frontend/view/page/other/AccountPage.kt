package com.android.frontend.view.page.other

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AccountPage(navController: NavController, profileViewModel: ProfileViewModel = viewModel()) {

    val context = LocalContext.current

    val isDarkMode = isSystemInDarkTheme()

    val profileImage by profileViewModel.profileImageLiveData.observeAsState()
    profileViewModel.fetchUserProfileImage(context)

    val user by profileViewModel.userLiveData.observeAsState()
    profileViewModel.fetchUserProfile(context)

    var firstName = user?.firstName ?: ""
    var lastName = user?.lastName ?: ""
    var email = user?.email ?: ""
    var phoneNumber = user?.phoneNumber ?: "Nessun numero di telefono"

    val previusFirstName = user?.firstName ?: ""
    val previusLastName = user?.lastName ?: ""
    val previusEmail = user?.email ?: ""
    val previusPhoneNumber = user?.phoneNumber ?: "Nessun numero di telefono"

    var InputFirstName = user?.firstName ?: ""
    var InputLastName = user?.lastName ?: ""
    var InputEmail = user?.email ?: ""
    var InputPhoneNumber = ""

    val isLoading by profileViewModel.isLoading

    var isEditMode by remember { mutableStateOf(false) }
    var showEmailChangeDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileViewModel.updatePhotoUser(context, it)
        }
    }

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
                    Box(
                        modifier = Modifier.size(160.dp)
                    ) {
                        if (profileImage != null) {
                            Image(
                                painter = rememberAsyncImagePainter(model = profileImage),
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.user_image),
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(CircleShape)
                            )
                        }
                        IconButton(
                            onClick = {
                                imagePickerLauncher.launch("image/*")
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile Image",
                                tint = Color.White
                            )
                        }
                    }
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
                    item { EditableProfileField(label = "Nome", value = InputFirstName, onValueChange = { InputFirstName = it }) }
                    item { EditableProfileField(label = "Cognome", value = InputLastName, onValueChange = { InputLastName = it }) }
                    item { EditableProfileField(label = "Email", value = InputEmail, onValueChange = { InputEmail = it }) }
                    item { EditableProfileField(label = "Numero di telefono", value = InputPhoneNumber, onValueChange = { InputPhoneNumber = it }) }

                    item { Spacer(modifier = Modifier.height(20.dp)) }

                    item {
                        Button(
                            onClick = {
                                if (email != InputEmail) {
                                    showEmailChangeDialog = true
                                } else {
                                    profileViewModel.updateUserProfile(
                                        context,
                                        InputFirstName,
                                        InputLastName,
                                        InputEmail,
                                        InputPhoneNumber
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
                                context,
                                InputFirstName,
                                InputLastName,
                                InputEmail,
                                InputPhoneNumber
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

