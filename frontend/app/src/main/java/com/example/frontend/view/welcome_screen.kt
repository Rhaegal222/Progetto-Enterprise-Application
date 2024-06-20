package com.example.frontend.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.frontend.R
import com.example.frontend.navigation.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WelcomeScreenContent(navController: NavHostController) {
    val context = LocalContext.current
    val height = LocalConfiguration.current.screenHeightDp.dp
    val isDarkMode = isSystemInDarkTheme()
    val backgroundColor = if (isDarkMode) Color.Gray else Color.White
    val textColor = if (isDarkMode) Color.White else Color.Black

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(height * 0.1f))
            Image(
                painter = painterResource(R.drawable.logo), // R.drawable.ChatBot rappresenta il nome del file nella cartella drawable
                contentDescription = "ChatBot Image",
                modifier = Modifier
                    .size(400.dp)  // Imposta la dimensione desiderata
                    .padding(16.dp)  // Aggiunge il padding se necessario
            )
            Text(
                text = "Benvenuto", // Replace with your welcome title
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Accedi o registrati per acquistare i prodotti", // Replace with your welcome subtitle
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = textColor
            )
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        navController.navigate(Screen.LoginScreen.route)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Text(text = "LOGIN")
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = {
                        navController.navigate(Screen.SignUpScreen.route)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "REGISTRATI")
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}