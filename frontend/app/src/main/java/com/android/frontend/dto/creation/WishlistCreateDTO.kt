package com.android.frontend.dto.creation

import com.android.frontend.dto.WishlistDTO

data class WishlistCreateDTO(
    val wishlistName: String,
    val wishlistVisibility: WishlistDTO.WishlistVisibility,
)
