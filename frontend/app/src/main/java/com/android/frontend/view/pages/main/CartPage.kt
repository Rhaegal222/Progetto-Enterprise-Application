package com.android.frontend.view.pages.main

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.frontend.dto.CartItemDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.view_models.user.CartViewModel
import java.math.BigDecimal

@Composable
fun CartPage(cartViewModel: CartViewModel, navController: NavController) {
    val context = LocalContext.current
    val cart by cartViewModel.cart.collectAsState()

    LaunchedEffect(Unit) {
        cartViewModel.loadCart(context)
    }

    cart?.let {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp, 0.dp)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(it.cartItems) { cartItem ->
                    CartItemCard(cartItem, cartViewModel, context)
                }
            }
            CartSummary(cartItems = it.cartItems)
            Button(
                onClick = {
                    navController.navigate(Navigation.CheckoutPage.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Complete Order")
            }
        }
    }
}

@Composable
fun CartItemCard(cartItem: CartItemDTO, cartViewModel: CartViewModel, context: Context) {
    var quantity by remember { mutableStateOf(cartItem.quantity) }
    val price = if (cartItem.onSale) cartItem.discountedPrice ?: cartItem.productPrice else cartItem.productPrice

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Product: ${cartItem.productName}", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Product Price: $price", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Delivery Price: ${cartItem.deliveryPrice}", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (quantity > 1) {
                        quantity--
                        cartViewModel.updateCartItem(cartItem.id, quantity, context)
                    }
                }) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease Quantity")
                }
                Text(text = quantity.toString(), fontSize = 16.sp, modifier = Modifier.padding(horizontal = 8.dp))
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

@Composable
fun CartSummary(cartItems: List<CartItemDTO>) {
    val total = cartItems.fold(BigDecimal.ZERO) { acc, cartItem ->
        acc + (cartItem.productPrice * BigDecimal(cartItem.quantity)) + cartItem.deliveryPrice
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Cart Summary", fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Total Price: $total", fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}
