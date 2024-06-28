package com.android.frontend

import android.app.Application
import android.content.Context
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
import com.android.frontend.controller.models.UserDTO
import kotlinx.coroutines.launch
import com.android.frontend.model.SecurePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        if (!sharedPreferences.contains("isDarkTheme")) {
            val isDarkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            sharedPreferences.edit().putBoolean("isDarkTheme", isDarkTheme).apply()
        }

        viewModel.makeApiCall()

        setContent {
            FrontendTheme {
                AppRouter()
            }
        }
    }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _apiResponse = MutableLiveData<UserDTO>()
    val apiResponse: LiveData<UserDTO> get() = _apiResponse

    fun makeApiCall() {
        Log.d("MainViewModel", "Making API call")
        viewModelScope.launch {
            try {
                val userService = RetrofitInstance.getUserApi(getApplication<Application>().applicationContext)
                val accessToken = SecurePreferences.getAccessToken(getApplication<Application>().applicationContext)
                val response = userService.me("Bearer $accessToken").awaitResponse()
                if (response.isSuccessful) {
                    _apiResponse.postValue(response.body())
                } else {
                    handleApiError(response.code())
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error making API call", e)
            }
        }
    }

    private fun handleApiError(code: Int) {
        Log.e("MainViewModel", "API call failed with code $code")
        if (code == 500) {
            viewModelScope.launch {
                val newAccessToken = refreshAccessToken()
                if (newAccessToken != null) {
                    makeApiCall()
                } else {
                    Log.e("MainViewModel", "Error refreshing access token")
                }
            }
        } else {
            Log.e("MainViewModel", "API call failed with code $code")
        }
    }

    private suspend fun refreshAccessToken(): String? {
        Log.d("MainViewModel", "Refreshing access token")
        return withContext(Dispatchers.IO) {
            try {
                val refreshToken = SecurePreferences.getRefreshToken(getApplication<Application>().applicationContext)
                Log.d("MainViewModel", "Refresh token: $refreshToken")
                if (refreshToken != null) {
                    val userService = RetrofitInstance.getUserApi(getApplication<Application>().applicationContext)
                    val response = userService.refreshToken("Bearer $refreshToken").awaitResponse()
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val newAccessToken = responseBody?.get("accessToken")
                        val newRefreshToken = responseBody?.get("refreshToken")
                        if (newAccessToken != null && newRefreshToken != null) {
                            SecurePreferences.saveAccessToken(getApplication<Application>().applicationContext, newAccessToken)
                            SecurePreferences.saveRefreshToken(getApplication<Application>().applicationContext, newRefreshToken)
                            return@withContext newAccessToken
                        }
                    }
                }
                return@withContext null
            } catch (e: Exception) {
                return@withContext null
            }
        }
    }
}
