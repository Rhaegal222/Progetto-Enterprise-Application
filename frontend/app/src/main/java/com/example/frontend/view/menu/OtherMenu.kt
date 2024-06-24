package com.example.frontend.view.menu

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
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
import com.example.frontend.R
import com.example.frontend.navigation.Navigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.example.frontend.model.CurrentDataUtils

@Composable
fun OtherMenu(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        MenuItem(navController, Icons.Default.ManageAccounts, R.string.account, false)
        MenuItem(navController, Icons.Default.Info, R.string.about, false)
        MenuItem(navController, Icons.Default.Logout, R.string.logout, true)
    }
}

@Composable
fun MenuItem(navController: NavHostController, icon: ImageVector, textResId: Int, isLogout: Boolean = false) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                if (isLogout) {
                    CurrentDataUtils.logout()
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
                        R.string.account -> navController.navigate(Navigation.AccountPage.route)
                        R.string.about -> navController.navigate(Navigation.AboutPage.route)
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
