package com.android.frontend.dto

import java.math.BigDecimal

data class ProductDTO(
    var id: Long,
    var name: String,
    var description: String? = null,
    var ingredients: String? = null,
    var nutritionalValues: String? = null,
    var weight: String,
    var quantity: Int,
    var price: BigDecimal,
    var shippingCost: BigDecimal,
    var availability: Availability,
    var brand: BrandDTO,
    var category: CategoryDTO,
    var image: ProductImageDTO? = null,
    var onSale: Boolean,
    var discountedPrice: BigDecimal? = null
) {


    enum class Availability{
        IN_STOCK,
        OUT_OF_STOCK,
        PRE_ORDER,
        DISCONTINUED
    }
}