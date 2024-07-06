package com.android.frontend

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.android.frontend.navigation.AppRouter
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.ui.theme.FrontendTheme
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class MainActivity : ComponentActivity() {

    private var backendBaseUrl by mutableStateOf<String?>(null)
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

        initializeBackend()
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun initializeBackend() {
        lifecycleScope.launch {
            try {
                RetrofitInstance.initializeBackendBaseUrl()
                backendBaseUrl = CurrentDataUtils.backendBaseUrl
            } catch (e: SocketTimeoutException) {
                Log.e("ERROR", "Backend initialization failed", e)
                // Show error screen or handle the error accordingly
                navController.navigate("welcomeScreen")
            } catch (e: Exception) {
                Log.e("ERROR", "Unexpected error during backend initialization", e)
                // Show error screen or handle the error accordingly
                navController.navigate("welcomeScreen")
            }
        }
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
                            RetrofitInstance.initializeBackendBaseUrl()
                            backendBaseUrl = CurrentDataUtils.backendBaseUrl
                            navController.navigate("wishlistDetails/$it?backendBaseUrl=${backendBaseUrl}")
                        } catch (e: SocketTimeoutException) {
                            Log.e("ERROR", "Backend initialization failed", e)
                            // Show error screen or handle the error accordingly
                            navController.navigate("welcomeScreen")
                        } catch (e: Exception) {
                            Log.e("ERROR", "Unexpected error during backend initialization", e)
                            // Show error screen or handle the error accordingly
                            navController.navigate("welcomeScreen")
                        }
                    }
                }
            }
        }
    }
}
