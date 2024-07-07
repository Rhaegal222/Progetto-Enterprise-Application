package com.android.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.frontend.view.pages.user.authentication.*

@Composable
fun AuthGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Navigation.WelcomePage.route) {
        composable(Navigation.WelcomePage.route) { WelcomePage(navController) }
        composable(Navigation.SignupPage.route) { SignupPage(navController) }
        composable(Navigation.LoginPage.route) { LoginPage(navController) }
        composable(Navigation.ForgetPasswordPage.route) { ForgetPasswordPage(navController) }
    }
}
