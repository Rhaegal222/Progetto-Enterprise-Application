package com.android.frontend.dto

import java.math.BigDecimal

data class CartItemDTO(
    var id: String,
    var productId: String,
    var quantity: Int
)