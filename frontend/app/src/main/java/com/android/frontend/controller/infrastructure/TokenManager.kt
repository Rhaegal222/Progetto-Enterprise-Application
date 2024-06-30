package com.android.frontend.controller.infrastructure

import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.frontend.MainActivity
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.UserBasicDTO
import com.android.frontend.controller.models.UserDTO
import com.android.frontend.model.CurrentDataUtils
import com.android.frontend.model.SecurePreferences
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.pow

class TokenManager {
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
        Log.d("DEBUG", "${getCurrentStackTrace()}, Checking if user is logged in")

        val accessToken = getAccessToken(context)
        val refreshToken = getRefreshToken(context)

        if (accessToken == null && refreshToken == null) {
            Log.d("DEBUG", "${getCurrentStackTrace()}, Tokens are null, user is not logged in")
            return false
        }

        if (accessToken != null && refreshToken != null) {
            Log.d("DEBUG", "${getCurrentStackTrace()}, Tokens are not null, user is logged in")
            val userService = RetrofitInstance.getUserApi(context)
            try {
                val response = withContext(Dispatchers.IO) {
                    Log.d("DEBUG", "${getCurrentStackTrace()}, Verifying token with access token: $accessToken")
                    userService.me("Bearer $accessToken").execute()
                }
                if (response.isSuccessful) {
                    Log.d("DEBUG", "${getCurrentStackTrace()}, User is logged in")
                    if (response.body() != null && response.body() is UserBasicDTO) {
                        SecurePreferences.saveUser(context, response.body()!!)
                    }
                    return true
                } else {
                    Log.d("DEBUG", "${getCurrentStackTrace()}, User is not logged in")
                    return false
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Error verifying token", e)
                return false
            }
        }
        return false
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
        Log.d("DEBUG", "${getCurrentStackTrace()}, Trying to refresh token (attempt ${CurrentDataUtils.refreshAttempts.get()})")

        CurrentDataUtils.refreshAttempts.incrementAndGet()

        if (CurrentDataUtils.refreshAttempts.get() >= maxRefreshAttempts) {
            Log.w("DEBUG", "${getCurrentStackTrace()}, Max refresh attempts reached")
            clearTokens(context)
            return false
        }

        return mutex.withLock {
            if (refreshTokenJob?.isActive == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()}, Waiting for ongoing refresh token job")
                refreshTokenJob!!.await()
            } else {
                Log.d("DEBUG", "${getCurrentStackTrace()}, Starting new refresh token job")
                refreshTokenJob = CoroutineScope(Dispatchers.IO).async {
                    refreshToken(context)
                }
                refreshTokenJob!!.await()
            }
        }
    }

    private suspend fun refreshToken(context: Context): Boolean {

        val userService = RetrofitInstance.getUserApi(context)
        val refreshToken = getRefreshToken(context)

        if (refreshToken.isNullOrEmpty()) {
            logout(context)
            return false
        }

        try {
            Log.d("DEBUG", "${getCurrentStackTrace()}, Attempt: ${CurrentDataUtils.refreshAttempts.get()} Refreshing token with refresh token: $refreshToken")

            val response = withContext(Dispatchers.IO) {
                userService.refreshToken(refreshToken).execute()
            }

            if (response.isSuccessful) {
                Log.d("DEBUG", "${getCurrentStackTrace()}, Token refreshed")
                val tokenMap = response.body()
                val newAccessToken = tokenMap?.get("accessToken")
                val newRefreshToken = tokenMap?.get("refreshToken")
                if (newAccessToken != null && newRefreshToken != null) {
                    Log.d("DEBUG", "${getCurrentStackTrace()}, New access token: $newAccessToken")
                    Log.d("DEBUG", "${getCurrentStackTrace()}, New refresh token: $newRefreshToken")
                    saveTokens(context, newAccessToken, newRefreshToken)
                    CurrentDataUtils.refreshAttempts.set(0)
                    Log.d("DEBUG", "${getCurrentStackTrace()}, Reset refresh attempts to 0")
                    return true
                } else {
                    Log.e("DEBUG", "${getCurrentStackTrace()}, Tokens are null")
                    handleRefreshFailure()
                    return false
                }
            } else {
                handleRefreshFailure()
                return false
            }
        } catch (e: Exception) {
            Log.e("DEBUG", "${getCurrentStackTrace()}, Error refreshing token", e)
            handleRefreshFailure()
            return false
        }
    }

    private suspend fun handleRefreshFailure() {
        val attempts = CurrentDataUtils.refreshAttempts.incrementAndGet()
        Log.d("DEBUG", "${getCurrentStackTrace()}, Incrementing refresh attempts to $attempts")
        delay(1000L * (2.0.pow(attempts.toDouble()).toLong())) // Exponential backoff
    }

    fun logout(context: Context) {
        clearTokens(context)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
}
