package com.android.frontend.view.screen

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.android.frontend.navigation.MainGraph
import com.android.frontend.view.menu.BottomBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val mainNavController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomBar(navController = mainNavController)
        }
    ){
        MainGraph(navController = mainNavController)
    }
}
