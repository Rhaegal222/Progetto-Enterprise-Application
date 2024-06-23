package com.example.frontend.controller.models

data class UserDTO (

    val id: kotlin.String, //
    val firstName: kotlin.String, //
    val lastName: kotlin.String, //
    val username: kotlin.String, //
    val email: kotlin.String, //
    val phoneNumber: String? = null, //
    val photoProfile: UserImageDTO? = null, //
    val provider: Provider, //
    val status: Status, //
    val addresses: kotlin.Array<AddressDTO>? = null, //
    val paymentMethods: kotlin.Array<PaymentMethodDTO>? = null, //
    val role: Role, //
) {
    /**
     *
     * Values: LOCAL,GOOGLE
     */
    enum class Provider(val value: kotlin.String){
        LOCAL("LOCAL"),
        GOOGLE("GOOGLE");
    }
    /**
     *
     * Values: ACTIVE,BANNED,HIDDEN,CANCELLED
     */
    enum class Status(val value: kotlin.String){
        ACTIVE("ACTIVE"),
        BANNED("BANNED"),
        HIDDEN("HIDDEN"),
        CANCELLED("CANCELLED");
    }
    /**
     *
     * Values: ADMIN,USER
     */
    enum class Role(val value: kotlin.String){
        ADMIN("ADMIN"),
        USER("USER");
    }
}