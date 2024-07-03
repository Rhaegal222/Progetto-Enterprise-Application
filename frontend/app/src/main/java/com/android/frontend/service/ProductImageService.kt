package com.android.frontend.service

import com.android.frontend.dto.ProductImageDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ProductImageService {
    @GET("/api/v1/productPicture/getImage/{type}/{folder_name}/{file_name:.*}")
    @Streaming
    fun getImage(
        @Path("type") type: String,
        @Path("folder_name") folderName: String,
        @Path("file_name") fileName: String
    ): Call<ResponseBody>

    @Multipart
    @POST("/api/v1/productPicture/uploadImage")
    fun savePhotoProduct(
        @Header("Authorization") authHeader: String,
        @Part file: MultipartBody.Part,
        @Part("product_id") productId: RequestBody,
        @Part("description") description: RequestBody
    ): Call<ProductImageDTO>


    @DELETE("/api/v1/productPicture/deleteImage/{id}")
    fun deletePhotoProduct(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Call<Void>
}