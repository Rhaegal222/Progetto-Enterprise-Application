package com.android.frontend.controller.models

data class AddressDTO (

    val id: kotlin.String,
    val header: kotlin.String,
    val country: kotlin.String,
    val city: kotlin.String,
    val street: kotlin.String,
    val zipCode: kotlin.String,
    val phoneNumber: kotlin.String,
    val isDefault: kotlin.Boolean
) {
}