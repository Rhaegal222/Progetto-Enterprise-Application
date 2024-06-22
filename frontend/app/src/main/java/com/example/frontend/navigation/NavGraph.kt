package com.example.frontend.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.frontend.view.CartPage
import com.example.frontend.view.HomePage
import com.example.frontend.view.LoginPage
import com.example.frontend.view.MenuPage
import com.example.frontend.view.ProfileMenuPage
import com.example.frontend.view.ProfilePage
import com.example.frontend.view.SignUpPage
import com.example.frontend.view.screen.ForgetPasswordScreen
import com.example.frontend.view.screen.MainScreen
import com.example.frontend.view.screen.WelcomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Graph.start,
        route = Graph.root
    ) {
        startGraph(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.startGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.WelcomeScreen.route,
        route = Graph.start
    ) {
        composable(Screen.MainScreen.route) { MainScreen() }
        composable(Screen.WelcomeScreen.route) { WelcomeScreen(navController) }
        composable(Screen.LoginScreen.route) { LoginPage(navController) }
        composable(Screen.SignUpScreen.route) { SignUpPage(navController) }
        composable(Screen.ForgetPassword.route) { ForgetPasswordScreen(navController)}
    }
}

@Composable
fun MainPageGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Navigation.HomePage.route) {
        composable(Navigation.HomePage.route) {
            HomePage(navController)
        }
        composable(Navigation.CartPage.route) {
            CartPage()
        }
        composable(Navigation.MenuPage.route) {
            MenuPage(navController)
        }
        composable(Navigation.ProfileMenuPage.route) {
            ProfileMenuPage(navController)
        }
        composable(Navigation.ProfilePage.route) {
            ProfilePage()
        }
    }
}

object Graph {
    const val root = "root_graph"
    const val start = "start_graph"
    const val main = "main_graph"
}

