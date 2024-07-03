package com.android.frontend.dto

import java.time.LocalDateTime

data class WishlistDTO (
    var id: String,
    var wishlistName: String,
    var wishlistVisibility: WishlistVisibility,
    var products: List<ProductDTO>? = null,
    var createdAt: LocalDateTime? = null
) {

    enum class WishlistVisibility{
        PUBLIC,
        PRIVATE,
        SHARED
    }
}



