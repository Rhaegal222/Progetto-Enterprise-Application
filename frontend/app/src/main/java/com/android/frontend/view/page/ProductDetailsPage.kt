package com.android.frontend.view.page


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.frontend.view_models.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsPage(productViewModel: ProductViewModel, productId: String) {
    val productDetails = productViewModel.productDetails.observeAsState()
    LaunchedEffect(key1 = productViewModel) {
        productViewModel.getProductDetails(productId)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
            )
        },
        content = { innerPadding ->
            LazyColumn (
                modifier = Modifier.consumeWindowInsets(innerPadding),
                contentPadding = innerPadding
            ) {
                productDetails.value?.let { product ->
                    item {
                        Column {
                            Text(text = product.title)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = product.productPrice.toString() + "€")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = product.brand.name)
                            Spacer(modifier = Modifier.height(4.dp))
                            // Add more product details here as desired
                        }
                    }
                }
            }
        }
    )
}

