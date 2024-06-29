package com.android.frontend.view_models;

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.infrastructure.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DebugViewModel : ViewModel() {
    fun rejectToken(context: Context) {
        viewModelScope.launch {
            val userService = RetrofitInstance.getUserApi(context)
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val call = userService.rejectToken("Bearer $accessToken")
            try {
                val response = withContext(Dispatchers.IO) {
                    call.execute()
                }
                if (response.isSuccessful) {
                    Log.d("DEBUG DebugViewModel", "Token rejected")
                } else {
                    Log.d("DEBUG DebugViewModel", "Token rejection failed")
                }
            } catch (e: Exception) {
                Log.e("DEBUG DebugViewModel", "Error rejecting token", e)
            }
        }
    }

    fun showToken(context: Context) {
        val accessToken = TokenManager.getInstance().getAccessToken(context)
        val refreshToken = TokenManager.getInstance().getRefreshToken(context)
        Log.d("DEBUG DebugViewModel", "Access token: $accessToken")
        Log.d("DEBUG DebugViewModel", "Refresh token: $refreshToken")
    }
}
