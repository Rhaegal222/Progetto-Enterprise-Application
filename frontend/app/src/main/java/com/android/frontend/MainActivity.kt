package com.android.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.rememberNavController
import com.android.frontend.model.SecurePreferences
import com.android.frontend.ui.theme.FrontendTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.android.frontend.navigation.Graph
import com.android.frontend.navigation.NavGraph
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrontendTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope()

                    LaunchedEffect(Unit) {
                        scope.launch {
                            if (checkAuthentication()) {
                                navController.navigate(Graph.main) {
                                    popUpTo(Graph.start) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Graph.start) {
                                    popUpTo(Graph.main) { inclusive = true }
                                }
                            }
                        }
                    }
                    NavGraph(navController = navController)
                }
            }
        }
    }

    private suspend fun checkAuthentication(): Boolean {
        val isAuthenticated = withContext(Dispatchers.IO) {
            val accessToken = SecurePreferences.getAccessToken(applicationContext)
            !accessToken.isNullOrEmpty()
        }
        return isAuthenticated
    }
}
