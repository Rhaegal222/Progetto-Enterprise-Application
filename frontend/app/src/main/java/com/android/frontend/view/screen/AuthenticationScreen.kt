package com.android.frontend.view.screen

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.android.frontend.view.page.authentication.WelcomePage

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticationScreen(navController: NavHostController) {
    WelcomePage(navController = navController)
}
