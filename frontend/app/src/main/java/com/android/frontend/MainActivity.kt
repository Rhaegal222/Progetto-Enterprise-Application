package com.android.frontend

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.android.frontend.navigation.AppRouter
import com.android.frontend.ui.theme.FrontendTheme
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        if (!sharedPreferences.contains("isDarkTheme")) {
            val isDarkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            sharedPreferences.edit().putBoolean("isDarkTheme", isDarkTheme).apply()
        }

        setContent {
            FrontendTheme {
                navController = rememberNavController()
                AppRouter(navController)
            }
        }
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        Log.d("DEBUG", "handleIntent: $intent")
        val appLinkData: Uri? = intent.data
        appLinkData?.let { uri ->
            val wishlistId = uri.getQueryParameter("id")
            wishlistId?.let {
                if (::navController.isInitialized) {
                    lifecycleScope.launch {
                        try {
                            Log.d("DEBUG", "Navigating to wishlistDetails/$it")
                            navController.navigate("wishlistDetails/$it")
                        } catch (e: SocketTimeoutException) {
                            Log.e("ERROR", "Backend initialization failed", e)
                            // Naviga alla schermata di benvenuto
                            navController.navigate("welcomeScreen")
                        } catch (e: Exception) {
                            Log.e("ERROR", "Unexpected error during backend initialization", e)
                            // Naviga alla schermata di benvenuto
                            navController.navigate("welcomeScreen")
                        }
                    }
                }
            }
        }
    }
}
