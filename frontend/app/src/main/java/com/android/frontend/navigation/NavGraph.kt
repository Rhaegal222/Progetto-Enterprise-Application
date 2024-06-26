package com.android.frontend.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.android.frontend.ui.ChangePasswordPage
import com.android.frontend.view.menu.AccountMenu
import com.android.frontend.view.page.AboutPage
import com.android.frontend.view.page.AccountPage
import com.android.frontend.view.page.CartPage
import com.android.frontend.view.page.HomePage
import com.android.frontend.view.page.LoginPage
import com.android.frontend.view.menu.OtherMenu
import com.android.frontend.view.menu.ProfileMenu
import com.android.frontend.view.menu.SecurityMenu
import com.android.frontend.view.page.AllProductsPage
import com.android.frontend.view.page.SignUpPage
import com.android.frontend.view.screen.ForgetPasswordScreen
import com.android.frontend.view.screen.MainScreen
import com.android.frontend.view.screen.WelcomeScreen
import com.android.frontend.view_models.ProductViewModel

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
        composable(Navigation.ProfileMenu.route) {
            ProfileMenu(navController)
        }
        composable(Navigation.CartPage.route) {
            CartPage()
        }
        composable(Navigation.OtherMenu.route) {
            OtherMenu(navController)
        }
        composable(Navigation.AccountPage.route) {
            AccountPage(navController)
        }
        composable(Navigation.AboutPage.route) {
            AboutPage(navController)
        }
        composable(Navigation.AccountMenu.route) {
            AccountMenu(navController)
        }
        composable(Navigation.SecurityMenu.route) {
            SecurityMenu(navController)
        }
        composable(Navigation.ChangePasswordPage.route) {
            ChangePasswordPage(navController)
        }
        composable(Navigation.AllProductsPage.route) {
            AllProductsPage(navController, productViewModel = ProductViewModel())
        }
    }
}

object Graph {
    const val root = "root_graph"
    const val start = "start_graph"
    const val main = "main_graph"
}

