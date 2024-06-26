package com.android.frontend.navigation

sealed class Screen(val route: String) {
    object AuthenticationScreen : Screen(route = "authentication_screen")
    object MainScreen : Screen(route = "main_screen")
}

sealed class Navigation(val route: String) {
    object SignupPage : Navigation(route = "signup_page")
    object LoginPage : Navigation(route = "login_page")
    object ForgetPasswordPage : Navigation(route = "forgetPassword_page")
    object HomePage : Navigation(route = "home_page")
    object CartPage : Navigation(route = "cart_page")
    object AccountPage : Navigation(route = "account_page")
    object AboutPage : Navigation(route = "about_page")
    object OtherMenu : Navigation(route = "other_menu")
    object ProfileMenu : Navigation(route = "profile_menu")
    object AccountMenu : Navigation(route = "account_menu")
    object SecurityMenu : Navigation(route = "security_menu")
    object ChangePasswordPage : Navigation(route = "changePassword_page")
    object AllProductsPage : Navigation(route = "products_page")
    object ProductDetailsPage : Navigation(route = "productDetails_page")
}