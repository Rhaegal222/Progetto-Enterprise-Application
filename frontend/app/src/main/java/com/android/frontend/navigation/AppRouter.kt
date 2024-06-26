package com.android.frontend.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen(val route: String) {
    object WelcomeScreen : Screen(route = "welcome_screen")
    object SignUpScreen : Screen(route = "signup_screen")
    object LoginScreen : Screen(route = "login_screen")
    object ForgetPassword: Screen(route = "forgetPassword_screen")
    object MainScreen : Screen(route = "main_screen")
}

sealed class Navigation(val route: String) {
    object SignUpPage : Navigation(route = "signup_page")
    object LoginPage : Navigation(route = "login_page")
    object HomePage : Navigation(route = "home_page")
    object CartPage : Navigation(route = "cart_page")
    object AccountPage : Navigation(route = "account_page")
    object AboutPage : Navigation(route = "about_page")
    object OtherMenu : Navigation(route = "other_menu")
    object ProfileMenu : Navigation(route = "profile_menu")
    object AccountMenu : Navigation(route = "account_menu")
    object SecurityMenu : Navigation(route = "security_menu")
    object ChangePasswordPage : Navigation(route = "changePassword_page")


}

object AppRouter{
    val currentScreen: MutableState<Screen> = mutableStateOf(Screen.WelcomeScreen)
    fun navigateTo(destination: Screen){
        currentScreen.value = destination
    }
}

object MainRouter{
    val currentPage : MutableState<Navigation> = mutableStateOf(Navigation.HomePage)
    fun changePage(nextPage: Navigation){
        currentPage.value = nextPage
    }
}