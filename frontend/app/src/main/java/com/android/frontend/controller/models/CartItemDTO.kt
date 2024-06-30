package com.android.frontend.controller.models

import java.math.BigDecimal

data class CartItemDTO(
    val id: String,
    val productId: String,
    val quantity: Int,
    val productName: String,
    val productPrice: BigDecimal,
    val deliveryPrice: BigDecimal
)
