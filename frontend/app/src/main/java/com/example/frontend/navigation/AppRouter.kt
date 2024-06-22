package com.example.frontend.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

sealed class Screen(val route: String) {
    object SignUpScreen : Screen(route = "signup_screen")
    object LoginScreen : Screen(route = "login_screen")
    object WelcomeScreen : Screen(route = "start_screen")
    object HomeScreen : Screen(route = "home_screen")
}

sealed class Navigation(val route: String) {
    object HomePage : Navigation(route = "home_page")
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