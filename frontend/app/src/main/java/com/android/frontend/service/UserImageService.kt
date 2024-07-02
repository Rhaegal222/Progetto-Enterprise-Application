package com.android.frontend.service

import com.android.frontend.dto.UserImageDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UserImageService {

    @GET("api/v1/images/{type}/{folder_name}/{file_name}")
    @Streaming
    fun getImage(
        @Path("type") type: String,
        @Path("folder_name") folderName: String,
        @Path("file_name") fileName: String
    ): Call<ResponseBody>

    @Multipart
    @POST("api/v1/images/users/photo-profile")
    fun savePhotoUser(
        @Header("Authorization") authHeader: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<UserImageDTO>

    @DELETE("api/v1/images/users/photo-profile/{id}")
    fun deletePhotoUser(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Call<Void>
}
