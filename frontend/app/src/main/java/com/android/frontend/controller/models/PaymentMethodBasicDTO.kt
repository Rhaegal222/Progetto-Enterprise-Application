package com.example.frontend.controller.models

data class PaymentMethodBasicDTO (

    val id: kotlin.String,
    val creditCard: kotlin.String,
    val isDefault: kotlin.Boolean? = null
) {
}