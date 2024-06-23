package com.example.frontend.view

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.frontend.R
import com.example.frontend.model.CurrentDataUtils
import com.example.frontend.navigation.Navigation
import com.example.frontend.navigation.Screen

@Composable
fun ProfileMenuPage(navController: NavHostController) {
    val userName = "Nome Utente"
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Ciao, $userName",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        ProfileMenuItem(navController, R.drawable.profile_icon, R.string.il_mio_profilo)
        ProfileMenuItem(navController, R.drawable.orders_icon, R.string.i_miei_ordini)
        ProfileMenuItem(navController, R.drawable.wishlist_icon, R.string.lista_desideri)
        ProfileMenuItem(navController, R.drawable.paymentmethod_icon, R.string.metodi_di_pagamento)
        ProfileMenuItem(navController, R.drawable.address_icon, R.string.indirizzi_di_spedizione)
        ProfileMenuItem(navController, R.drawable.logout_icon, R.string.logout, true)
    }
}

@Composable
fun ProfileMenuItem(navController: NavHostController, iconResId: Int, textResId: Int, isLogout: Boolean = false) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                if (isLogout) {
                    CurrentDataUtils.logout()
                    val packageManager: PackageManager = context.packageManager
                    val intent: Intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
                    val componentName: ComponentName = intent.component!!
                    val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
                    context.startActivity(restartIntent)
                    Runtime.getRuntime().exit(0)
                } else {
                    when (textResId) {
                        R.string.il_mio_profilo -> navController.navigate(Navigation.ProfilePage.route)
                        // Add other navigation logic here
                    }
                }
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = stringResource(id = textResId),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = textResId),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
