package com.android.frontend.view.screen

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.android.frontend.model.SecurePreferences
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
                Log.d("RootScreen", "User is authenticated. Navigating to MainScreen")
                navController.navigate(Screen.MainScreen.route)
            } else {
                Log.d("RootScreen", "User is not authenticated. Navigating to AuthenticationScreen")
                navController.navigate(Screen.AuthenticationScreen.route)
            }
        }
    }
}

suspend fun checkAuthentication(context: Context): Boolean {
    return withContext(Dispatchers.IO) {
        val accessToken = SecurePreferences.getAccessToken(context)
        !accessToken.isNullOrEmpty()
    }
}
