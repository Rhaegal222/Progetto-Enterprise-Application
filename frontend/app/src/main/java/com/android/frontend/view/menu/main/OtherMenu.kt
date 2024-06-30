package com.android.frontend.view.menu.main

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.navigation.Navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.android.frontend.MainActivity
import com.android.frontend.config.TokenManager

@Composable
fun OtherMenu(navController: NavHostController) {

    Column(modifier = Modifier
        .fillMaxSize()
    ) {
        MenuItem(navController, Icons.Default.ManageAccounts, R.string.account)
        MenuItem(navController, Icons.Default.Settings, R.string.app_settings)
        MenuItem(navController, Icons.Default.Info, R.string.about)
        MenuItem(navController, Icons.AutoMirrored.Filled.Logout, R.string.logout)
        MenuItem(navController, Icons.Default.BugReport, R.string.debug)
    }
}

@Composable
fun MenuItem(navController: NavHostController, icon: ImageVector, textResId: Int) {

    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                when (textResId) {
                    R.string.account -> navController.navigate(Navigation.AccountMenu.route)
                    R.string.about -> navController.navigate(Navigation.AboutPage.route)
                    R.string.app_settings -> navController.navigate(Navigation.SettingsMenu.route)
                    R.string.logout -> {
                        TokenManager.getInstance().clearTokens(context)
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                    R.string.debug -> navController.navigate(Navigation.DebugMenu.route)
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

