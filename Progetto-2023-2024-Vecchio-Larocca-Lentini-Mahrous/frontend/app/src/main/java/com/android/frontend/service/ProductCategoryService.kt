package com.android.frontend.service

import com.android.frontend.dto.CategoryDTO
import com.android.frontend.dto.creation.CategoryCreateDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ProductCategoryService {

    @POST("/api/v1/category/addCategory")
    fun addCategory(
        @Header("Authorization") token: String,
        @Body categoryCreateDTO: CategoryCreateDTO
    ): Call<Void>

    @DELETE("/api/v1/category/deleteCategory")
    fun deleteCategory(
        @Header("Authorization") token: String,
        @Query("id") id: Long
    ): Call<String>

    @GET("/api/v1/category/getAllCategories")
    fun getAllCategories(
        @Header("Authorization") authorization: String
    ): Call<List<CategoryDTO>>
}
