package com.android.frontend.controller.infrastructure

import android.content.Context
import android.util.Log
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.UserDTO
import com.android.frontend.model.SecurePreferences
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.pow

class TokenManager {
    private var refreshAttempts = 0
    private val maxRefreshAttempts = 2
    private var refreshTokenJob: Deferred<Boolean>? = null
    private val mutex = Mutex()

    companion object {
        private var instance: TokenManager? = null
        fun getInstance(): TokenManager {
            if (instance == null) {
                instance = TokenManager()
            }
            return instance!!
        }
    }

    suspend fun isUserLoggedIn(context: Context): Boolean {
        val accessToken = getAccessToken(context)
        val refreshToken = getRefreshToken(context)

        if (accessToken != null && refreshToken != null) {
            val userService = RetrofitInstance.getUserApi(context)
            return try {
                val response = withContext(Dispatchers.IO) {
                    userService.me("Bearer $accessToken").execute()
                }

                if (response.isSuccessful) {
                    if (response.body() != null && response.body() is UserDTO) {
                        SecurePreferences.saveUser(context, response.body()!!)
                    }
                    true
                } else {
                    tryRefreshToken(context)
                }
            } catch (e: Exception) {
                Log.e("DEBUG TokenManager", "Error verifying token", e)
                false
            }
        } else {
            return false
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

    suspend fun tryRefreshToken(context: Context): Boolean {
        if (refreshAttempts >= maxRefreshAttempts) {
            Log.w("DEBUG TokenManager", "Max refresh attempts reached")
            return false
        }

        return mutex.withLock {
            if (refreshTokenJob?.isActive == true) {
                // If a refresh is already in progress, wait for its completion
                refreshTokenJob!!.await()
            } else {
                // Start a new refresh
                refreshTokenJob = CoroutineScope(Dispatchers.IO).async {
                    refreshToken(context)
                }
                refreshTokenJob!!.await()
            }
        }
    }

    private suspend fun refreshToken(context: Context): Boolean {
        val userService = RetrofitInstance.getUserApi(context)
        val refreshToken = getRefreshToken(context) ?: return false

        return try {
            val response = withContext(Dispatchers.IO) {
                userService.refreshToken(refreshToken).execute()
            }

            if (response.isSuccessful) {
                val tokenMap = response.body()
                val newAccessToken = tokenMap?.get("accessToken")
                val newRefreshToken = tokenMap?.get("refreshToken")
                if (newAccessToken != null && newRefreshToken != null) {
                    Log.d("DEBUG TokenManager", "New access token: $newAccessToken")
                    Log.d("DEBUG TokenManager", "New refresh token: $newRefreshToken")
                    saveTokens(context, newAccessToken, newRefreshToken)
                    refreshAttempts = 0
                    true
                } else {
                    handleRefreshFailure()
                    false
                }
            } else {
                handleRefreshFailure()
                false
            }
        } catch (e: Exception) {
            Log.e("DEBUG TokenManager", "Error refreshing token", e)
            handleRefreshFailure()
            false
        }
    }

    private suspend fun handleRefreshFailure() {
        refreshAttempts++
        delay(1000L * (2.0.pow(refreshAttempts.toDouble()).toLong())) // Exponential backoff
    }
}
