package com.android.frontend.view.menu

import android.annotation.SuppressLint
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
import androidx.navigation.NavHostController
import com.android.frontend.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Password
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.android.frontend.navigation.Navigation

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SecurityMenu(navController: NavHostController) {
    val colors = MaterialTheme.colorScheme
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.security).uppercase(),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = colors.onBackground
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            SecurityItem(navController, Icons.Default.Password, R.string.change_password)
        }
    }
}

@Composable
fun SecurityItem(navController: NavHostController, icon: ImageVector, textResId: Int, isLogout: Boolean = false) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                when (textResId) {
                    R.string.change_password -> navController.navigate(Navigation.ChangePasswordPage.route)
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = colors.onBackground
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = textResId),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
