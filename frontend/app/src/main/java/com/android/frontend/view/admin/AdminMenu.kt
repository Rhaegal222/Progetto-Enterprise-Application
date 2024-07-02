package com.android.frontend.view.admin

import android.content.Intent
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Addchart
import androidx.compose.material.icons.filled.BrandingWatermark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.Logout
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.android.frontend.MainActivity
import com.android.frontend.R
import com.android.frontend.config.TokenManager
import com.android.frontend.navigation.Navigation
import com.android.frontend.persistence.SecurePreferences

@Composable
fun AdminMenu(navController: NavController) {
    val username = SecurePreferences.getUser(LocalContext.current)?.firstName ?: ""

    Column(modifier = Modifier
        .fillMaxSize()) {
        Text(
            text = "ADMIN: $username!",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        AdminMenuItem(navController, Icons.Default.AccountBox, R.string.users)
        AdminMenuItem(navController, Icons.Default.LocalGroceryStore, R.string.products)
        AdminMenuItem(navController, Icons.Default.Category , R.string.categories )
        AdminMenuItem(navController, Icons.Default.BrandingWatermark , R.string.brands )
        AdminMenuItem(navController, Icons.Default.Logout, R.string.logout)
    }
}

@Composable
fun AdminMenuItem(navController: NavController, icon: ImageVector, textResId: Int, isLogout: Boolean = false) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                when (textResId) {
                    R.string.logout -> {
                        TokenManager
                            .getInstance()
                            .clearTokens(context)
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                    R.string.users -> navController.navigate(Navigation.UserPage.route) // Correct route name
                    R.string.products -> navController.navigate(Navigation.ProductPage.route) // Correct route name
                    R.string.categories -> navController.navigate(Navigation.CategoryPage.route) // Correct route name
                    R.string.brands -> navController.navigate(Navigation.BrandPage.route) // Correct route name
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
