package com.android.frontend.controller.models

data class PaymentMethodDTO (

    val id: kotlin.String,
    val cardNumber: kotlin.String,
    val expireMonth: kotlin.String,
    val expireYear: kotlin.String,
    val owner: kotlin.String,
    val isDefault: kotlin.Boolean
){
}