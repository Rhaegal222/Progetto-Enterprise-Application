package com.android.frontend.service


import com.android.frontend.dto.ProductDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {

    @POST("/api/v1/products/addProduct")
    fun addProduct(
        @Body productDTO: ProductDTO
    ): Call<Void>

    @DELETE("/api/v1/products/deleteProduct/")
    fun deleteProduct(
        @Query("id") id: String
    ): Call<String>

    @GET("/api/v1/products/getProductById/{id}")
    fun getProductById(
        @Path("id") id: String
    ): Call<ProductDTO>

    @GET("/api/v1/products/getAllProducts")
    fun getAllProducts(
    ): Call<List<ProductDTO>>

    @PUT("/api/v1/products/updateProduct/")
    fun updateProduct(
        @Query("id") id: String, @Body product: ProductDTO
    ): Call<ProductDTO>

    @GET("/api/v1/products/getProductsByCategory/")
    fun getProductsByCategory(
        @Query("categoryName") categoryName: String
    ): Call<List<ProductDTO>>

    @GET("/api/v1/products/getProductsByBrand/")
    fun getProductsByBrand(
        @Query("brandName") brandName: String
    ): Call<List<ProductDTO>>



    @GET("/api/v1/products/getProductsByPriceRange/")
    fun getProductsByPriceRange(
        @Query("min") min: Double, @Query("max") max: Double
    ): Call<List<ProductDTO>>
}