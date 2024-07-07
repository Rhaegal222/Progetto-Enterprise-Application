package com.android.frontend.view.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.android.frontend.navigation.MainGraph
import com.android.frontend.view.component.BottomBar
import com.android.frontend.view_models.user.CartViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val mainNavController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()

    Scaffold(
        modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        bottomBar = {
            BottomBar(navController = mainNavController, cartViewModel = cartViewModel)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MainGraph( navController = mainNavController, cartViewModel = cartViewModel, modifier = Modifier.fillMaxSize().padding(0.dp))
        }
    }
}