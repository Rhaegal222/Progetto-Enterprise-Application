package com.android.frontend.controller.models

data class PaymentMethodBasicDTO (

    val id: kotlin.String,
    val cardNumber: kotlin.String,
    val isDefault: kotlin.Boolean? = null
) {
}