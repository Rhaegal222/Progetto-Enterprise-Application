package com.android.frontend.view.screen

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.android.frontend.controller.infrastructure.TokenManager
import com.android.frontend.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RootScreen(navController: NavHostController) {
    val context = LocalContext.current

    Scaffold {
        LaunchedEffect(Unit) {
            val isAuthenticated = checkAuthentication(context)
            if (isAuthenticated) {
                Log.d("DEBUG RootScreen", "User is authenticated. Navigating to MainScreen")
                navController.navigate(Screen.MainScreen.route)
            } else {
                Log.d("DEBUG RootScreen", "User is not authenticated. Navigating to AuthenticationScreen")
                navController.navigate(Screen.AuthenticationScreen.route)
            }
        }
    }
}

private suspend fun checkAuthentication(context: Context): Boolean {
    val accessToken = TokenManager.getInstance().getAccessToken(context)
    return if (accessToken != null) {
        true
    } else {
        withContext(Dispatchers.IO) {
            TokenManager.getInstance().tryRefreshToken(context)
        }
    }
}
