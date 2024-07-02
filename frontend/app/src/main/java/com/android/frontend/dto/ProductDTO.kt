package com.android.frontend.dto

import java.math.BigDecimal

data class ProductDTO (

    val id: String,
    val title: String,
    val description: String,
    val ingredients:String,
    val nutritionalValues: String,
    val productPrice: BigDecimal,
    val deliveryPrice: BigDecimal,
    val brand: BrandDTO,
    val productWeight: String,
    val quantity: Int,
    val availability: Availability,
    val productCategory: ProductCategoryDTO,
    val photoProduct: ProductImageDTO? = null,
    val onSale: Boolean,
    val discountedPrice: BigDecimal?
) {


    enum class Availability(val value:String){
        AVAILABLE("AVAILABLE"),
        PENDING("PENDING"),
        UNAVAILABLE("UNAVAILABLE");
    }
}