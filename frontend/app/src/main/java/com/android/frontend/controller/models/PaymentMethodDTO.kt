package com.android.frontend.controller.models

data class PaymentMethodDTO (

    val id: kotlin.String,
    val creditCard: kotlin.String,
    val expiryDate: java.time.LocalDate,
    val owner: kotlin.String,
    val isDefault: kotlin.Boolean
) {
}