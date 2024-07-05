package com.android.frontend.service

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ProductImageService {

    @Multipart
    @POST("/api/v1/productPicture/uploadInitialPhotoProductById/{productId}")
    fun uploadInitialPhotoProductById(
        @Header("Authorization") authHeader: String,
        @Path("productId") productId: Long,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>

    @GET("/api/v1/productPicture/getPhotoProductById/{productId}")
    @Streaming
    fun getPhotoProductById(
        @Header("Authorization") authHeader: String,
        @Path("productId") productId: Long
    ): Call<ResponseBody>

    @Multipart
    @PUT("/api/v1/productPicture/replacePhotoProductById/{productId}")
    fun replacePhotoProductById(
        @Header("Authorization") authHeader: String,
        @Path("productId") productId: Long,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>

    @DELETE("/api/v1/productPicture/deletePhotoProductById/{productId}")
    fun deletePhotoProductById(
        @Header("Authorization") authHeader: String,
        @Path("productId") productId: Long
    ): Call<ResponseBody>
}
