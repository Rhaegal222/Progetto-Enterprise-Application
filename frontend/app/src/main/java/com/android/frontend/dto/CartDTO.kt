package com.android.frontend.dto

data class CartDTO(
    val id: kotlin.String,
    val userId: kotlin.String,
    val cartItems: List<CartItemDTO>
)
