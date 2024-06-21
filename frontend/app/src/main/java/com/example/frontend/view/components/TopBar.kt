package com.example.frontend.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.frontend.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavHostController,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val customColor = Color(android.graphics.Color.parseColor("#73813C"))
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                // Contenuto del navigation drawer
                DrawerMenuItem(
                    icon = painterResource(id = R.drawable.home_icon),
                    label = "Home",
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navController.navigate("home_screen")
                    }
                )
                DrawerMenuItem(
                    icon = painterResource(id = R.drawable.products_icon),
                    label = "Prodotti",
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navController.navigate("products_screen")
                    }
                )
                DrawerMenuItem(
                    icon = painterResource(id = R.drawable.about_icon),
                    label = "Chi Siamo",
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navController.navigate("about_screen")
                    }
                )
            }
        },
        drawerState = drawerState,
    ) {
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier
                            .scale(2.0F)
                            .padding(start = 25.dp)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                }) {
                    Icon(painter = painterResource(id = R.drawable.menu_icon), contentDescription = null, tint = Color.Black)
                }
            },
            actions = {
                IconButton(onClick = onProfileClick) {
                    Icon(painter = painterResource(id = R.drawable.profile_icon), contentDescription = null, tint = Color.Black)
                }
                IconButton(onClick = onCartClick) {
                    Icon(painter = painterResource(id = R.drawable.cart_icon), contentDescription = null, tint = Color.Black)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = customColor
            ),
            modifier = Modifier.height(50.dp)
        )
    }
}

@Composable
fun DrawerMenuItem(icon: Painter, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = Color.Black, // stessa tint delle icone nel TopBar
            modifier = Modifier.size(40.dp) // stessa dimensione delle icone nel TopBar
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}
