package com.android.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.android.frontend.navigation.NavGraph
import com.android.frontend.ui.theme.FrontendTheme

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrontendTheme {
                navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
