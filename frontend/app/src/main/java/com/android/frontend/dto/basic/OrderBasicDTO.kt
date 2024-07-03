package com.android.frontend.dto.basic

import com.android.frontend.dto.CartDTO

data class OrderBasicDTO(
    val id: String,
    val cart: CartDTO
) {
}