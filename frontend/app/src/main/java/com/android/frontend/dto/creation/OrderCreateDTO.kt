package com.android.frontend.dto.creation

import com.android.frontend.dto.AddressDTO
import com.android.frontend.dto.CartDTO
import com.android.frontend.dto.CartItemDTO
import com.android.frontend.dto.PaymentMethodDTO
import java.util.UUID

data class OrderCreateDTO(
    val items : List<CartItemDTO>,
    val addressId : UUID,
    val paymentMethod : UUID,
    val userId : UUID
)