package com.android.frontend.view.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.navigation.Screen
import com.android.frontend.persistence.SecurePreferences

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RootScreen(navController: NavHostController) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (TokenManager.getInstance().isUserLoggedIn(context)) {
            if (SecurePreferences.getUser(context)?.role == "ADMIN") {
                Log.d("DEBUG", "${getCurrentStackTrace()} User is authenticated as ADMIN. Navigating to AdminScreen")
                navController.navigate(Screen.AdminScreen.route)
            } else {
                Log.d("DEBUG", "${getCurrentStackTrace()} User is authenticated. Navigating to MainScreen")
                navController.navigate(Screen.MainScreen.route)
            }
        } else {
            Log.d("DEBUG", "${getCurrentStackTrace()} User is not authenticated. Navigating to AuthenticationScreen")
            navController.navigate(Screen.AuthenticationScreen.route)
        }
    }
}
