package com.android.frontend.dto.basic

import com.android.frontend.dto.UserImageDTO

data class UserBasicDTO(
    var id: String,
    var lastName: String,
    var firstName: String,
    var email: String,
    var phoneNumber: String? = null,
    var photoProfile: UserImageDTO? = null,
    var status: UserStatus? = null,
    var role: UserRole? = null
) {
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
}

