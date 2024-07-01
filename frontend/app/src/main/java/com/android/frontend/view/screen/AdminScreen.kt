package com.android.frontend.view.screen

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.android.frontend.navigation.AdminGraph

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AdminScreen() {
    val authenticationNavController = rememberNavController()
    AdminGraph(navController = authenticationNavController)
}
