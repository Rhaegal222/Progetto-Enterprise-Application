package com.android.frontend.service

import com.android.frontend.dto.CartCreateDTO
import com.android.frontend.dto.CartDTO
import retrofit2.Call
import retrofit2.http.*

interface CartService {

    @GET("/api/v1/cart/getCartByUserId/{userId}")
    fun getCartByUserId(@Path("userId") userId: String): Call<CartDTO>

    @POST("/api/v1/cart/addProduct")
    fun addProductToCart(@Body cartCreateDTO: CartCreateDTO): Call<CartDTO>

    @DELETE("/api/v1/cart/removeProduct/{cartItemId}")
    fun removeProductFromCart(@Path("cartItemId") cartItemId: String): Call<Void>

    @PUT("/api/v1/cart/updateProduct/{cartItemId}")
    fun updateCartItem(@Path("cartItemId") cartItemId: String, @Query("quantity") quantity: Int): Call<CartDTO>
}
