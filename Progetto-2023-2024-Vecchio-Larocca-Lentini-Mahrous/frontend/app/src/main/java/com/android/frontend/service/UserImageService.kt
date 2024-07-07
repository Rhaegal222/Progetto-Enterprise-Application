package com.android.frontend.service

import com.android.frontend.dto.UserImageDTO
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UserImageService {

    @Multipart
    @POST("/api/v1/profilePicture/uploadInitialPhotoProfile")
    fun uploadInitialPhotoProfile(
        @Header("Authorization") authHeader: String,
        @Part("userId") userId: String,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>

    @GET("/api/v1/profilePicture/getPhotoProfileById/{userId}")
    @Streaming
    fun getPhotoProfileById(
        @Header("Authorization") authHeader: String,
        @Path("userId") userId: String
    ): Call<ResponseBody>

    @GET("/api/v1/profilePicture/getMyPhotoProfile")
    @Streaming
    fun getMyPhotoProfile(
        @Header("Authorization") authHeader: String
    ): Call<ResponseBody>

    @Multipart
    @PUT("/api/v1/profilePicture/replaceMyPhotoProfile")
    fun replaceMyPhotoProfile(
        @Header("Authorization") authHeader: String,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>

    @DELETE("/api/v1/profilePicture/deleteMyPhotoProfile")
    fun deleteMyPhotoProfile(
        @Header("Authorization") authHeader: String
    ): Call<ResponseBody>
}
