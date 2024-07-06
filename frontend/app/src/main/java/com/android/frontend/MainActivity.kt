package com.android.frontend

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.android.frontend.navigation.AppRouter
import com.android.frontend.ui.theme.FrontendTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        if (!sharedPreferences.contains("isDarkTheme")) {
            val isDarkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            sharedPreferences.edit().putBoolean("isDarkTheme", isDarkTheme).apply()
        }

        setContent {
            FrontendTheme {
                val navController = rememberNavController()
                handleIntent(navController, intent)
                AppRouter(navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        if (intent.data != null) {
            super.onNewIntent(intent)
        }
        intent.let {
            setContent {
                FrontendTheme {
                    val navController = rememberNavController()
                    handleIntent(navController, it)
                    AppRouter(navController)
                }
            }
        }
    }

    @Composable
    private fun handleIntent(navController: NavHostController, intent: Intent) {
        LaunchedEffect(intent) {
            val appLinkData: Uri? = intent.data
            appLinkData?.let { uri ->
                val wishlistId = uri.getQueryParameter("id")
                wishlistId?.let {
                    navController.navigate("wishlistDetails/$it")
                }
            }
        }
    }
}
