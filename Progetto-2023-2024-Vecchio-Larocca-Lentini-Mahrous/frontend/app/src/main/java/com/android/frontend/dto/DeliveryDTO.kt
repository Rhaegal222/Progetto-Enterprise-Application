package com.android.frontend.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class DeliveryDTO(
    var id: String,
    var sendTime: LocalDateTime,
    var deliveredTime: LocalDateTime? = null,
    var deliveryCost: BigDecimal? = null,
    var shipper: String? = null,
    var deliveryStatus: DeliveryStatus,
    var senderAddress: AddressDTO,
    var receiverAddress: AddressDTO
){
    enum class DeliveryStatus {
        SHIPPED,
        DELIVERED
    }
}