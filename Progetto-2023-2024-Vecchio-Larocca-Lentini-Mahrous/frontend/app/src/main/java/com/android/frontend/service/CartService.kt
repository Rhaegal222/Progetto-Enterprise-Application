package com.android.frontend.service

import com.android.frontend.dto.creation.CartCreateDTO
import com.android.frontend.dto.CartDTO
import com.android.frontend.dto.creation.CartItemCreateDTO
import retrofit2.Call
import retrofit2.http.*
import java.util.UUID

interface CartService {

    @GET("/api/v1/cart/getCartByUserId/{userId}")
    fun getCartByUserId(
        @Path("userId") userId: UUID
    ): Call<CartDTO>

    @GET("/api/v1/cart/getCartForLoggedUser")
    fun getCartForLoggedUser(
        @Header("Authorization") token: String
    ): Call<CartDTO>


    @PUT("/api/v1/cart/editItem")
    fun editItemInCart(
        @Header("Authorization") token: String,
        @Query("cartItemId") cartItemId: UUID,
        @Query("quantity") quantity: Int
    ): Call<CartDTO>

    @POST("/api/v1/cart/addItem")
    fun addItemToCart(
        @Header("Authorization") token: String,
        @Body cartItemCreateDTO: CartItemCreateDTO
    ): Call<CartDTO>

    @DELETE("/api/v1/cart/removeItem/{cartItemId}")
    fun removeItemFromCart(
        @Header("Authorization") token: String,
        @Path("cartItemId") cartItemId: UUID
    ): Call<CartDTO>

    @DELETE("/api/v1/cart/clearCart")
    fun clearCart(
        @Header("Authorization") token: String
    ): Call<CartDTO>
}
