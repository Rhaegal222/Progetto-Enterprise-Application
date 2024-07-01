package com.android.frontend.dto.basic

import com.android.frontend.dto.UserImageDTO

data class UserBasicDTO (

    val id: kotlin.String,
    val lastName: kotlin.String,
    val firstName: kotlin.String,
    val photoProfile: UserImageDTO? = null,
    val status: kotlin.String? = null,
) {
}