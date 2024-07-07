package com.android.frontend.view.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.frontend.dto.ProductDTO
@Composable
fun OnSaleProductCard(
    product: ProductDTO,
    onProductClick: (ProductDTO) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onProductClick(product) }
    ) {
        Column {

        }
    }
}