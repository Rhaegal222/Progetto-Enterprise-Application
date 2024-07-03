package com.android.frontend.dto.creation

import com.android.frontend.dto.AddressDTO
import com.android.frontend.dto.CartDTO
import com.android.frontend.dto.PaymentMethodDTO

data class OrderCreateDTO(
    val cart: CartDTO,
    val deliveryAddress: AddressDTO,
    val paymentMethod: PaymentMethodDTO
) {
}