package com.android.frontend.service

import retrofit2.http.GET

interface ApiService {
    @GET("api/v1/baseUrl")
    suspend fun getBaseUrl(): BaseUrlResponse

    data class BaseUrlResponse(
        val baseUrl: String
    )
}
