package com.android.frontend.dto

data class AddressDTO(
    var id: String,
    var fullName: String,
    var phoneNumber: String,
    var street: String,
    var additionalInfo: String,
    var postalCode: String,
    var province: String,
    var city: String,
    var country: String,
    var isDefault: Boolean
)
