package com.android.frontend.controller.models

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
    val role: UserRole, //
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
    enum class UserRole(val value: kotlin.String){
        ADMIN("ADMIN"),
        USER("USER");
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserDTO

        if (id != other.id) return false
        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (username != other.username) return false
        if (email != other.email) return false
        if (phoneNumber != other.phoneNumber) return false
        if (photoProfile != other.photoProfile) return false
        if (provider != other.provider) return false
        if (status != other.status) return false
        if (addresses != null) {
            if (other.addresses == null) return false
            if (!addresses.contentEquals(other.addresses)) return false
        } else if (other.addresses != null) return false
        if (paymentMethods != null) {
            if (other.paymentMethods == null) return false
            if (!paymentMethods.contentEquals(other.paymentMethods)) return false
        } else if (other.paymentMethods != null) return false
        if (role != other.role) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + (phoneNumber?.hashCode() ?: 0)
        result = 31 * result + (photoProfile?.hashCode() ?: 0)
        result = 31 * result + provider.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + (addresses?.contentHashCode() ?: 0)
        result = 31 * result + (paymentMethods?.contentHashCode() ?: 0)
        result = 31 * result + role.hashCode()
        return result
    }
}