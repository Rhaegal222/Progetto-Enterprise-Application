package com.android.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.frontend.ui.ChangePasswordPage
import com.android.frontend.view.menu.*
import com.android.frontend.view.page.*
import com.android.frontend.view_models.ProductViewModel

@Composable
fun MainGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Navigation.HomePage.route) {
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
        composable(Navigation.ProductDetailsPage.route) { ProductDetailsPage(ProductViewModel(), "") }
    }
}
