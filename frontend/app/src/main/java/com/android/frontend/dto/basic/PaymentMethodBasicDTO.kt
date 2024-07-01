package com.android.frontend.dto.basic

data class PaymentMethodBasicDTO (

    val id: kotlin.String,
    val cardNumber: kotlin.String,
    val isDefault: kotlin.Boolean? = null
) {
}