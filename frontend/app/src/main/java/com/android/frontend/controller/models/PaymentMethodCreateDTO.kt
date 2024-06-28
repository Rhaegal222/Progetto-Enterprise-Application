package com.android.frontend.controller.models

import java.time.LocalDate

data class PaymentMethodCreateDTO(
    val cardNumber: String,
    val expireMonth: kotlin.String,
    val expireYear: kotlin.String,
    val owner: String,
    val isDefault: Boolean
) {
}