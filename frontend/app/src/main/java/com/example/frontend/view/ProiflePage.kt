package com.example.frontend.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.R

@Composable
fun ProfilePage() {
    println("ProfilePage loaded")
    val profileImage = painterResource(id = R.drawable.user_image) // Replace with actual profile image resource
    val userName = "Nome Utente" // Replace with actual username
    val email = "email@example.com" // Replace with actual email
    val firstName = "Nome" // Replace with actual first name
    val lastName = "Cognome" // Replace with actual last name

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
            painter = profileImage,
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
        ProfileField(label = "Nome Utente", value = userName)
        ProfileField(label = "Email", value = email)

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
