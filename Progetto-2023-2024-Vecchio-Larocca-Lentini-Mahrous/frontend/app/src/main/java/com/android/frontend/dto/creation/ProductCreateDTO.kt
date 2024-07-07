package com.android.frontend.dto.creation

import com.android.frontend.dto.BrandDTO
import com.android.frontend.dto.CategoryDTO
import com.android.frontend.dto.ProductDTO
import com.android.frontend.dto.ProductImageDTO
import java.math.BigDecimal

data class ProductCreateDTO(
        val name: String ?= null,
        val description: String ?= null,
        val ingredients:String ?= null,
        val nutritionalValues: String ?= null,
        val weight: String ?= null,
        val quantity: Int ?= null,
        val price: BigDecimal ?= null,
        val shippingCost: BigDecimal ?= null,
        val availability: ProductDTO.Availability ?= null,
        val brand: BrandDTO ?= null,
        val category: CategoryDTO ?= null,
        val image: ProductImageDTO ?= null,
        val onSale: Boolean ?= null,
        val salePrice: BigDecimal ?= null
)
