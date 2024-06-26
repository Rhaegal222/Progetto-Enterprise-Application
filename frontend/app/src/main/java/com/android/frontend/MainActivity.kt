package com.android.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.android.frontend.model.SecurePreferences
import com.android.frontend.navigation.Graph
import com.android.frontend.navigation.NavGraph
import com.android.frontend.ui.theme.FrontendTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrontendTheme {
                navController = rememberNavController()
                CheckAuthentication(navController)
            }
        }
    }

    @Composable
    fun CheckAuthentication(navController: NavHostController) {
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            val isAuthenticated = withContext(Dispatchers.IO) {
                // Controlla se l'utente è autenticato (es. controllando i token di accesso)
                val accessToken = SecurePreferences.getAccessToken(applicationContext)
                !accessToken.isNullOrEmpty()
            }
            if (isAuthenticated) {
                navController.navigate(Graph.main) {
                    popUpTo(Graph.start) { inclusive = true }
                }
            } else {
                navController.navigate(Graph.start) {
                    popUpTo(Graph.main) { inclusive = true }
                }
            }
        }
        NavGraph(navController = navController)
    }
}
