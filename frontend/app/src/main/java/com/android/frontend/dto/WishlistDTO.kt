package com.android.frontend.dto

data class WishlistDTO (
    val id: Long,
    val wishlistName: String,
    val userId: String,
    val visibility: Visibility,
    val products: List<ProductDTO>? = null
) {

    enum class Visibility(val value:String){
        PUBLIC("PUBLIC"),
        PRIVATE("PRIVATE"),
        SHARED("SHARED");
    }
}



