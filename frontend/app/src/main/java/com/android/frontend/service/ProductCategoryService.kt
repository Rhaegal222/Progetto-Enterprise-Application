package com.android.frontend.service

import com.android.frontend.dto.ProductCategoryDTO
import com.android.frontend.dto.creation.ProductCategoryCreateDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ProductCategoryService {

    @POST("/api/v1/productCategory/addCategory")
    fun addCategory(
        @Header("Authorization") token: String,
        @Body productCategoryCreateDTO: ProductCategoryCreateDTO
    ): Call<Void>

    @DELETE("/api/v1/productCategory/deleteCategory")
    fun deleteCategory(
        @Header("Authorization") token: String,
        @Query("id") id: Int
    ): Call<String>

    @GET("/api/v1/productCategory/allCategories")
    fun getAllCategories(
        @Header("Authorization") authorization: String
    ): Call<List<ProductCategoryDTO>>
}
