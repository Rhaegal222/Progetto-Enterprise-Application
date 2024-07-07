package com.android.frontend.dto

import java.math.BigDecimal
import java.util.UUID

data class OrderItemDTO(
    val id: UUID,
    val productName: String,
    val productId: Long,
    val quantity: Int,
    val partialCost: BigDecimal
)
