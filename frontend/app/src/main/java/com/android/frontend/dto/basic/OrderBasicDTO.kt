package com.android.frontend.dto.basic

import com.android.frontend.dto.CartItemDTO
import com.android.frontend.dto.OrderDTO

data class OrderBasicDTO(
    var id: String,
    var items: List<CartItemDTO>,
    var status: OrderDTO.OrderStatus,
)
