package com.android.frontend.dto

import java.time.LocalDateTime
import java.util.UUID

data class OrderDTO(
    val id: UUID,
    val userId: Long,
    val addressId: Long,
    val paymentMethodId: Long,
    val items: List<CartItemDTO>,
    val status: OrderStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
){
    enum class OrderStatus {
        CREATED,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        RETURNED
    }
}