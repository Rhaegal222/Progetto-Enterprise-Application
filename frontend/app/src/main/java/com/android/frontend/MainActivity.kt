package com.android.frontend

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.android.frontend.navigation.AppRouter
import com.android.frontend.ui.theme.FrontendTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        if (!sharedPreferences.contains("isDarkTheme")) {
            val isDarkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            sharedPreferences.edit().putBoolean("isDarkTheme", isDarkTheme).apply()
        }

        setContent {
            FrontendTheme {
                AppRouter()
            }
        }
    }
}
