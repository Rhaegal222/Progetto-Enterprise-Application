package com.android.frontend.dto.creation

import com.android.frontend.dto.BrandDTO
import com.android.frontend.dto.ProductDTO
import com.android.frontend.dto.ProductImageDTO
import java.math.BigDecimal

data class ProductCreateDTO(
        val title: String,
        val description: String? = null,
        val ingredients: String? = null,
        val nutritionalValues: String? = null,
        val productPrice: BigDecimal,
        val deliveryPrice: BigDecimal,
        val productWeight: String? = null,
        val availability: ProductDTO.Availability,
        val quantity: Int,
        val brand: BrandDTO,
        val productCategory: ProductCategoryDTO,
        val photoProduct: ProductImageDTO? = null,
        val onSale: Boolean,
        val discountedPrice: BigDecimal? = null
)
