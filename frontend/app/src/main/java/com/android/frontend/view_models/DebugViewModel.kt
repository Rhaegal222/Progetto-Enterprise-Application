package com.android.frontend.view_models;

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.model.SecurePreferences
import kotlinx.coroutines.launch
import retrofit2.Callback

class DebugViewModel : ViewModel() {
    fun rejectToken(context: Context) {
        viewModelScope.launch {
            val userService = RetrofitInstance.getUserApi(context)
            val accessToken = SecurePreferences.getRefreshToken(context)
            val call = userService.rejectToken("Bearer $accessToken")
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                    if (response.isSuccessful) {
                        // SecurePreferences.clearAll(context)
                    }
                }

                override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                    // Do nothing
                }
            })
        }
    }

    fun showToken(context: Context) {
        val accessToken = SecurePreferences.getAccessToken(context)
        val refreshToken = SecurePreferences.getRefreshToken(context)
        Log.d("DebugViewModel", "Access token: $accessToken")
        Log.d("DebugViewModel", "Refresh token: $refreshToken")
    }
}
