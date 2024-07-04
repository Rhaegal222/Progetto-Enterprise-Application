package com.android.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.frontend.view.pages.user.browse.ChangePasswordPage
import com.android.frontend.view.pages.user.menu.AccountMenu
import com.android.frontend.view.pages.user.menu.OtherMenu
import com.android.frontend.view.pages.user.menu.ProfileMenu
import com.android.frontend.view.pages.user.menu.DebugMenu
import com.android.frontend.view.pages.user.menu.SecurityMenu
import com.android.frontend.view.pages.user.menu.SettingsMenu
import com.android.frontend.view.pages.user.main.HomePage
import com.android.frontend.view.pages.user.add.AddPaymentPage
import com.android.frontend.view.pages.user.browse.AboutPage
import com.android.frontend.view.pages.user.browse.PersonalInformationPage
import com.android.frontend.view.pages.user.browse.ThemePage
import com.android.frontend.view.pages.user.browse.CountryLanguagePage
import com.android.frontend.view.pages.user.main.CartPage
import com.android.frontend.view.pages.user.browse.CheckoutPage
import com.android.frontend.view.pages.user.details.ProductDetailsPage
import com.android.frontend.view.pages.user.details.SaleProductDetailsPage
import com.android.frontend.view.pages.user.main.SalesProductsPage
import com.android.frontend.view.pages.user.add.AddAddressPage
import com.android.frontend.view.pages.profile.AddWishlistPage
import com.android.frontend.view.pages.user.browse.PaymentMethodsPage
import com.android.frontend.view.pages.user.browse.ShippingAddressesPage
import com.android.frontend.view.pages.user.browse.ProductsPage
import com.android.frontend.view.pages.user.browse.WishlistsPage
import com.android.frontend.view.pages.user.details.WishlistDetailsPage

@Composable
fun MainGraph(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Navigation.HomePage.route) {
        composable(Navigation.HomePage.route) { HomePage(navController) }
        composable(Navigation.CartPage.route) { CartPage(navController) }
        composable(Navigation.AccountPage.route) { PersonalInformationPage(navController) }
        composable(Navigation.AboutPage.route) { AboutPage(navController) }
        composable(Navigation.OtherMenu.route) { OtherMenu(navController) }
        composable(Navigation.ProfileMenu.route) { ProfileMenu(navController) }
        composable(Navigation.AccountMenu.route) { AccountMenu(navController) }
        composable(Navigation.SecurityMenu.route) { SecurityMenu(navController) }
        composable(Navigation.ChangePasswordPage.route) { ChangePasswordPage(navController) }
        composable(Navigation.AllProductsPage.route) { ProductsPage(navController) }
        composable(Navigation.ProductDetailsPage.route) { ProductDetailsPage(navController) }
        composable(Navigation.PaymentsPage.route) { PaymentMethodsPage(navController) }
        composable(Navigation.AddPaymentPage.route) { AddPaymentPage(navController) }
        composable(Navigation.SettingsMenu.route) { SettingsMenu(navController) }
        composable(Navigation.ThemePage.route) { ThemePage(navController) }
        composable(Navigation.CountryLanguagePage.route) { CountryLanguagePage(navController) }
        composable(Navigation.ShippingAddressesPage.route) { ShippingAddressesPage(navController) }
        composable(Navigation.AddAddressPage.route) { AddAddressPage(navController) }
        composable(Navigation.DebugMenu.route) { DebugMenu(navController) }
        composable(Navigation.SalesProductsPage.route) { SalesProductsPage(navController) }
        composable(Navigation.SaleProductDetailsPage.route) { SaleProductDetailsPage() }
        composable(Navigation.WishlistsPage.route) { WishlistsPage(navController) }
        composable(Navigation.AddWishlistPage.route) { AddWishlistPage(navController) }
        composable(Navigation.CheckoutPage.route) { CheckoutPage(navController) }
        composable(Navigation.WishlistDetailsPage.route) { WishlistDetailsPage(navController) }
    }
}
