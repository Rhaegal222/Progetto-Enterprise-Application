package com.android.frontend.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface GoogleAuthenticationService {

    @POST("/api/v1/users/google-auth")
    fun googleAuth(
        @Query("idTokenString") idTokenString: String
    ): Call<Map<String, String>>

    @GET("/api/v1/users/refreshToken")
    fun refreshToken(
        @Header("Authorization") authorization: String
    ): Call<Map<String, String>>

}