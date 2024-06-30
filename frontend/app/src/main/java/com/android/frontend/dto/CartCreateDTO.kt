package com.android.frontend.dto

data class CartCreateDTO(
    val userId : kotlin.String,
    val productId : kotlin.String,
    val quantity : kotlin.Int
)
