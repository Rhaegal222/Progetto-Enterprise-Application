package com.android.frontend.dto.creation

import com.android.frontend.dto.CartItemDTO

data class CartCreateDTO(
    val userId : kotlin.String,
    val items: List<CartItemDTO>
)
