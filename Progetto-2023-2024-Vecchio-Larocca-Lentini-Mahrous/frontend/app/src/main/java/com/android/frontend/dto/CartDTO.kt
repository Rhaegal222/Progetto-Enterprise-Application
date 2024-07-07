package com.android.frontend.dto

data class CartDTO(
    var id: String,
    var userId: String,
    var items: List<CartItemDTO>
)