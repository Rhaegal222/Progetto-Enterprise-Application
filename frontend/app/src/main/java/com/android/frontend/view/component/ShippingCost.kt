package com.android.frontend.view.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.frontend.R
import com.android.frontend.dto.ProductDTO

@Composable
fun ShippingCost(productDTO: ProductDTO) {
    Row {
        Text(
            text = stringResource(id = R.string.shipping_price),
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${productDTO.shippingCost} €",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}