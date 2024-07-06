package com.android.frontend.dto

import java.math.BigDecimal

data class ProductUpdateRequest(
    var name: String,
    var description: String,
    var ingredients: String,
    var nutritionalValues: String,
    var weight: String,
    var quantity: Int,
    var price: BigDecimal,
    var shippingCost: BigDecimal,
    var availability: ProductDTO.Availability,
    var brand: BrandDTO,
    var category: CategoryDTO,
    var onSale: Boolean,
    var salePrice: BigDecimal
)
