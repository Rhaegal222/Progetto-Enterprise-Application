package com.android.frontend.controller.models

data class UserUpdateRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val username: String
)
