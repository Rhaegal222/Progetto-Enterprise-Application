package com.android.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.android.frontend.ui.ChangePasswordPage
import com.android.frontend.view.menu.*
import com.android.frontend.view.page.*
import com.android.frontend.view.page.authentication.*
import com.android.frontend.view.screen.AuthenticationScreen
import com.android.frontend.view.screen.MainScreen
import com.android.frontend.view_models.ProductViewModel
import com.example.frontend.view.AddPaymentPage
import com.example.frontend.view.PaymentsPage

object Graph {
    const val start = "authentication"
    const val main = "main"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Graph.main) { // Start with main graph instead of authentication
        authenticationGraph(navController)
        mainGraph(navController)
    }
}

fun NavGraphBuilder.authenticationGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.AuthenticationScreen.route,
        route = Graph.start
    ) {
        composable(Screen.AuthenticationScreen.route) { AuthenticationScreen(navController) }
        composable(Navigation.WelcomePage.route) { WelcomePage(navController) }
        composable(Navigation.SignupPage.route) { SignupPage(navController) }
        composable(Navigation.LoginPage.route) { LoginPage(navController) }
        composable(Navigation.ForgetPasswordPage.route) { ForgetPasswordPage(navController) }
    }
}

fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    navigation(
        startDestination = Screen.MainScreen.route,
        route = Graph.main
    ) {
        composable(Screen.MainScreen.route) { MainScreen(navController) }
        composable(Navigation.HomePage.route) { HomePage(navController) }
        composable(Navigation.CartPage.route) { CartPage() }
        composable(Navigation.AccountPage.route) { AccountPage(navController) }
        composable(Navigation.AboutPage.route) { AboutPage(navController) }
        composable(Navigation.OtherMenu.route) { OtherMenu(navController) }
        composable(Navigation.ProfileMenu.route) { ProfileMenu() }
        composable(Navigation.AccountMenu.route) { AccountMenu(navController) }
        composable(Navigation.SecurityMenu.route) { SecurityMenu(navController) }
        composable(Navigation.ChangePasswordPage.route) { ChangePasswordPage(navController) }
        composable(Navigation.AllProductsPage.route) { AllProductsPage(navController, ProductViewModel()) }
        composable(Navigation.ProductDetailsPage.route) { ProductDetailsPage(ProductViewModel(), String()) }
        // composable(Navigation.SearchPage.route) { SearchPage(navController) }
        // composable(Navigation.CheckoutPage.route) { CheckoutPage(navController) }
        // composable(Navigation.OrderSuccessPage.route) { OrderSuccessPage(navController) }
        // composable(Navigation.OrderHistoryPage.route) { OrderHistoryPage(navController) }
        composable(Navigation.PaymentsPage.route) {
            PaymentsPage(navController)
        }
        composable(Navigation.AddPaymentPage.route) {
            AddPaymentPage(navController)
        }
    }
}
