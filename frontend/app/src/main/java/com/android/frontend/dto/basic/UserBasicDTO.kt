package com.android.frontend.dto.basic

import com.android.frontend.dto.UserDTO
import com.android.frontend.dto.UserImageDTO

data class UserBasicDTO(
    var id: String,
    var lastName: String,
    var firstName: String,
    var email: String,
    var phoneNumber: String? = null,
    var photoProfile: UserImageDTO? = null,
    var status: UserDTO.UserStatus? = null,
    var role: UserDTO.UserRole? = null
)

