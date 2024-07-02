package com.android.frontend.service

import com.android.frontend.dto.WishlistDTO
import com.android.frontend.dto.creation.WishlistCreateDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface WishlistService {

    @POST("/api/v1/wishlist/createWishlist")
    fun createWishlist(
        @Header("Authorization") authorization: String,
        @Body wishlistCreateDTO: WishlistCreateDTO
    ): Call<Void>

    @POST("/api/v1/wishlist/{wishlistId}/products")
    fun addProductToWishlist(
        @Header("Authorization") authorization: String,
        @Path("wishlistId") wishlistId: Long,
        @Body productId: Set<String>
    ): Call<Void>

    @GET("/api/v1/wishlist/getAllLoggedUserWishlists")
    fun getAllLoggedUserWishlists(
        @Header("Authorization") authorization: String
    ): Call<List<WishlistDTO>>


    @DELETE("/api/v1/wishlist/{wishlistId}/products/{productId}")
    fun removeProductsFromWishlist(
        @Header("Authorization") authorization: String,
        @Path("wishlistId") wishlistId: Long,
        @Path("productId") productId: Set<String>
    ): Call<Void>


    @DELETE("/api/v1/wishlist/deleteWishlist/{id}")
    fun deleteWishlist(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    ): Call<Void>



}