package com.android.frontend.view.page.product


import android.util.Log
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.frontend.controller.models.ProductDTO
import com.android.frontend.model.CurrentDataUtils
import com.android.frontend.navigation.Navigation
import com.android.frontend.view.page.HomePage
import com.android.frontend.view_models.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsPage(productViewModel: ProductViewModel) {
    val productId = CurrentDataUtils.currentProductId
    val productDetails = productViewModel.productDetails.observeAsState().value
    ProductViewModel().getProductDetails(productId)
    Log.d("ProductDetailsPage", "Fetching product details for id: $productId")


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
                productDetails?.let { product ->
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

