package com.android.frontend.dto.creation

import com.android.frontend.dto.AddressDTO
import com.android.frontend.dto.CartDTO
import com.android.frontend.dto.PaymentMethodDTO

data class OrderCreateDTO(
    val items : List<CartDTO>,
    val addressId : String,
    val paymentMethod : String,
    val userId : String
)