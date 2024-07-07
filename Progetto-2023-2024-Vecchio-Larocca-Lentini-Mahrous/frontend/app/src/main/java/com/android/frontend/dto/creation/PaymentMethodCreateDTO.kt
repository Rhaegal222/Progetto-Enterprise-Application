package com.android.frontend.dto.creation

data class PaymentMethodCreateDTO(
    val cardNumber: String,
    val expireMonth: String,
    val expireYear: String,
    val owner: String,
    val isDefault: Boolean,
)