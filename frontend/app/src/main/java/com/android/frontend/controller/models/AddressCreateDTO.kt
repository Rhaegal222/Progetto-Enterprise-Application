package com.android.frontend.controller.models

data class AddressCreateDTO (

    val header: kotlin.String,
    val country: kotlin.String,
    val city: kotlin.String,
    val street: kotlin.String,
    val zipCode: kotlin.String,
    val isDefault: kotlin.Boolean
) {
}