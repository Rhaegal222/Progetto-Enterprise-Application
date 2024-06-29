package com.android.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.android.frontend.view.page.other.ThemePage
import com.android.frontend.view.page.other.CountryLanguagePage
import com.android.frontend.view.page.product.AllProductsPage
import com.android.frontend.view.page.product.CartPage
import com.android.frontend.view.page.product.ProductDetailsPage
import com.android.frontend.view.page.profile.AddAddressPage
import com.android.frontend.view.page.profile.PaymentMethodsPage
import com.android.frontend.view.page.profile.ShippingAddressesPage

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
        composable(Navigation.ProductDetailsPage.route) { ProductDetailsPage(ProductViewModel()) }
        composable(Navigation.PaymentsPage.route) { PaymentMethodsPage(navController) }
        composable(Navigation.AddPaymentPage.route) { AddPaymentPage(navController) }
        composable(Navigation.SettingsMenu.route) { SettingsMenu(navController) }
        composable(Navigation.ThemePage.route) { ThemePage(navController) }
        composable(Navigation.CountryLanguagePage.route) { CountryLanguagePage(navController) }
        composable(Navigation.ShippingAddressesPage.route) { ShippingAddressesPage(navController)  }
        composable(Navigation.AddAddressPage.route) { AddAddressPage(navController)  }
        composable(Navigation.DebugMenu.route) { DebugMenu(navController) }
    }
}
