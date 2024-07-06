package com.android.frontend

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
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

class MainActivity : ComponentActivity() {

    private var backendBaseUrl by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        if (!sharedPreferences.contains("isDarkTheme")) {
            val isDarkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            sharedPreferences.edit().putBoolean("isDarkTheme", isDarkTheme).apply()
        }

        lifecycleScope.launch {
            RetrofitInstance.initializeBackendBaseUrl()
            backendBaseUrl = CurrentDataUtils.backendBaseUrl
            setContent {
                FrontendTheme {
                    val navController = rememberNavController()
                    HandleIntent(navController, intent, backendBaseUrl)
                    AppRouter(navController)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        if (intent.data != null) {
            super.onNewIntent(intent)
        }
        lifecycleScope.launch {
            RetrofitInstance.initializeBackendBaseUrl()
            backendBaseUrl = CurrentDataUtils.backendBaseUrl
            setContent {
                FrontendTheme {
                    val navController = rememberNavController()
                    HandleIntent(navController, intent, backendBaseUrl)
                    AppRouter(navController)
                }
            }
        }
    }

    @Composable
    private fun HandleIntent(navController: NavHostController, intent: Intent, backendBaseUrl: String?) {
        LaunchedEffect(intent) {
            val appLinkData: Uri? = intent.data
            appLinkData?.let { uri ->
                val wishlistId = uri.getQueryParameter("id")
                wishlistId?.let {
                    navController.navigate("wishlistDetails/$it?backendBaseUrl=${backendBaseUrl}")
                }
            }
        }
    }
}
