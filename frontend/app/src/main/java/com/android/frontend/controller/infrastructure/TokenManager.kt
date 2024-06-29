package com.android.frontend.controller.infrastructure

import android.content.Context
import android.util.Log
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.UserDTO
import com.android.frontend.model.SecurePreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.awaitResponse

class TokenManager {
    companion object {
        private var instance: TokenManager? = null
        fun getInstance(): TokenManager {
            if (instance == null) {
                instance = TokenManager()
            }
            return instance!!
        }
    }

    fun isLoggedIn(context: Context, callback: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val userService = RetrofitInstance.getUserApi(context)
            try {
                val response = userService.me("Bearer ${getAccessToken(context)}").awaitResponse()

                if (response.isSuccessful) {
                    response.body()?.let {
                        SecurePreferences.saveUser(context, it)
                        withContext(Dispatchers.Main) {
                            callback(true)
                        }
                    } ?: run {
                        handleResponseError(context, response)
                        withContext(Dispatchers.Main) {
                            callback(false)
                        }
                    }
                } else {
                    handleResponseError(context, response)
                    withContext(Dispatchers.Main) {
                        callback(false)
                    }
                }
            } catch (e: Exception) {
                Log.e("TokenManager", "Error fetching user profile: ${e.message}", e)
                clearTokens(context)
                withContext(Dispatchers.Main) {
                    callback(false)
                }
            }
        }
    }

    private fun handleResponseError(context: Context, response: Response<UserDTO>) {
        when (response.code()) {
            401 -> Log.e("TokenManager", "Unauthorized: ${response.message()}")
            403 -> Log.e("TokenManager", "Forbidden: ${response.message()}")
            else -> Log.e("TokenManager", "Error fetching user profile: ${response.message()}")
        }

        val refreshToken = getRefreshToken(context)
        clearTokens(context)

        try {
            if (refreshToken != null) {
                if (tryRefreshToken(context)) {
                    Log.e("TokenManager", "Refreshed token")
                } else {
                    Log.e("TokenManager", "Failed to refresh token")
                }
            } else {
                Log.e("TokenManager", "No refresh token found")
            }
        } catch (e: Exception) {
            Log.e("TokenManager", "Error refreshing token: ${e.message}", e)
        }
    }

    fun getAccessToken(context: Context): String? {
        return SecurePreferences.getAccessToken(context)
    }

    fun getRefreshToken(context: Context): String? {
        return SecurePreferences.getRefreshToken(context)
    }

    fun saveTokens(context: Context, accessToken: String, refreshToken: String) {
        SecurePreferences.saveAccessToken(context, accessToken)
        SecurePreferences.saveRefreshToken(context, refreshToken)
    }

    fun clearTokens(context: Context) {
        SecurePreferences.clearAll(context)
    }

    fun tryRefreshToken(context: Context): Boolean {
        val userService = RetrofitInstance.getUserApi(context)
        val refreshToken = getRefreshToken(context) ?: return false
        val response = userService.refreshToken("Bearer $refreshToken").execute()
        if (response.isSuccessful) {
            val tokenMap = response.body()
            val newAccessToken = tokenMap?.get("accessToken")
            val newRefreshToken = tokenMap?.get("refreshToken")
            if (newAccessToken != null && newRefreshToken != null) {
                saveTokens(context, newAccessToken, newRefreshToken)
                return true
            }
        } else {
            return false
        }
        return false
    }
}
