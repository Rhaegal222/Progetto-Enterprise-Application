package com.android.frontend.dto

import com.android.frontend.dto.basic.UserBasicDTO

data class OrderDTO(
    val id: String,
    val cart: CartDTO,
    val user: UserBasicDTO,
    val deliveryAddress: AddressDTO?,
    val paymentMethod: PaymentMethodDTO?
){
}