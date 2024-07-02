package com.android.frontend.dto.creation

data class WishlistCreateDTO (
    val wishlistName: String,
    val visibility: Visibility

){
    enum class Visibility(val value:String){
    PUBLIC("PUBLIC"),
    PRIVATE("PRIVATE"),
    SHARED("SHARED");
}}
