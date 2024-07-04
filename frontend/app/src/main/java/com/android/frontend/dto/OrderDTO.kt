package com.android.frontend.dto

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class OrderDTO(
    val id: UUID,
    val items: List<OrderItemDTO>,
    val totalCost: BigDecimal,
    val status: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val user: UserDTO,
    val address: AddressDTO,
    val paymentMethod: PaymentMethodDTO
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