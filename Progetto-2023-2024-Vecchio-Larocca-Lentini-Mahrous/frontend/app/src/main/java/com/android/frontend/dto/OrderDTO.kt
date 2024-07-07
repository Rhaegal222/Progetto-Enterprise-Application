package com.android.frontend.dto

import java.math.BigDecimal
import org.threeten.bp.LocalDateTime
import java.util.UUID

data class OrderDTO(
    val id: UUID,
    val items: List<OrderItemDTO>,
    val totalCost: BigDecimal,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
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