package com.example.frontend.view.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.frontend.R
import com.example.frontend.navigation.MainPageGraph
import com.example.frontend.navigation.Navigation
import com.example.frontend.view.components.TopBar

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        topBar = { AllTopBar(navController)},
        content = { paddingValues ->
            Box(modifier = Modifier
                .padding(paddingValues)
                .padding(top = 0.dp) // Adjust the top padding to the height of the TopBar
            ) {
                MainPageGraph(navController = navController)
                BackHandler { navController.popBackStack() }
            }
        },
        bottomBar = {
            MainBottomBar(navController = navController)
        }
    )
}

@Composable
fun AllTopBar(navController: NavHostController) {

    val currentRoute = navController
        .currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)

    TopBar(navController)
}
@Composable
fun MainBottomBar(navController: NavHostController) {
    val currentNavigation = navController
        .currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)

    BottomAppBar {
        NavigationBar {

            NavigationBarItem(
                selected = currentNavigation.value?.destination?.route == Navigation.HomePage.route,
                onClick = { navController.navigate(Navigation.HomePage.route) },
                icon = {
                    Icon(Icons.Default.Home, modifier = Modifier.size(30.dp), contentDescription = stringResource(id = R.string.home))
                }
            )

            NavigationBarItem(
                selected = currentNavigation.value?.destination?.route == Navigation.ProfileMenuPage.route,
                onClick = { navController.navigate(Navigation.ProfileMenuPage.route) },
                icon = {
                    Icon(Icons.Default.Person, modifier = Modifier.size(30.dp), contentDescription = stringResource(id = R.string.profile))
                }
            )

            NavigationBarItem(
                selected = currentNavigation.value?.destination?.route == Navigation.CartPage.route,
                onClick = { navController.navigate(Navigation.CartPage.route) },
                icon = {
                    Icon(Icons.Default.ShoppingCart, modifier = Modifier.size(30.dp), contentDescription = stringResource(id = R.string.cart))
                }
            )

            NavigationBarItem(
                selected = currentNavigation.value?.destination?.route == Navigation.MenuPage.route,
                onClick = { navController.navigate(Navigation.MenuPage.route) },
                icon = {
                    Icon(Icons.Default.Menu, modifier = Modifier.size(30.dp), contentDescription = stringResource(id = R.string.menu))
                }
            )
        }
    }
}
