package com.android.frontend.view.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.navigation.Navigation

@Composable
fun BottomBar(navController: NavHostController) {
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
                selected = currentNavigation.value?.destination?.route == Navigation.AllProductsPage.route,
                onClick = { navController.navigate(Navigation.AllProductsPage.route) },
                icon = {
                    Icon(Icons.Default.ManageSearch, modifier = Modifier.size(30.dp), contentDescription = stringResource(id = R.string.profile))
                }
            )

            NavigationBarItem(
                selected = currentNavigation.value?.destination?.route == Navigation.ProfileMenu.route,
                onClick = { navController.navigate(Navigation.ProfileMenu.route) },
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
                selected = currentNavigation.value?.destination?.route == Navigation.OtherMenu.route,
                onClick = { navController.navigate(Navigation.OtherMenu.route) },
                icon = {
                    Icon(Icons.Default.Menu, modifier = Modifier.size(30.dp), contentDescription = stringResource(id = R.string.menu))
                }
            )
        }
    }
}