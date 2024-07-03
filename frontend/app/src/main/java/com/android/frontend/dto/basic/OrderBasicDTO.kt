package com.android.frontend.dto.basic

import com.android.frontend.dto.CartDTO

data class OrderBasicDTO(
    var id: String,
    var cart: CartDTO
)
