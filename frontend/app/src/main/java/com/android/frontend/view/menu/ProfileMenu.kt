package com.android.frontend.view.menu

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
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
import com.android.frontend.model.SecurePreferences
import com.android.frontend.navigation.Navigation

@Composable
fun ProfileMenu(navController: NavController) {
    val userName = "Nome Utente"
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.hello_user, userName),
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
fun ProfileMenuItem(navController: NavController, icon: ImageVector, textResId: Int, isLogout: Boolean = false) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                if (isLogout) {
                    SecurePreferences.clearAll(context)
                    val packageManager: PackageManager = context.packageManager
                    val intent: Intent =
                        packageManager.getLaunchIntentForPackage(context.packageName)!!
                    val componentName: ComponentName = intent.component!!
                    val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
                    context.startActivity(restartIntent)
                    Runtime
                        .getRuntime()
                        .exit(0)
                } else {
                    when (textResId) {
                        // R.string.my_orders ->
                        // R.string.wishlist ->
                        R.string.payment_methods -> navController.navigate(Navigation.PaymentMethodsPage.route)
                        // R.string.shipping_addresses -> navController.navigate(Navigation.ShippingAddressesPage.route)
                    }
                }
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = stringResource(id = textResId), modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = textResId),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
