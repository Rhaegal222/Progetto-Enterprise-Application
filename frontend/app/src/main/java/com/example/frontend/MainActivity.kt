package com.example.frontend

import com.example.frontend.view.LoginScreen
import com.example.frontend.view.WelcomeScreenContent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend.navigation.Screen
import com.example.frontend.ui.theme.FrontendTheme
import com.example.frontend.view.HomeScreen
import com.example.frontend.view.ProfileScreen
import com.example.frontend.view.SignUpScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrontendTheme {
                val navController = rememberNavController()
                SetupNavGraph(navController = navController)
            }
        }
    }
}

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.WelcomeScreen.route) {
        composable(Screen.WelcomeScreen.route) {
            WelcomeScreenContent(navController)
        }
        composable(Screen.LoginScreen.route) {
            LoginScreen(navController)
        }
        composable(Screen.SignUpScreen.route) {
            SignUpScreen(navController)
        }
        composable(Screen.HomeScreen.route) {
            HomeScreen(navController)
        }
        composable(Screen.ProfileScreen.route){
            ProfileScreen(navController)
        }

    }
}