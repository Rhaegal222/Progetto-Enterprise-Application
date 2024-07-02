package com.android.frontend.dto.creation

import com.android.frontend.dto.BrandDTO
import com.android.frontend.dto.ProductCategoryDTO
import com.android.frontend.dto.ProductDTO
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
        val imageUrl: String? = null,
        val onSale: Boolean,
        val discountedPrice: BigDecimal? = null
)
