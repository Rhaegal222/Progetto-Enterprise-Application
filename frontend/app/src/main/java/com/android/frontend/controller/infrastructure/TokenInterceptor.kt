package com.android.frontend.controller.infrastructure

import okhttp3.Interceptor
import okhttp3.Response
import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.frontend.MainActivity
import com.android.frontend.model.CurrentDataUtils
import kotlinx.coroutines.*
import okhttp3.Request

class TokenInterceptor(private val context: Context) : Interceptor {

    private val requestQueue = mutableListOf<Interceptor.Chain>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private fun buildRequest(request: Request, token: String?): Request {
        return request.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = TokenManager.getInstance().getAccessToken(context)
        val request = buildRequest(chain.request(), accessToken)
        val response = chain.proceed(request)

        Log.d("TokenInterceptor", "HTTP request: ${request.url}")
        Log.d("TokenInterceptor", "HTTP response: ${response.code}")

        return handleResponse(chain, response)
    }

    private fun handleResponse(chain: Interceptor.Chain, response: Response): Response {
        if (response.code == 401 || response.code == 403) {
            synchronized(this) {
                if (!CurrentDataUtils.tokenExpired) {
                    CurrentDataUtils.tokenExpired = true
                    if (requestQueue.isEmpty())
                        requestQueue.add(chain)
                    coroutineScope.launch {
                        if (TokenManager.getInstance().tryRefreshToken(context)) {
                            if (requestQueue.isNotEmpty()) {
                                requestQueue.forEach {
                                    val newRequest = buildRequest(it.request(), TokenManager.getInstance().getAccessToken(context))
                                    val newResponse = it.proceed(newRequest)
                                    if (newResponse.code != 200) {
                                        withContext(Dispatchers.Main) { logout() }
                                        return@forEach
                                    }
                                }
                                requestQueue.clear()
                                CurrentDataUtils.tokenExpired = false
                            }
                        } else {
                            withContext(Dispatchers.Main) { logout() }
                        }
                    }
                } else {
                    requestQueue.add(chain)
                }
            }
        }
        return response
    }

    private fun logout() {
        requestQueue.clear()
        TokenManager.getInstance().clearTokens(context)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
}
