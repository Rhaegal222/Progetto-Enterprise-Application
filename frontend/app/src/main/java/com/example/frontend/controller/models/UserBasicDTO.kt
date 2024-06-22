package com.example.frontend.controller.models

data class UserBasicDTO (

    var id: kotlin.String,
    val username: kotlin.String,
    val bio: kotlin.String? = null,
    val photoProfile: UserImageDTO? = null,
) {
}