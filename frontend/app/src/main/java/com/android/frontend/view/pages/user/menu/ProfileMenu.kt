package com.android.frontend.view.pages.user.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.frontend.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.android.frontend.persistence.SecurePreferences
import com.android.frontend.navigation.Navigation

@Composable
fun ProfileMenu(navController: NavController) {

    val username = SecurePreferences.getUser(LocalContext.current)?.firstName ?: ""

    Column(modifier = Modifier
        .fillMaxSize()) {
        Text(
            text = "${stringResource(id = R.string.hello_user)} $username!",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        ProfileMenuItem(navController, Icons.Default.LocalShipping, R.string.my_orders)
        ProfileMenuItem(navController, Icons.Default.Favorite, R.string.wishlist)
        ProfileMenuItem(navController, Icons.Default.Payment, R.string.payment_methods)
        ProfileMenuItem(navController, Icons.Default.LocationOn, R.string.shipping_addresses)
    }
}

@Composable
fun ProfileMenuItem(navController: NavController, icon: ImageVector, textResId: Int) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                when (textResId) {
                    R.string.my_orders -> navController.navigate(Navigation.OrdersPage.route)
                     R.string.wishlist ->navController.navigate(Navigation.WishlistsPage.route)
                    R.string.payment_methods -> navController.navigate(Navigation.PaymentsPage.route)
                    R.string.shipping_addresses -> navController.navigate(Navigation.AddressesPage.route)
                }
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = textResId),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
