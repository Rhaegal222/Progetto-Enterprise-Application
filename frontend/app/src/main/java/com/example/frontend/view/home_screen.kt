package com.example.frontend.view

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.frontend.view.components.TopBar
import com.example.frontend.view_models.HomeViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel: HomeViewModel = viewModel()
    val searchQuery = viewModel.searchQuery.collectAsState().value

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                onMenuClick = {
                    // Gestito in TopBar
                },
                onProfileClick = {
                    // Gestito in TopBar
                },
                onCartClick = {
                    // Gestito in TopBar
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(top = 35.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    label = { Text("Search") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Restante contenuto
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Home Screen Content")
            }
        }
    )
}
