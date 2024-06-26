package com.android.frontend.view.page

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

        Column(
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
            Spacer(modifier = Modifier.height(50.dp))

            Image(
                painter = painterResource(id = profileImage),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "Il tuo profilo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileField(label = "Nome", value = firstName)
            ProfileField(label = "Cognome", value = lastName)
            ProfileField(label = "Email", value = email)
            ProfileField(label = "Numero di telefono", value = phoneNumber)
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
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
