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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.frontend.R
import com.example.frontend.navigation.Navigation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController) {
    val currentPage =
        navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)
    val route = currentPage.value?.destination?.route

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            horizontalAlignment = Alignment.Start
        ) {
            TopAppBar(
                title = {
                    when {
                        route != null && route.contains(Navigation.HomePage.route) -> {
                            Text(
                                text = stringResource(id = R.string.app_name),
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )
                        }

                        route != null && route.contains(Navigation.ProfilePage.route) -> {
                            Text(
                                text = stringResource(id = R.string.profile),
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )
                        }

                        route != null && route.contains(Navigation.CartPage.route) -> {
                            Text(
                                text = stringResource(id = R.string.cart),
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )
                        }

                        route != null && route.contains(Navigation.MenuPage.route) -> {
                            Text(
                                text = stringResource(id = R.string.menu),
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )
                        }

                        route != null && route.contains(Navigation.ProfileMenuPage.route) -> {
                            Text(
                                text = stringResource(id = R.string.menuProfile),
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )
                        }

                        route != null && route.contains(Navigation.AboutPage.route) -> {
                            Text(
                                text = stringResource(id = R.string.about),
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )
                        }

                        route != null && route.contains(Navigation.ProfilePage.route) -> {
                            Text(
                                text = stringResource(id = R.string.profile),
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center
                            )
                        }

                        else -> Text(
                            text = stringResource(id = R.string.app_name),
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    if (route != null && !route.contains(Navigation.HomePage.route)) {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                painterResource(id = R.drawable.arrowleft_icon),
                                contentDescription = stringResource(id = R.string.back),
                                modifier = Modifier.height(20.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}
