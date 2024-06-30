package com.android.frontend.view.page.other.account

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.frontend.view_models.UserViewModel
import com.android.frontend.R
import coil.compose.rememberAsyncImagePainter
import com.android.frontend.config.getCurrentStackTrace

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PersonalInformationPage(navController: NavController, userViewModel: UserViewModel = viewModel()) {
    val context = LocalContext.current

    var isObscured by remember { mutableStateOf(true) }

    val profileImage by userViewModel.profileImageLiveData.observeAsState()
    val profile by userViewModel.profileLiveData.observeAsState()

    var firstName by remember { mutableStateOf(profile?.firstName ?: "") }
    var lastName by remember { mutableStateOf(profile?.lastName ?: "") }
    var email by remember { mutableStateOf(profile?.email ?: "") }
    var phoneNumber by remember { mutableStateOf(profile?.phoneNumber ?: "Nessun numero di telefono") }

    val isLoading by userViewModel.isLoading

    var isEditMode by remember { mutableStateOf(false) }
    var showEmailChangeDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            userViewModel.updatePhotoUser(context, it)
        }
    }

    LaunchedEffect(profile) {
        profile?.let {
            firstName = it.firstName
            lastName = it.lastName
            email = it.email
            phoneNumber = if (!it.phoneNumber.isNullOrEmpty()) it.phoneNumber else "Nessun numero di telefono"
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.fetchData(context)
        Log.d("DEBUG", "${getCurrentStackTrace()} Profile: $profile")
        Log.d("DEBUG", "${getCurrentStackTrace()} Profile Image: $profileImage")
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.personal_informations).uppercase(),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                Box(modifier = Modifier.size(160.dp)) {
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
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile Image"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(stringResource(id = R.string.firstname)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !isEditMode
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text(stringResource(id = R.string.lastname)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !isEditMode
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(id = R.string.email)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !isEditMode
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text(stringResource(id = R.string.phone_number)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = !isEditMode
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (isEditMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Button(
                            onClick = {
                                if (email != firstName) {
                                    showEmailChangeDialog = true
                                } else {
                                    userViewModel.updateUserProfile(
                                        context,
                                        firstName,
                                        lastName,
                                        email,
                                        phoneNumber
                                    )
                                    isEditMode = false
                                }
                            },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = stringResource(id = R.string.apply_changes))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                isEditMode = false
                            },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            isEditMode = true
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = stringResource(id = R.string.edit))
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
                                    userViewModel.updateUserProfile(
                                        context,
                                        firstName,
                                        lastName,
                                        email,
                                        phoneNumber
                                    )
                                    userViewModel.logout(context)
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
    }
}