package com.android.frontend.dto

data class UserDTO (
    var id: String,
    var firstName: String,
    var lastName: String,
    var username: String,
    var email: String,
    var phoneNumber: String? = null,
    var image: UserImageDTO? = null,
    var role: UserRole,
    var status: UserStatus,
    var provider: Provider,
    var cart: CartDTO
){

    enum class UserRole {
        USER,
        ADMIN,
        SUPPLIER
    }

    enum class UserStatus {
        ACTIVE,
        INACTIVE,
        BANNED,
        SUSPENDED,
        CANCELLED
    }

    enum class Provider {
        LOCAL,
        KEYCLOAK,
        GOOGLE
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
        if (image != other.image) return false
        if (provider != other.provider) return false
        if (status != other.status) return false
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
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + provider.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + role.hashCode()
        return result
    }
}