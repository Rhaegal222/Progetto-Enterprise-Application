package com.android.frontend.controller.models

data class CartCreateDTO(
    val userId : kotlin.String,
    val productId : kotlin.String,
    val quantity : kotlin.Int
)
