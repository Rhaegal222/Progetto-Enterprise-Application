package com.android.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.frontend.view.pages.user.details.WishlistDetailsPage
import com.android.frontend.view.screen.AuthenticationScreen
import com.android.frontend.view.screen.MainScreen
import com.android.frontend.view.screen.RootScreen
import com.android.frontend.view.screen.AdminScreen


object Graph {
    const val AUTHENTICATION = "authentication"
    const val MAIN = "main"
    const val ROOT = "root"
    const val ADMIN = "admin"
}

@Composable
fun RootGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Graph.ROOT) {
        composable(Graph.ROOT) { RootScreen(navController) }
        composable(Graph.AUTHENTICATION) { AuthenticationScreen() }
        composable(Graph.MAIN) { MainScreen() }
        composable(Graph.ADMIN) { AdminScreen() }
        composable("wishlist/{wishlistId}") { backStackEntry ->
            val wishlistId = backStackEntry.arguments?.getString("wishlistId") ?: ""
            WishlistDetailsPage(navController = navController, wishlistId = wishlistId)
        }
    }
}
