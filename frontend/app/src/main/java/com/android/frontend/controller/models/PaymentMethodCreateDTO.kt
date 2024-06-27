package com.example.frontend.controller.models

import java.time.LocalDate

data class PaymentMethodCreateDTO(
    val creditCard: String,
    val expiryDate: String,
    val owner: String,
    val isDefault: Boolean
) {
}