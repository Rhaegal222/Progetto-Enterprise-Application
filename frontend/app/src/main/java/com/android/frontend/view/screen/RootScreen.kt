package com.android.frontend.view.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.android.frontend.controller.infrastructure.TokenManager
import com.android.frontend.navigation.Screen

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RootScreen(navController: NavHostController) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (TokenManager.getInstance().isUserLoggedIn(context)) {
            Log.d("DEBUG RootScreen", "User is authenticated. Navigating to MainScreen")
            navController.navigate(Screen.MainScreen.route)
        } else {
            Log.d("DEBUG RootScreen", "User is not authenticated. Navigating to AuthenticationScreen")
            navController.navigate(Screen.AuthenticationScreen.route)
        }
    }
}
