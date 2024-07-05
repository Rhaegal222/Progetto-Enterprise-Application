package com.android.frontend.view.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.frontend.dto.CartItemDTO
import com.android.frontend.dto.ProductDTO
import com.android.frontend.view_models.user.CartViewModel

@Composable
fun CartItemCard(cartItem: CartItemDTO, product: ProductDTO, cartViewModel: CartViewModel) {

    val context = LocalContext.current

    var quantity by remember { mutableIntStateOf(cartItem.quantity) }
    val price = if (product.onSale) product.salePrice ?: product.price else product.price

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Product: ${product.name}", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Product Price: $price", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Delivery Price: ${product.shippingCost}", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (quantity > 1) {
                        quantity--
                        cartViewModel.updateCartItem(cartItem.id, quantity, context)
                    }
                }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease Quantity")
                }
                Text(
                    text = quantity.toString(),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(onClick = {
                    quantity++
                    cartViewModel.updateCartItem(cartItem.id, quantity, context)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase Quantity")
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    cartViewModel.removeCartItem(cartItem.id, context)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Item")
                }
            }
        }
    }
}