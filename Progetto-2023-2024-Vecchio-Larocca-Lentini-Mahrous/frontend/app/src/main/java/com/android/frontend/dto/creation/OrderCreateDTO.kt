package com.android.frontend.dto.creation

import java.util.UUID

data class OrderCreateDTO(
    val addressId: UUID,
    val paymentMethodId: UUID
)