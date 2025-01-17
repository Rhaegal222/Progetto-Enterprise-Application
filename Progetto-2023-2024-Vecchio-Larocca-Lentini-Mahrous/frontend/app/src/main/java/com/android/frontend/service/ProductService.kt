package com.android.frontend.service

import com.android.frontend.dto.ProductDTO
import com.android.frontend.dto.ProductUpdateRequest
import com.android.frontend.dto.creation.ProductCreateDTO
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.*

interface ProductService {

    @POST("/api/v1/products/addProduct")
    fun addProduct(
        @Header("Authorization") authorization: String,
        @Body productCreateDTO: ProductCreateDTO
    ): Call<Map<String, String>>

    @DELETE("/api/v1/products/deleteProduct/{id}")
    fun deleteProduct(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    ): Call<Void>

    @PATCH("/api/v1/products/updateProduct/{id}")
    fun updateProduct(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long,
        @Body patch: ProductUpdateRequest
    ): Call<ProductDTO>

    @GET("/api/v1/products/getProductById/{id}")
    fun getProductById(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    ): Call<ProductDTO>

    @GET("/api/v1/products/getAllProducts")
    fun getAllProducts(
        @Header("Authorization") authorization: String
    ): Call<List<ProductDTO>>

    @GET("/api/v1/products/getProductsByCategory/")
    fun getProductsByCategory(
        @Header("Authorization") authorization: String,
        @Query("categoryName") categoryName: String
    ): Call<List<ProductDTO>>

    @GET("/api/v1/products/getProductsByBrand/")
    fun getProductsByBrand(
        @Header("Authorization") authorization: String,
        @Query("brandName") brandName: String
    ): Call<List<ProductDTO>>

    @GET("/api/v1/products/getProductsByPriceRange/")
    fun getProductsByPriceRange(
        @Header("Authorization") authorization: String,
        @Query("min") min: Double,
        @Query("max") max: Double
    ): Call<List<ProductDTO>>

    @GET("/api/v1/products/getSalesProducts")
    suspend fun getSalesProducts(
        @Header("Authorization") authorization: String
    ): Response<List<ProductDTO>>
}
