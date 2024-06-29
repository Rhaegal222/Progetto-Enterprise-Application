package com.android.frontend.controller.infrastructure

import okhttp3.Interceptor
import okhttp3.Response
import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.frontend.MainActivity
import com.android.frontend.RetrofitInstance
import com.android.frontend.model.SecurePreferences
import com.android.frontend.model.SecurePreferences.getRefreshToken

class TokenInterceptor(private val context: Context) : Interceptor {

    private val userService = RetrofitInstance.getSimpleUserApi()

    override fun intercept(chain: Interceptor.Chain): Response {
        Log.d("TokenInterceptor", "Intercepting request...")
        val accessToken = SecurePreferences.getAccessToken(context)
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        val response = chain.proceed(request)
        Log.d("TokenInterceptor", "Response code: ${response.code}")
        if (response.code == 401 || response.code == 403) {
            synchronized(this) {
                val newAccessToken = refreshToken()

                if (newAccessToken != null) {
                    val newRequest = chain.request().newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $newAccessToken")
                        .build()
                    return chain.proceed(newRequest)
                } else {
                    return response
                }
            }
        }
        return response
    }

    private fun logout() {
        SecurePreferences.clearAll(context)
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }

    private fun refreshToken(): String? {
        Log.d("TokenInterceptor", "Refreshing token...")
        val refreshToken = getRefreshToken(context) ?: return null

        val response = userService.refreshToken("Bearer $refreshToken").execute()

        return if (response.isSuccessful) {
            val responseBody = response.body()
            val newAccessToken = responseBody?.get("accessToken")
            val newRefreshToken = responseBody?.get("refreshToken")
            if (newAccessToken != null && newRefreshToken != null) {
                Log.d("TokenInterceptor", "New access token: $newAccessToken\nNew refresh token: $newRefreshToken")
                saveTokens(newAccessToken, newRefreshToken)
                newAccessToken
            } else {
                logout()
                null
            }
        } else {
            logout()
            null
        }
    }

    private fun saveTokens(accessToken: String, refreshToken: String) {
        SecurePreferences.saveAccessToken(context, accessToken)
        SecurePreferences.saveRefreshToken(context, refreshToken)
    }
}
