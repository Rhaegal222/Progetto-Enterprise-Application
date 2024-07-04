package com.android.frontend.dto

import java.math.BigDecimal
import java.util.UUID

data class CartItemDTO(
    var id: UUID,
    var productId: Long,
    var quantity: Int
)