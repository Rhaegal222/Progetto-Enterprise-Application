package com.android.frontend.dto.creation

data class PaymentMethodCreateDTO(
    val cardNumber: String,
    val expireMonth: kotlin.String,
    val expireYear: kotlin.String,
    val owner: String,
    val isDefault: Boolean
) {
}