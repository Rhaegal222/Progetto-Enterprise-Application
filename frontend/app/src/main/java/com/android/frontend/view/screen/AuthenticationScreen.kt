package com.android.frontend.view.screen

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.android.frontend.navigation.AuthGraph

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticationScreen() {
    val authenticationNavController = rememberNavController()
    AuthGraph(navController = authenticationNavController)
}
