package com.android.frontend.dto.creation

data class CartCreateDTO(
    val userId : kotlin.String,
    val productId : kotlin.String,
    val quantity : kotlin.Int
)
