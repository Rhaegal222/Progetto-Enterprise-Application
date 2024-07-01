package com.android.frontend.dto.creation

data class AddressCreateDTO (
    val fullName: String,
    val phoneNumber: String,
    val street: String,
    val additionalInfo: String,
    val zipCode: String,
    val city: String,
    val province: String,
    val country: String,
    val isDefault: Boolean
)