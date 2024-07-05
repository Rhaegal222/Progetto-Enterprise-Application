package com.android.frontend.view.pages.user.main

import android.util.Log
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.frontend.dto.CartItemDTO
import com.android.frontend.dto.ProductDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.view_models.user.CartViewModel
import kotlinx.coroutines.flow.firstOrNull
import java.math.BigDecimal
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.android.frontend.R
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.view.component.CartItemCard
import com.android.frontend.view.component.ErrorDialog


@Composable
fun CartPage(
    navController: NavController,
    cartViewModel: CartViewModel = viewModel()
) {

    val context = LocalContext.current
    val cartItems by cartViewModel.cartItems.observeAsState()
    val isLoading by cartViewModel.isLoading.observeAsState(false)
    val hasError by cartViewModel.hasError.observeAsState(false)

    LaunchedEffect(Unit) {
        Log.d("DEBUG", "${getCurrentStackTrace()} Loading cart")
        cartViewModel.getCartForLoggedUser(context)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (hasError) {
        ErrorDialog(
            title = stringResource(id = R.string.fetching_error),
            onDismiss = { navController.popBackStack() },
            onRetry = { cartViewModel.getCartForLoggedUser(context) },
            errorMessage = stringResource(id = R.string.cart_load_failed)
        )
    } else {
        cartItems?.let { CartContent(navController, it, cartViewModel) }
    }
}

@Composable
fun CartContent(navController: NavController, cart: List<CartItemDTO>, cartViewModel: CartViewModel) {
    val context = LocalContext.current
    Column {
        cart.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp, 0.dp)
            ) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(it) { cartItem ->
                        val productDetails by cartViewModel.getProductDetails(
                            context,
                            cartItem.productId
                        ).collectAsState(null)
                        productDetails?.let { product ->
                            CartItemCard(cartItem, product, cartViewModel)
                        }
                    }
                }
                Column {
                    CartSummary(cart, cartViewModel)
                    Row {
                        Button(
                            onClick = {
                                cartViewModel.clearCart(context)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Text("Svuota Carrello")
                        }
                        Button(
                            onClick = {
                                navController.navigate(Navigation.CheckoutPage.route)
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Text("Complete Order")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartSummary(cartItems: List<CartItemDTO>, cartViewModel: CartViewModel) {

    val context = LocalContext.current
    var total by remember { mutableStateOf(BigDecimal.ZERO) }

    LaunchedEffect(cartItems) {
        total = BigDecimal.ZERO
        for (cartItem in cartItems) {
            val product = cartViewModel.getProductDetails(context, cartItem.productId).firstOrNull()
            product?.let {
                total += (it.price * BigDecimal(cartItem.quantity)) + it.shippingCost
            }
        }
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
