package com.android.frontend.dto.basic

import com.android.frontend.dto.UserDTO
import com.android.frontend.dto.UserImageDTO

data class UserBasicDTO(
    var id: String,
    var lastName: String,
    var firstName: String,
    var email: String,
    var phoneNumber: String,
    var photoProfile: UserImageDTO,
    var status: UserDTO.UserStatus,
    var role: UserDTO.UserRole
)

