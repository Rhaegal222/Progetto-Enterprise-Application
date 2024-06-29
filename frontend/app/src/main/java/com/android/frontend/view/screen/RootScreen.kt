package com.android.frontend.view.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.android.frontend.controller.infrastructure.TokenManager
import com.android.frontend.navigation.Screen

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RootScreen(navController: NavHostController) {
    val context = LocalContext.current

    Scaffold {
        LaunchedEffect(Unit) {
            TokenManager().isLoggedIn(context) { isAuthenticated ->
                if (isAuthenticated) {
                    Log.d("RootScreen", "User is authenticated. Navigating to MainScreen")
                    navController.navigate(Screen.MainScreen.route)
                } else {
                    Log.d("RootScreen", "User is not authenticated. Navigating to AuthenticationScreen")
                    navController.navigate(Screen.AuthenticationScreen.route)
                }
            }
        }
    }
}
