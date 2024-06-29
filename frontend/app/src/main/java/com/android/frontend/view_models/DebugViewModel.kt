package com.android.frontend.view_models;

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.model.SecurePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Callback

class DebugViewModel : ViewModel() {
    fun rejectToken(context: Context) {
        viewModelScope.launch {
            val userService = RetrofitInstance.getUserApi(context)
            val accessToken = SecurePreferences.getAccessToken(context)
            val call = userService.rejectToken("Bearer $accessToken")
            try {
                val response = withContext(Dispatchers.IO) {
                    call.execute()
                }
                if (response.isSuccessful) {
                    Log.d("DebugViewModel", "Token rejected")
                } else {
                    Log.d("DebugViewModel", "Token rejection failed")
                }
            } catch (e: Exception) {
                Log.e("DebugViewModel", "Error rejecting token", e)
            }
        }
    }

    fun showToken(context: Context) {
        val accessToken = SecurePreferences.getAccessToken(context)
        val refreshToken = SecurePreferences.getRefreshToken(context)
        Log.d("DebugViewModel", "Access token: $accessToken")
        Log.d("DebugViewModel", "Refresh token: $refreshToken")
    }
}
