package com.android.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.frontend.view.pages.browse.ChangePasswordPage
import com.android.frontend.view.pages.menu.AccountMenu
import com.android.frontend.view.pages.menu.OtherMenu
import com.android.frontend.view.pages.menu.ProfileMenu
import com.android.frontend.view.pages.menu.DebugMenu
import com.android.frontend.view.pages.menu.SecurityMenu
import com.android.frontend.view.pages.menu.SettingsMenu
import com.android.frontend.view.pages.main.HomePage
import com.android.frontend.view_models.user.ProductViewModel
import com.android.frontend.view.pages.add.AddPaymentPage
import com.android.frontend.view.pages.browse.AboutPage
import com.android.frontend.view.pages.browse.PersonalInformationPage
import com.android.frontend.view.pages.browse.ThemePage
import com.android.frontend.view.pages.browse.CountryLanguagePage
import com.android.frontend.view.pages.browse.AllProductsPage
import com.android.frontend.view.pages.main.CartPage
import com.android.frontend.view.pages.browse.CheckoutPage
import com.android.frontend.view.pages.details.ProductDetailsPage
import com.android.frontend.view.pages.details.SaleProductDetailsPage
import com.android.frontend.view.pages.main.SalesProductsPage
import com.android.frontend.view.pages.add.AddAddressPage
import com.android.frontend.view.pages.profile.AddWishlistPage
import com.android.frontend.view.pages.browse.PaymentMethodsPage
import com.android.frontend.view.pages.browse.ShippingAddressesPage
import com.android.frontend.view.pages.browse.AllWishlistsPage
import com.android.frontend.view_models.user.CartViewModel
import com.android.frontend.view_models.user.WishlistViewModel

@Composable
fun MainGraph(navController: NavHostController, cartViewModel: CartViewModel, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Navigation.HomePage.route, modifier = modifier) {
        composable(Navigation.HomePage.route) { HomePage(navController) }
        composable(Navigation.CartPage.route) { CartPage(cartViewModel = cartViewModel, navController = navController) }
        composable(Navigation.AccountPage.route) { PersonalInformationPage(navController) }
        composable(Navigation.AboutPage.route) { AboutPage(navController) }
        composable(Navigation.OtherMenu.route) { OtherMenu(navController) }
        composable(Navigation.ProfileMenu.route) { ProfileMenu(navController) }
        composable(Navigation.AccountMenu.route) { AccountMenu(navController) }
        composable(Navigation.SecurityMenu.route) { SecurityMenu(navController) }
        composable(Navigation.ChangePasswordPage.route) { ChangePasswordPage(navController) }
        composable(Navigation.AllProductsPage.route) { AllProductsPage(navController, ProductViewModel(), cartViewModel = cartViewModel) }
        composable(Navigation.ProductDetailsPage.route) { ProductDetailsPage(ProductViewModel(), cartViewModel = cartViewModel, navController) }
        composable(Navigation.PaymentsPage.route) { PaymentMethodsPage(navController) }
        composable(Navigation.AddPaymentPage.route) { AddPaymentPage(navController) }
        composable(Navigation.SettingsMenu.route) { SettingsMenu(navController) }
        composable(Navigation.ThemePage.route) { ThemePage(navController) }
        composable(Navigation.CountryLanguagePage.route) { CountryLanguagePage(navController) }
        composable(Navigation.ShippingAddressesPage.route) { ShippingAddressesPage(navController) }
        composable(Navigation.AddAddressPage.route) { AddAddressPage(navController) }
        composable(Navigation.DebugMenu.route) { DebugMenu(navController) }
        composable(Navigation.SalesProductsPage.route) { SalesProductsPage(navController, ProductViewModel(), cartViewModel = cartViewModel) }
        composable(Navigation.SaleProductDetailsPage.route) { SaleProductDetailsPage(ProductViewModel(), cartViewModel = cartViewModel) }
        composable(Navigation.AllWishlistsPage.route) { AllWishlistsPage(navController, WishlistViewModel()) }
        composable(Navigation.AddWishlistPage.route) { AddWishlistPage(navController, WishlistViewModel()) }
        composable(Navigation.CheckoutPage.route) { CheckoutPage(cartViewModel = cartViewModel, navController = navController) }
    }
}
