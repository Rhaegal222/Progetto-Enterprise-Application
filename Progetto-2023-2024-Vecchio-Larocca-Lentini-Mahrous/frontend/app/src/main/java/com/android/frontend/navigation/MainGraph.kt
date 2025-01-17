package com.android.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.android.frontend.view.pages.user.main.SalesProductsPage
import com.android.frontend.view.pages.user.add.AddAddressPage
import com.android.frontend.view.pages.user.add.AddWishlistPage
import com.android.frontend.view.pages.user.browse.AddressesPage
import com.android.frontend.view.pages.user.browse.OrdersPage
import com.android.frontend.view.pages.user.browse.PaymentMethodsPage
import com.android.frontend.view.pages.user.browse.ProductsPage
import com.android.frontend.view.pages.user.browse.WishlistsPage
import com.android.frontend.view.pages.user.details.WishlistDetailsPage
import com.android.frontend.view.pages.user.details.WishlistUpdatePage
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
        composable(Navigation.ProductsPage.route) { ProductsPage(navController, cartViewModel = cartViewModel) }

        composable("${Navigation.ProductDetailsPage}/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            if (productId != null) {
                ProductDetailsPage(navController, cartViewModel = cartViewModel, productId)
            }
        }

        composable("${Navigation.WishlistDetailsPage}/{wishlistId}") { backStackEntry ->
            val wishlistId = backStackEntry.arguments?.getString("wishlistId")
            if (wishlistId != null) {
                WishlistDetailsPage(navController, wishlistId)
            }
        }

        composable(Navigation.PaymentsPage.route) { PaymentMethodsPage(navController) }
        composable(Navigation.AddPaymentPage.route) { AddPaymentPage(navController) }
        composable(Navigation.SettingsMenu.route) { SettingsMenu(navController) }
        composable(Navigation.ThemePage.route) { ThemePage(navController) }
        composable(Navigation.CountryLanguagePage.route) { CountryLanguagePage(navController) }
        composable(Navigation.AddressesPage.route) { AddressesPage(navController) }
        composable(Navigation.AddAddressPage.route) { AddAddressPage(navController) }
        composable(Navigation.DebugMenu.route) { DebugMenu(navController) }
        composable(Navigation.SalesProductsPage.route) { SalesProductsPage(navController, cartViewModel = cartViewModel) }
        composable(Navigation.WishlistsPage.route) { WishlistsPage(navController, WishlistViewModel()) }
        composable(Navigation.AddWishlistPage.route) { AddWishlistPage(navController, WishlistViewModel()) }
        composable(Navigation.CheckoutPage.route) { CheckoutPage(cartViewModel = cartViewModel, navController = navController) }
        composable(Navigation.OrdersPage.route) { OrdersPage(navController)}
        composable(Navigation.WishlistUpdatePage.route) { WishlistUpdatePage(wishlistViewModel = WishlistViewModel(), navController = navController)}
    }
}