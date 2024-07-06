package com.android.frontend.view.pages.user.browse

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil.compose.rememberAsyncImagePainter
import com.android.frontend.view_models.user.UserViewModel
import com.android.frontend.R
import com.android.frontend.persistence.SecurePreferences
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.ui.theme.colors.TextButtonColorScheme
import com.android.frontend.view.component.ErrorDialog

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PersonalInformationPage(navController: NavController, userViewModel: UserViewModel = viewModel()) {
    val context = LocalContext.current

    val isLoading by userViewModel.isLoading.observeAsState(false)
    val hasError by userViewModel.hasError.observeAsState(false)

    LaunchedEffect(Unit) {
        userViewModel.getUserDetails(context)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (hasError) {
        ErrorDialog(
            title = stringResource(id = R.string.fetching_error),
            onDismiss = { navController.popBackStack() },
            onRetry = { userViewModel.getUserDetails(context) },
            errorMessage = stringResource(id = R.string.fetching_error_message)
        )
    } else {
        PersonalInformationContent(
            navController,
            userViewModel,
            context
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInformationContent(
    navController: NavController,
    userViewModel: UserViewModel,
    context: Context
) {
    val userImage by userViewModel.profileImageLiveData.observeAsState()
    val userDetails by userViewModel.userDetailsLiveData.observeAsState()

    var isEditMode by remember { mutableStateOf(false) }
    var showEmailChangeDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            userViewModel.updatePhotoUser(context, it)
        }
    }

    var firstName by remember { mutableStateOf(userDetails?.firstName ?: "") }
    var lastName by remember { mutableStateOf(userDetails?.lastName ?: "") }
    var email by remember { mutableStateOf(userDetails?.email ?: "") }
    var phoneNumber by remember { mutableStateOf(userDetails?.phoneNumber ?: "") }

    val originalEmail = userDetails?.email

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.personal_informations).uppercase(),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp, 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(modifier = Modifier.height(25.dp))

            Box(modifier = Modifier.size(160.dp)) {
                if (userImage != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = userImage),
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
                if (SecurePreferences.getProvider(context) != "google")
                    IconButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
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

            Spacer(modifier = Modifier.height(25.dp))

            if (isEditMode) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(stringResource(id = R.string.firstname)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text(stringResource(id = R.string.lastname)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(id = R.string.email)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text(stringResource(id = R.string.phone_number)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors()
                )
            } else {
                OutlinedTextField(
                    value = userDetails?.firstName ?: "",
                    onValueChange = {  },
                    label = { Text(stringResource(id = R.string.firstname)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = userDetails?.lastName ?: "",
                    onValueChange = { },
                    label = { Text(stringResource(id = R.string.lastname)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = userDetails?.email ?: "",
                    onValueChange = { },
                    label = { Text(stringResource(id = R.string.email)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(),
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = if (userDetails?.phoneNumber.isNullOrEmpty()) "Nessun numero di cellulare" else userDetails?.phoneNumber ?: "",
                    onValueChange = { },
                    label = { Text(stringResource(id = R.string.phone_number)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(),
                    readOnly = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isEditMode) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        shape = RoundedCornerShape(12.dp),
                        onClick = {
                            if (email != originalEmail) {
                                showEmailChangeDialog = true
                            } else {
                                userViewModel.updateMe(context, firstName, lastName, email, phoneNumber)
                                isEditMode = false
                            }
                        },
                        colors = ButtonColorScheme.buttonColors(),
                        modifier = Modifier
                            .height(40.dp)
                            .width(150.dp)
                    ) {
                        Text(text = stringResource(id = R.string.apply_changes))
                    }

                    Button(
                        shape = RoundedCornerShape(12.dp),
                        onClick = {
                            isEditMode = false
                            firstName = userDetails?.firstName ?: ""
                            lastName = userDetails?.lastName ?: ""
                            email = userDetails?.email ?: ""
                            phoneNumber = userDetails?.phoneNumber ?: ""
                        },
                        modifier = Modifier
                            .height(40.dp)
                            .width(150.dp),
                        colors = ButtonColorScheme.buttonColors()
                    ) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }
            } else {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        isEditMode = true
                    },
                    modifier = Modifier
                        .height(40.dp)
                        .width(150.dp),
                    colors = ButtonColorScheme.buttonColors()
                ) {
                    Text(text = stringResource(id = R.string.edit))
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            if (showEmailChangeDialog) {
                AlertDialog(
                    onDismissRequest = { showEmailChangeDialog = false },
                    title = { Text(text = stringResource(id = R.string.email_change)) },
                    text = { Text(text = stringResource(id = R.string.email_change_message)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                userViewModel.updateMe(context, firstName, lastName, email, phoneNumber)
                                userViewModel.logout(context)
                                navController.navigate("LoginPage") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                                showEmailChangeDialog = false
                            }
                        ) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEmailChangeDialog = false }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                )
            }
        }
    }
}
