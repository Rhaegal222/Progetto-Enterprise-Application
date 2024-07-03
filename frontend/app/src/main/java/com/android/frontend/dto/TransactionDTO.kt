package com.android.frontend.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionDTO(
    var id: String,
    var creationTime: LocalDateTime,
    var amount: BigDecimal,
    var transactionState: TransactionState,
    var paymentMethod: String,
    var paymentMethodOwner: String
){
    enum class TransactionState {
        REJECTED,
        COMPLETED
    }
}
