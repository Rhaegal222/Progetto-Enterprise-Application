package com.android.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun AppRouter(navController: NavHostController = rememberNavController()) {
    RootGraph(navController = navController)
}

sealed class Screen(val route: String) {
    object RootScreen : Screen(route = Graph.ROOT)
    object AuthenticationScreen : Screen(route = Graph.AUTHENTICATION)
    object MainScreen : Screen(route = Graph.MAIN)
    object AdminScreen : Screen(route = Graph.ADMIN)
}

sealed class Navigation(val route: String) {
    object WelcomePage : Navigation(route = "welcome_page")
    object SignupPage : Navigation(route = "signup_page")
    object LoginPage : Navigation(route = "login_page")
    object ForgetPasswordPage : Navigation(route = "forget_password_page")
    object HomePage : Navigation(route = "home_page")
    object CartPage : Navigation(route = "cart_page")
    object AccountPage : Navigation(route = "account_page")
    object AboutPage : Navigation(route = "about_page")
    object OtherMenu : Navigation(route = "other_menu")
    object ProfileMenu : Navigation(route = "profile_menu")
    object AccountMenu : Navigation(route = "account_menu")
    object SecurityMenu : Navigation(route = "security_menu")
    object ChangePasswordPage : Navigation(route = "change_password_page")
    object ProductsPage : Navigation(route = "products_page")
    object ProductDetailsPage : Navigation(route = "product_details")
    object PaymentsPage : Navigation(route = "payments_page")
    object AddPaymentPage : Navigation(route = "addpayment_page")
    object SettingsMenu : Navigation(route = "settings_menu")
    object ThemePage : Navigation(route = "theme_page")
    object CountryLanguagePage : Navigation(route = "country_language_page")
    object AddressesPage : Navigation(route = "shippingaddress_page")
    object AddAddressPage : Navigation(route = "addaddress_page")
    object DebugMenu : Navigation(route = "debug_menu")
    object LoadingPage : Navigation(route = "loading_page")
    object SalesProductsPage : Navigation(route = "salesProducts")
    object SaleProductDetailsPage : Navigation(route = "sale_product_details")

    object AdminMenu : Navigation(route = "admin_menu")
    object AddProductPage : Navigation(route = "add_product_page")
    object EditProductPage : Navigation(route = "edit_product_page")
    object AddBrandPage : Navigation(route = "add_brand_page")
    object AddCategoryPage : Navigation(route = "add_category_page")
    object CategoryPage : Navigation(route = "category_page")
    object BrandPage : Navigation(route = "brand_page")
    object ProductPage : Navigation(route = "product_page")
    object UserPage : Navigation(route = "user_page")
    object CheckoutPage : Navigation(route = "checkout_page")
    object WishlistUpdatePage : Navigation(route = "wishlistUpdate_page")
    object WishlistsPage : Navigation(route = "wishlists")
    object AddWishlistPage : Navigation(route = "addWishlist")
    object WishlistDetailsPage : Navigation(route = "wishlistDetails")
    object OrdersPage: Navigation(route = "order_page")
}
