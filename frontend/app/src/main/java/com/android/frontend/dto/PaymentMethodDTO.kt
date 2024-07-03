package com.android.frontend.dto

data class PaymentMethodDTO(
    var id: String? = null,
    var cardNumber: String,
    var expireMonth: String,
    var expireYear: String,
    var isDefault: Boolean,
    var owner: String
)