package com.example.frontend.controller.models

data class ProductDTO (

    val id: kotlin.String,
    val title: kotlin.String,
    val description: kotlin.String? = null,
    val descriptionBrand: kotlin.String? = null,
    val ingredients: kotlin.String? = null,
    val nutritionalValues: kotlin.String? = null,
    val productCost: CustomMoneyDTO,
    val deliveryCost: CustomMoneyDTO,
    val brand: kotlin.String? = null,
    val productSize: ProductSize,
    val availability: Availability,
    val productCategory: ProductCategoryDTO,
    val usersThatLiked: kotlin.Array<UserBasicDTO>? = null,
    val productImages: kotlin.Array<ProductImageDTO>? = null,
) {
    enum class ProductSize(val value: kotlin.String){ //
        BIG("BIG"),
        MEDIUM("MEDIUM"),
        SMALL("SMALL");
    }

    enum class Availability(val value: kotlin.String){ //
        AVAILABLE("AVAILABLE"),
        PENDING("PENDING"),
        UNAVAILABLE("UNAVAILABLE");
    }
}