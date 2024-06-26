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
import com.android.frontend.view.page.ProductDetailsPage
import com.android.frontend.view.page.AboutPage
import com.android.frontend.view.page.AccountPage
import com.android.frontend.view.page.CartPage
import com.android.frontend.view.page.HomePage
import com.android.frontend.view.page.LoginPage
import com.android.frontend.view.menu.OtherMenu
import com.android.frontend.view.menu.ProfileMenu
import com.android.frontend.view.menu.SecurityMenu
import com.android.frontend.view.page.AllProductsPage
import com.android.frontend.view.page.ForgetPasswordPage
import com.android.frontend.view.page.SignupPage
import com.android.frontend.view.screen.AuthenticationScreen
import com.android.frontend.view_models.ProductViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Graph.start) {
        authenticationGraph(navController)
        mainGraph(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.authenticationGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.AuthenticationScreen.route,
        route = Graph.start
    ) {
        composable(Screen.AuthenticationScreen.route) { AuthenticationScreen(navController) }
        composable(Navigation.SignupPage.route) {
            SignupPage(navController)
        }
        composable(Navigation.LoginPage.route) {
            LoginPage(navController)
        }
        composable(Navigation.ForgetPasswordPage.route) {
            ForgetPasswordPage(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    navigation(
        startDestination = Navigation.HomePage.route,
        route = Graph.main
    ) {
        composable(Navigation.HomePage.route) {
            HomePage(navController)
        }
        composable(Navigation.ProfileMenu.route) {
            ProfileMenu()
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

@Composable
fun NavProduct(navController: NavHostController, productid: Int) {
    NavHost(navController = navController, startDestination = Navigation.ProductDetailsPage.route) {
        composable(Navigation.ProductDetailsPage.route) {
            ProductDetailsPage(productViewModel = ProductViewModel(), productId = productid)
        }
    }
}

object Graph {
    const val start = "start"
    const val main = "main"
}
