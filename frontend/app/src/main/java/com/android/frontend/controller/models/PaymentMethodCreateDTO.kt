package com.android.frontend.controller.models

import java.time.LocalDate

data class PaymentMethodCreateDTO(
    val cardNumber: String,
    val expiryDate: String,
    val owner: String,
    val isDefault: Boolean
) {
}