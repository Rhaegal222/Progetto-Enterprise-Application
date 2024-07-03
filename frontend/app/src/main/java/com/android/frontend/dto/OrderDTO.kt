package com.android.frontend.dto

import java.time.LocalDateTime

data class OrderDTO(
    var id: String,
    var items: List<CartItemDTO>,
    var status: OrderStatus,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
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