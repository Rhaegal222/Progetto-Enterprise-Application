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
import com.android.frontend.navigation.Navigation
import com.android.frontend.ui.theme.FrontendTheme
import com.android.frontend.view.pages.user.details.WishlistDetailsPage
import kotlinx.coroutines.launch

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
                LaunchedEffect(Unit) {
                    handleIntent(intent)
                }
                AppRouter(navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (!::navController.isInitialized) {
            Log.d("DEBUG", "NavController not initialized, deferring intent handling")
            return
        }

        val appLinkData: Uri? = intent.data
        Log.d("DEBUG", "App link data: $appLinkData")
        appLinkData?.let { uri ->
            val pathSegments = uri.pathSegments
            if (pathSegments.size > 4 && pathSegments[3] == "getWishlistById") {
                val wishlistId = pathSegments[4]
                Log.d("DEBUG", "Wishlist ID: $wishlistId")
                wishlistId?.let {
                    Log.d("DEBUG", "Navigating to wishlist/$wishlistId")
                    lifecycleScope.launch {
                        navController.navigate("wishlist/$wishlistId")
                    }
                }
            }
        }
    }
}
