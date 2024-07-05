package com.android.frontend.view.pages.admin.browse

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.android.frontend.R
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.UserDTO
import com.android.frontend.persistence.SecurePreferences
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.view.component.ErrorDialog
import com.android.frontend.view_models.admin.UserViewModel

@SuppressLint("SuspiciousIndentation", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPage(navController: NavHostController, viewModel: UserViewModel = viewModel()) {
    val context = LocalContext.current
    val users by viewModel.usersLiveData.observeAsState()
    val userImages by viewModel.userImagesLiveData.observeAsState(emptyMap())
    val currentUserId = SecurePreferences.getUser(context)?.id ?: ""

    val isLoading by viewModel.isLoading.observeAsState(false)
    val hasError by viewModel.hasError.observeAsState(false)

    LaunchedEffect(Unit) {
        Log.d("DEBUG", "${getCurrentStackTrace()} Loading users")
        viewModel.fetchAllUsers(context)
    }

    val activeUsers = users?.filter { it.status.toString() == "ACTIVE" } ?: emptyList()
    val deletedUsers = users?.filter { it.status.toString() == "CANCELLED" } ?: emptyList()

    val currentUser = activeUsers.find { it.id == currentUserId }
    val sortedActiveUsers = listOfNotNull(currentUser) + activeUsers.filter { it.id != currentUserId }





    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.all_users)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        }
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (hasError) {
            ErrorDialog(
                title = stringResource(id = R.string.fetching_error),
                onDismiss = { navController.popBackStack() },
                onRetry = { viewModel.fetchAllUsers(context) },
                errorMessage = stringResource(id = R.string.users_load_failed)
            )
        } else {
            UserContent(navController, deletedUsers, sortedActiveUsers, userImages, currentUserId, viewModel)
        }
    }
}

@Composable
fun UserContent(navController: NavHostController, deletedUsers: List<UserDTO>, sortedActiveUsers: List<UserDTO>, userImages: Map<String, Uri>, currentUserId: String, viewModel: UserViewModel) {
    Column{
        Text(
            text = stringResource(id = R.string.active_users),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(8.dp)
        )
        Spacer(modifier = Modifier.height(70.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            items(sortedActiveUsers) { userDTO ->
                UserCard(
                    userDTO,
                    navController,
                    viewModel,
                    userImages[userDTO.id],
                    isDeleted = false,
                    isCurrentUser = currentUserId == userDTO.id
                )
            }
        }

        Text(
            text = stringResource(id = R.string.deleted_users),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            items(deletedUsers) { userDTO ->
                UserCard(
                    userDTO,
                    navController,
                    viewModel,
                    userImages[userDTO.id],
                    isDeleted = true,
                    isCurrentUser = currentUserId == userDTO.id
                )
            }
        }
    }

}


@Composable
fun UserCard(userDTO: UserDTO, navController: NavHostController, viewModel: UserViewModel, imageUri: Uri?, isDeleted: Boolean, isCurrentUser: Boolean) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var showSelfRoleChangeDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(8.dp)
            .width(180.dp)
            .height(260.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            val painter = if (imageUri != null) {
                rememberAsyncImagePainter(model = imageUri)
            } else {
                painterResource(id = R.drawable.user_image)
            }

            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Image(
                    painter = painter,
                    contentDescription = stringResource(id = R.string.user_image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentScale = ContentScale.Crop
                )

                if (isCurrentUser) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color.Green, CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${userDTO.firstName} ${userDTO.lastName}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = userDTO.email,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (!isDeleted) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable {
                            if (isCurrentUser) {
                                showSelfRoleChangeDialog = true
                            } else {
                                showRoleDialog = true
                            }
                        }
                        .padding(vertical = 4.dp)
                        .background(Color.DarkGray, RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = "${stringResource(id = R.string.user_role)}: ${userDTO.role}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown, 
                        contentDescription = stringResource(id = R.string.change_user_role),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        colors = ButtonColorScheme.buttonColors(),
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete_user),
                            modifier = Modifier.size(32.dp) 
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        println("User ${userDTO.firstName} ${userDTO.lastName} ${userDTO.id} deleted")
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        viewModel.deleteUser(userDTO.id, context)
                        println("User ${userDTO.firstName} ${userDTO.lastName} ${userDTO.id} deleted")
                        showDialog = false
                    }
                ) {
                    Text(stringResource(id = R.string.continuee))
                }
            },
            dismissButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = { showDialog = false
                    }) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            title = { Text(stringResource(id = R.string.delete_user)) },
            text = { Text(stringResource(id = R.string.sure_delete_user)) }
        )
    }

    if (showRoleDialog) {
        var selectedRole by remember { mutableStateOf(userDTO.role) }

        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        viewModel.changeUserRole(userDTO.id, selectedRole.toString(), context)
                        showRoleDialog = false
                    }
                ) {
                    Text(stringResource(id = R.string.save_changes))
                }
            },
            dismissButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = { showRoleDialog = false }
                )
                {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            title = { Text(stringResource(id = R.string.change_user_role)) },
            text = {
                Column {
                    Text(stringResource(id = R.string.select_new_role))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        RadioButton(
                            selected = selectedRole.toString() == "USER",
                            onClick = { selectedRole = UserDTO.UserRole.USER }
                        )
                        Text(stringResource(id = R.string.user), modifier = Modifier.align(Alignment.CenterVertically))
                    }
                    Row {
                        RadioButton(
                            selected = selectedRole.toString() == "ADMIN",
                            onClick = { selectedRole = UserDTO.UserRole.ADMIN }
                        )
                        Text(stringResource(id = R.string.admin), modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
            }
        )
    }

    if (showSelfRoleChangeDialog) {
        var selectedRole by remember { mutableStateOf(userDTO.role) }

        AlertDialog(
            onDismissRequest = { showSelfRoleChangeDialog = false },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        viewModel.changeUserRole(userDTO.id, selectedRole.toString(), context)
                        viewModel.logout(context)
                        navController.navigate("login_screen")
                    }
                ) {
                    Text(stringResource(id = R.string.continuee))
                }
            },
            dismissButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = { showSelfRoleChangeDialog = false }
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            title = { Text(stringResource(id = R.string.change_user_role)) },
            text = {
                Column {
                    Text(stringResource(id = R.string.sure_change_your_role))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        RadioButton(
                            selected = selectedRole.toString() == "USER",
                            onClick = { selectedRole = UserDTO.UserRole.USER }
                        )
                        Text(stringResource(id = R.string.user), modifier = Modifier.align(Alignment.CenterVertically))
                    }
                    Row {
                        RadioButton(
                            selected = selectedRole.toString() == "ADMIN",
                            onClick = { selectedRole = UserDTO.UserRole.ADMIN }
                        )
                        Text(stringResource(id = R.string.admin), modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
            }
        )
    }
}
