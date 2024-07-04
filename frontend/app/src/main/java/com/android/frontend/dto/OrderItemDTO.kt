package com.android.frontend.dto

import java.math.BigDecimal
import java.util.UUID

data class OrderItemDTO(
    val id: UUID,
    val productId: Long,
    val quantity: Int,
    val partialCost: BigDecimal
)
