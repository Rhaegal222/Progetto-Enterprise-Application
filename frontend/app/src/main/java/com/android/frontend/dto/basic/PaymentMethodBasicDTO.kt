package com.android.frontend.dto.basic

data class PaymentMethodBasicDTO(
    var id: String,
    var cardNumber: String,
    var isDefault: Boolean
)
