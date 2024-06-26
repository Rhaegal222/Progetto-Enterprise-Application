package com.android.frontend

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.android.frontend.model.SecurePreferences
import com.android.frontend.navigation.Graph
import com.android.frontend.navigation.NavGraph
import com.android.frontend.ui.theme.FrontendTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrontendTheme {
                navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }

        /*
        // Verifica se i token di accesso e refresh sono memorizzati e validi
        lifecycleScope.launch {
            checkLoginStatus()
        }
         */
    }

    private fun checkLoginStatus() {
        val accessToken = SecurePreferences.getAccessToken(applicationContext)
        val refreshToken = SecurePreferences.getRefreshToken(applicationContext)

        if (!accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()) {
            Log.d("MainActivity", "Access token: $accessToken")
            navController.navigate(Graph.main) {
                popUpTo(Graph.start) { inclusive = true }
            }
        } else {
            Log.d("MainActivity", "No access token or refresh token found")
            navController.navigate(Graph.start) {
                popUpTo(Graph.main) { inclusive = true }
            }
        }
    }
}
