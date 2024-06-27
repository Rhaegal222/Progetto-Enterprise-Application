package com.android.frontend.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.frontend.ui.ChangePasswordPage
import com.android.frontend.view.menu.*
import com.android.frontend.view.page.*
import com.android.frontend.view_models.ProductViewModel
import com.android.frontend.view.page.profile.AddPaymentPage
import com.android.frontend.view.page.other.AboutPage
import com.android.frontend.view.page.other.AccountPage
import com.android.frontend.view.page.product.AllProductsPage
import com.android.frontend.view.page.product.CartPage
import com.android.frontend.view.page.product.ProductDetailsPage
import com.android.frontend.view.page.profile.PaymentsPage

@Composable
fun MainGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Navigation.HomePage.route, modifier = modifier) {
        composable(Navigation.HomePage.route) { HomePage(navController) }
        composable(Navigation.CartPage.route) { CartPage() }
        composable(Navigation.AccountPage.route) { AccountPage(navController) }
        composable(Navigation.AboutPage.route) { AboutPage(navController) }
        composable(Navigation.OtherMenu.route) { OtherMenu(navController) }
        composable(Navigation.ProfileMenu.route) { ProfileMenu(navController) }
        composable(Navigation.AccountMenu.route) { AccountMenu(navController) }
        composable(Navigation.SecurityMenu.route) { SecurityMenu(navController) }
        composable(Navigation.ChangePasswordPage.route) { ChangePasswordPage(navController) }
        composable(Navigation.AllProductsPage.route) { AllProductsPage(navController, ProductViewModel()) }
        composable(Navigation.ProductDetailsPage.route) { ProductDetailsPage(ProductViewModel(), "") }
        composable(Navigation.PaymentsPage.route) { PaymentsPage(navController) }
        composable(Navigation.AddPaymentPage.route) { AddPaymentPage(navController) }
    }
}
