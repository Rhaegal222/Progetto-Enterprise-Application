package com.android.frontend.service

import com.android.frontend.dto.UserDTO
import com.android.frontend.dto.basic.UserBasicDTO
import com.android.frontend.dto.UserUpdateRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    @POST("/api/v1/users/register")
    fun register(
        @Query("firstname") firstname: String,
        @Query("lastname") lastname: String,
        @Query("email") email: String,
        @Query("password") password: String
    ): Call<Void>

    @POST("/api/v1/users/login")
    fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<Map<String, String>>

    @POST("/api/v1/users/changeRole/{userId}")
    suspend fun changeRole(
        @Header("Authorization") token: String,
        @Path("userId") userId: String,
        @Query("role") role: String
    ): Response<Void>

    @DELETE("/api/v1/users/deleteUser/{id}")
    suspend fun deleteUser(
        @Header("Authorization") token: String,
        @Path("id") userId: String
    ): Response<Void>

    @GET("/api/v1/users/refreshToken")
    fun refreshToken(
        @Header("Authorization") authorization: String
    ): Call<Map<String, String>>

    @GET("/api/v1/users/rejectToken")
    fun rejectToken(
        @Header("Authorization") authorization: String
    ): Call<Void>

    @GET("/api/v1/users/resetPassword")
    fun resetPassword(
        @Query("email") email: String
    ): Call<Void>

    @GET("/api/v1/users/me")
    fun userBasicDTOCall(
        @Header("Authorization") authorization: String
    ): Call<UserBasicDTO>

    @GET("/api/v1/users/retrieveUserProfile")
    fun userDTOCall(
        @Header("Authorization") authorization: String
    ): Call<UserDTO>

    @GET("/api/v1/users/getAllUsers")
    suspend fun getAllUsers(
        @Header("Authorization") token: String
    ): Response<List<UserDTO>>

    @GET("/api/v1/users/findUserById/{id}")
    fun findUserById(
        @Path("id") id: String
    ): Call<UserDTO>

    @GET("/api/v1/users/findByUsername")
    fun findByUsername(
        @Query("username") username: String
    ): Call<UserBasicDTO>

    @PATCH("/api/v1/updateUser/{id}")
    fun updateUser(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Body patch: UserUpdateRequest
    ): Call<UserDTO>

    @DELETE("/api/v1/users/{id}")
    fun deleteUser(
        @Path("id") id: String
    ): Call<Void>

    @POST("/api/v1/users/changePassword")
    fun changePassword(
        @Header("Authorization") authorization: String,
        @Query("oldPassword") oldPpassword: String,
        @Query("newPassword") newPpassword: String
    ): Call<Void>

    @GET("/api/v1/users/getNewPassword")
    fun resetPasswordToken(
        @Query("token") token: String
    ): Call<Void>
}
