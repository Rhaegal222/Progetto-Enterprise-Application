package com.example.frontend.service

import com.example.frontend.controller.models.UserDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {
    @POST("/api/v1/users/register")
    fun register(
        @Query("firstName") firstName: String,
        @Query("lastName") lastName: String,
        @Query("email") email: String,
        @Query("password") password: String
    ): Call<Void>

    @POST("/api/v1/users/authenticate")
    fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<Map<String, String>>

    @GET("/api/v1/users/refreshToken")
    fun refreshToken(
        @Header("Authorization") authorization: String
    ): Call<Map<String, String>>

    @GET("/api/v1/users/resetPassword")
    fun resetPassword(
        @Query("email") email: String
    ): Call<Void>

    @POST("/api/v1/users/google-auth")
    fun googleAuth(
        @Query("idTokenString") idTokenString: String
    ): Call<Map<String, String>>

    @GET("/api/v1/users/me")
    fun me(
        @Header("Authorization") authorization: String
    ): Call<UserDTO>
}
