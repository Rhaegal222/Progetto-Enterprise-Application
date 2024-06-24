package com.example.frontend.service

import com.example.frontend.controller.models.UserDTO
import com.example.frontend.controller.models.UserBasicDTO
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    @POST("/api/v1/users/register")
    fun register(
        @Query("firstname") firstname: String,
        @Query("lastname") lastname: String,
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

    @GET("/api/v1/users/{id}")
    fun userById(
        @Path("id") id: String
    ): Call<UserBasicDTO>

    @GET("/api/v1/users/find-by-username")
    fun findByUsername(
        @Query("username") username: String
    ): Call<UserBasicDTO>

    @PATCH("/api/v1/users/{id}")
    fun updateUser(
        @Path("id") id: String,
        @Body patch: UserDTO
    ): Call<UserDTO>

    @DELETE("/api/v1/users/{id}")
    fun deleteUser(
        @Path("id") id: String
    ): Call<Void>

    @GET("/api/v1/users/changePassword")
    fun changePassword(
        @Query("token") token: String,
        @Query("oldPassword") oldPassword: String,
        @Query("newPassword") newPassword: String
    ): Call<Void>

    @GET("/api/v1/users/getNewPassword")
    fun resetPasswordToken(
        @Query("token") token: String
    ): Call<Void>
}
