package com.android.frontend.controller.models

data class CartDTO(
    val id: kotlin.String,
    val userId: kotlin.String,
    val cartItems: List<CartItemDTO>
)
