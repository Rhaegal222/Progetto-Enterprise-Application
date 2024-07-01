package com.android.frontend.dto.creation

data class AddressCreateDTO (
    val firstName: String,
    val lastName: String,
    val country: String,
    val city: String,
    val street: String,
    val zipCode: String,
    val isDefault: Boolean
)