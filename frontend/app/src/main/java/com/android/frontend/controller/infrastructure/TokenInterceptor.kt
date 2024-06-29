package com.android.frontend.controller.infrastructure

import okhttp3.Interceptor
import okhttp3.Response
import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.frontend.MainActivity
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import java.io.IOException

class TokenInterceptor(private val context: Context) : Interceptor {

    private fun buildRequest(request: Request, token: String?): Request {
        return request.newBuilder()
            .addHeader("Authorization", token ?: "")
            .build()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var accessToken = TokenManager.getInstance().getAccessToken(context)
        val originalRequest = chain.request()

        var request = buildRequest(originalRequest, accessToken)
        var response = chain.proceed(request)

        Log.d("DEBUG TokenInterceptor", "Access token: $accessToken")
        Log.d("DEBUG TokenInterceptor", "HTTP request: ${request.url}")
        Log.d("DEBUG TokenInterceptor", "HTTP response: ${response.code}")

        if (response.code == 401 || response.code == 403) {
            response.close()

            synchronized(this) {
                val tokenManager = TokenManager.getInstance()
                val newAccessToken = runBlocking {
                    if (tokenManager.tryRefreshToken(context)) {
                        tokenManager.getAccessToken(context)
                    } else {
                        null
                    }
                }

                if (newAccessToken != null) {
                    request = buildRequest(originalRequest, newAccessToken)
                    response = chain.proceed(request)
                } else {
                    logout()
                    throw IOException("Token refresh failed")
                }
            }
        }
        return response
    }

    private fun logout() {
        TokenManager.getInstance().clearTokens(context)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
}
