package com.android.frontend.view.admin

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.android.frontend.R
import com.android.frontend.dto.UserDTO
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.view_models.admin.UserViewModel

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserPage(navController: NavHostController, viewModel: UserViewModel = viewModel()) {
    val context = LocalContext.current
    val users by viewModel.usersLiveData.observeAsState()
    val userImages by viewModel.userImagesLiveData.observeAsState(emptyMap())

    LaunchedEffect(Unit) {
        viewModel.fetchAllUsers(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Users") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(users ?: emptyList()) { userDTO ->
                    UserCard(userDTO, navController, viewModel, userImages[userDTO.id])
                }
            }
        }
    )
}

@Composable
fun UserCard(userDTO: UserDTO, navController: NavHostController, viewModel: UserViewModel, imageUri: Uri?) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(8.dp)
            .width(180.dp)
            .height(260.dp) // Increased height to accommodate larger button
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            val painter = if (imageUri != null) {
                rememberImagePainter(data = imageUri)
            } else {
                painterResource(id = R.drawable.user_image)
            }

            Image(
                painter = painter,
                contentDescription = "User Image",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(80.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${userDTO.firstName} ${userDTO.lastName}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                modifier = Modifier.heightIn(min = 20.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = userDTO.email,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { showRoleDialog = true }
                    .padding(vertical = 4.dp)
                    .background(Color.DarkGray, RoundedCornerShape(8.dp))
            ) {
                Text(
                    text = "Role: ${userDTO.role}",
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    color = Color.White

                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown, // Icon to indicate clickable
                    contentDescription = "Change Role",
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
                    modifier = Modifier.size(60.dp), // Increased button size
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = {
                        showDialog = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(32.dp) // Increased icon size
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(userDTO.id.toString(), context)
                        showDialog = false
                    }
                ) {
                    Text("Continua")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Annulla")
                }
            },
            title = { Text("Elimina Utente") },
            text = { Text("Sei sicuro di voler eliminare questo utente?") }
        )
    }

    if (showRoleDialog) {
        var selectedRole by remember { mutableStateOf(userDTO.role) }

        AlertDialog(
            onDismissRequest = { showRoleDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.changeUserRole(userDTO.id.toString(), selectedRole.toString(), context)
                        showRoleDialog = false
                    }
                ) {
                    Text("Salva")
                }
            },
            dismissButton = {
                Button(onClick = { showRoleDialog = false }) {
                    Text("Annulla")
                }
            },
            title = { Text("Modifica Ruolo") },
            text = {
                Column {
                    Text("Seleziona il nuovo ruolo:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        RadioButton(
                            selected = selectedRole.toString() == "USER",
                            onClick = { selectedRole = UserDTO.UserRole.USER }
                        )
                        Text("USER", modifier = Modifier.align(Alignment.CenterVertically))
                    }
                    Row {
                        RadioButton(
                            selected = selectedRole.toString() == "ADMIN",
                            onClick = { selectedRole = UserDTO.UserRole.ADMIN }
                        )
                        Text("ADMIN", modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
            }
        )
    }
}
