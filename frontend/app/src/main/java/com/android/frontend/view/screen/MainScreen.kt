package com.android.frontend.view.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.android.frontend.navigation.MainGraph
import com.android.frontend.view.component.BottomBar
import com.android.frontend.view_models.CartViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val mainNavController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()

    Scaffold(
        bottomBar = {
            BottomBar(navController = mainNavController, cartViewModel = cartViewModel)
        }
    ) { innerPadding ->
        MainGraph(navController = mainNavController, cartViewModel = cartViewModel, modifier = Modifier.padding(innerPadding))
    }
}
