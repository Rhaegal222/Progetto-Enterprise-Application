package com.android.frontend.service

import com.android.frontend.dto.BrandDTO
import com.android.frontend.dto.creation.BrandCreateDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface BrandService {

    @POST("/api/v1/brand/addBrand")
    fun addBrand(
        @Header("Authorization") token: String,
        @Body brandCreateDTO: BrandCreateDTO
    ): Call<Void>

    @DELETE("/api/v1/brand/deleteBrand")
    fun deleteBrand(
        @Header("Authorization") token: String,
        @Query("id") id: Long
    ): Call<String>

    @GET("/api/v1/brand/allBrands")
    fun getAllBrands(
        @Header("Authorization") token: String,
    ): Call<List<BrandDTO>>

    @GET("/api/v1/brand/getBrandById")
    fun getBrandById(
        @Header("Authorization") token: String,
        @Query("id") id: Long
    ): Call<BrandDTO>

    @GET("/api/v1/brand/getBrandByName")
    fun getBrandByName(
        @Header("Authorization") token: String,
        @Query("name") name: String
    ): Call<BrandDTO>


}
