package com.android.frontend.service

import com.android.frontend.dto.ProductDTO
import com.android.frontend.dto.WishlistDTO
import com.android.frontend.dto.creation.WishlistCreateDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WishlistService {

    @POST("/api/v1/wishlist/addWishlist")
    fun addWishlist(
        @Header("Authorization") authorization: String,
        @Body wishlistCreateDTO: WishlistCreateDTO
    ): Call<Void>

    @DELETE("/api/v1/wishlist/deleteWishlist/{wishlistId}")
    fun deleteWishlist(
        @Header("Authorization") authorization: String,
        @Path("wishlistId") wishlistId: String
    ): Call<Void>

    @POST("/api/v1/wishlist/{wishlistId}/addProductsToWishlist/{productId}")
    fun addProductToWishlist(
        @Header("Authorization") authorization: String,
        @Path("wishlistId") wishlistId: String,
        @Path("productId") productId: Long
    ): Call<Void>

    @DELETE("/api/v1/wishlist/{wishlistId}/removeProductsFromWishlist/{productId}")
    fun removeProductsFromWishlist(
        @Header("Authorization") authorization: String,
        @Path("wishlistId") wishlistId: String,
        @Path("productId") productId: Long
    ): Call<Void>

    @GET("/api/v1/wishlist/getProductByWishlistId/{wishlistId}")
    fun getProductByWishlistId(
        @Header("Authorization") authorization: String,
        @Path("wishlistId") wishlistId: String
    ): Call<List<ProductDTO>>

    @GET("/api/v1/wishlist/getAllLoggedUserWishlists")
    fun getAllLoggedUserWishlists(
        @Header("Authorization") authorization: String
    ): Call<List<WishlistDTO>>

    @PUT("/api/v1/wishlist/updateWishlist/{wishlistId}")
    fun updateWishlist(
        @Header("Authorization") authorization: String,
        @Path("wishlistId") wishlistId: String,
        @Body wishlistDTO: WishlistDTO
    ): Call<WishlistDTO>

    @GET("/api/v1/wishlist/getWishlistById/{wishlistId}")
    fun getWishlistById(
        @Header("Authorization") authorization: String,
        @Path("wishlistId") wishlistId: String
    ): Call<WishlistDTO>

    @POST("/api/v1/wishlist/shareWishlist/{wishlistId}/{email}")
    fun shareWishlist(
        @Header("Authorization") authorization: String,
        @Path("wishlistId") wishlistId: String,
        @Path("email") email: String
    ): Call<Void>

    @DELETE("/api/v1/wishlist/removeWishlistAccess/{wishlistId}/{email}")
    fun removeWishlistAccess(
        @Header("Authorization") authorization: String,
        @Path("wishlistId") wishlistId: String,
        @Path("email") email: String
    ): Call<Void>

    @DELETE("/api/v1/wishlist/deleteSharedWishlistAccessByWishlistId/{wishlistId}")
    fun deleteSharedWishlistAccessByWishlistId(
        @Header("Authorization") authorization: String,
        @Path("wishlistId") wishlistId: String
    ): Call<Void>

}