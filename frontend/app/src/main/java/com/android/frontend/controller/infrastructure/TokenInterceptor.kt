package com.android.frontend.controller.infrastructure

import okhttp3.Interceptor
import okhttp3.Response
import android.content.Context
import com.android.frontend.RetrofitInstance
import com.android.frontend.model.SecurePreferences
import com.android.frontend.model.SecurePreferences.getRefreshToken

class TokenInterceptor(private val context: Context) : Interceptor {

    private val userService = RetrofitInstance.getSimpleUserApi()

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = SecurePreferences.getAccessToken(context)
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        val response = chain.proceed(request)

        if (response.code == 401 || response.code == 403) {
            synchronized(this) {
                val newAccessToken = refreshToken()

                if (newAccessToken != null) {
                    val newRequest = chain.request().newBuilder()
                        .removeHeader("Authorization")
                        .addHeader("Authorization", "Bearer $newAccessToken")
                        .build()
                    return chain.proceed(newRequest)
                }
            }
        }

        return response
    }

    private fun refreshToken(): String? {
        val refreshToken = getRefreshToken(context) ?: return null

        val response = userService.refreshToken("Bearer $refreshToken").execute()

        return if (response.isSuccessful) {
            val responseBody = response.body()
            val newAccessToken = responseBody?.get("accessToken")
            val newRefreshToken = responseBody?.get("refreshToken")
            if (newAccessToken != null && newRefreshToken != null) {
                saveTokens(newAccessToken, newRefreshToken)
                newAccessToken
            } else {
                null
            }
        } else {
            null
        }
    }

    private fun saveTokens(accessToken: String, refreshToken: String) {
        SecurePreferences.saveAccessToken(context, accessToken)
        SecurePreferences.saveRefreshToken(context, refreshToken)
    }
}
