package com.android.frontend.view.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ManageSearch
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.navigation.Navigation
import com.android.frontend.view_models.user.CartViewModel

@Composable
fun BottomBar(navController: NavHostController, cartViewModel: CartViewModel) {
    val context = LocalContext.current
    val currentNavigation = navController
        .currentBackStackEntryFlow
        .collectAsState(initial = navController.currentBackStackEntry)

    val cartItemCount by cartViewModel.cartItemCount.observeAsState(0)

    LaunchedEffect(Unit) {
        cartViewModel.getCartForLoggedUser(context)
    }

    BottomAppBar(
        modifier = Modifier.fillMaxWidth().height(60.dp)
    ) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth().height(60.dp),
        ) {
            NavigationBarItem(
                selected = currentNavigation.value?.destination?.route == Navigation.HomePage.route,
                onClick = { navController.navigate(Navigation.HomePage.route) },
                icon = {
                    Icon(Icons.Default.Home, modifier = Modifier.size(30.dp), contentDescription = stringResource(id = R.string.home))
                }
            )

            NavigationBarItem(
                selected = currentNavigation.value?.destination?.route == Navigation.ProductsPage.route,
                onClick = { navController.navigate(Navigation.ProductsPage.route) },
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
                    Box {
                        Icon(Icons.Default.ShoppingCart, modifier = Modifier.size(30.dp), contentDescription = stringResource(id = R.string.cart))
                        Badge(
                            content = { Text(cartItemCount.toString(), fontSize = 10.sp) },
                            modifier = Modifier.align(Alignment.TopEnd).offset(4.dp, (-4).dp)
                        )
                    }
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