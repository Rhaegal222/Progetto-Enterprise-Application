package com.android.frontend.view.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.android.frontend.navigation.MainGraph
import com.android.frontend.view.component.BottomBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val mainNavController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomBar(navController = mainNavController)
        }
    ) { innerPadding ->
        MainGraph(navController = mainNavController, modifier = Modifier.padding(innerPadding))
    }
}
