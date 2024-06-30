package com.android.frontend.config

import okhttp3.Interceptor
import okhttp3.Response
import android.content.Context
import android.util.Log
import kotlinx.coroutines.runBlocking
import java.io.IOException

class TokenInterceptor(private val context: Context) : Interceptor {

    private fun buildRequest(request: okhttp3.Request, token: String?): okhttp3.Request {
        return request.newBuilder()
            .addHeader("Authorization", token ?: "")
            .build()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking {
            TokenManager.getInstance().getAccessToken(context)
        }
        val originalRequest = chain.request()

        var request = buildRequest(originalRequest, accessToken)
        var response = chain.proceed(request)

        if (response.code == 401 || response.code == 403) {
            response.close()
            synchronized(this) {
                val tokenManager = TokenManager.getInstance()
                val newAccessToken = runBlocking {
                    Log.d("DEBUG", "${getCurrentStackTrace()}, Token invalid, attempting to refresh token")
                    if (tokenManager.tryRefreshToken(context)) {
                        Log.d("DEBUG", "${getCurrentStackTrace()}, Successfully refreshed token")
                        tokenManager.getAccessToken(context)
                    } else {
                        Log.d("DEBUG", "${getCurrentStackTrace()}, Token refresh failed, logging out")
                        TokenManager.getInstance().logout(context)
                        null
                    }
                }

                if (newAccessToken != null) {
                    Log.d("DEBUG", "${getCurrentStackTrace()}, Successfully refreshed token, retrying request")
                    request = buildRequest(originalRequest, newAccessToken)
                    response = chain.proceed(request)
                } else {
                    Log.d("DEBUG", "${getCurrentStackTrace()}, Token refresh failed, logging out")
                    TokenManager.getInstance().logout(context)
                    throw IOException("Token refresh failed")
                }
            }
        }
        return response
    }
}
