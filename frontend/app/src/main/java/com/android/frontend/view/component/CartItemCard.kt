package com.android.frontend.view.component

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.android.frontend.dto.CartItemDTO
import com.android.frontend.dto.ProductDTO
import com.android.frontend.view_models.user.CartViewModel

@Composable
fun CartItemCard(cartItem: CartItemDTO, product: ProductDTO, cartViewModel: CartViewModel, imageUri: Uri?) {
    val context = LocalContext.current
    var quantity by remember { mutableStateOf(cartItem.quantity) }
    var isUpdating by remember { mutableStateOf(false) }

    val price = if (product.onSale) product.salePrice ?: product.price else product.price

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Product: ${product.name}", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
                Text(text = "Product Price: $price", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
                Text(text = "Delivery Price: ${product.shippingCost}", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            if (quantity > 1 && !isUpdating) {
                                isUpdating = true
                                cartViewModel.updateCartItem(cartItem.id, quantity - 1, context)
                            }
                        },
                        enabled = !isUpdating
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease Quantity")
                    }
                    Text(
                        text = quantity.toString(),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(
                        onClick = {
                            if (!isUpdating) {
                                isUpdating = true
                                cartViewModel.updateCartItem(cartItem.id, quantity + 1, context)
                            }
                        },
                        enabled = !isUpdating
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase Quantity")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            if (!isUpdating) {
                                isUpdating = true
                                cartViewModel.removeCartItem(cartItem.id, context)
                            }
                        },
                        enabled = !isUpdating
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove Item")
                    }
                }
            }
            imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(start = 16.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

    val cart by cartViewModel.cart.observeAsState()
    LaunchedEffect(cart) {
        cart?.items?.find { it.id == cartItem.id }?.let {
            quantity = it.quantity
        }
        isUpdating = false
    }
}
