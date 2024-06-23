package com.example.frontend.view.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.frontend.R
import com.example.frontend.navigation.MainPageGraph
import com.example.frontend.navigation.Navigation
import com.example.frontend.view.components.TopBar

@OptIn(ExperimentalMaterial3Api::class)
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
                selected = currentNavigation.value?.destination?.route == Navigation.MenuPage.route,
                onClick = { navController.navigate(Navigation.MenuPage.route) },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.menu_icon),
                        modifier = Modifier.size(30.dp),
                        contentDescription = stringResource(id = R.string.menu)
                    )
                }
            )
            NavigationBarItem(
                selected = currentNavigation.value?.destination?.route == Navigation.HomePage.route,
                onClick = { navController.navigate(Navigation.HomePage.route) },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.home_icon),
                        modifier = Modifier.size(30.dp),
                        contentDescription = stringResource(id = R.string.home)
                    )
                }
            )
            NavigationBarItem(
                selected = currentNavigation.value?.destination?.route == Navigation.ProfileMenuPage.route,
                onClick = { navController.navigate(Navigation.ProfileMenuPage.route) },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.profile_icon),
                        modifier = Modifier.size(30.dp),
                        contentDescription = stringResource(id = R.string.profile))
                }
            )
            NavigationBarItem(
                selected = currentNavigation.value?.destination?.route == Navigation.CartPage.route,
                onClick = { navController.navigate(Navigation.CartPage.route) },
                icon = {
                    Icon(
                        painterResource(id = R.drawable.cart_icon),
                        modifier = Modifier.size(30.dp),
                        contentDescription = stringResource(id = R.string.cart))
                }
            )
        }
    }
}
