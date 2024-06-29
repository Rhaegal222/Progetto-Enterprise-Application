package com.android.frontend

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.android.frontend.navigation.AppRouter
import com.android.frontend.ui.theme.FrontendTheme
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.frontend.controller.infrastructure.TokenManager
import com.android.frontend.controller.models.UserDTO
import kotlinx.coroutines.launch
import com.android.frontend.model.SecurePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

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
