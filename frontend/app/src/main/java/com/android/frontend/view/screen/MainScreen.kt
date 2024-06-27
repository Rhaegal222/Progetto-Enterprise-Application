package com.android.frontend.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.android.frontend.view.menu.BottomBar

@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) {
        Box(modifier = Modifier.padding(it)) {
            // TabGraph(navController = navController)
        }
    }
}


