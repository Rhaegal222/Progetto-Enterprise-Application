package com.android.frontend.view.page.product

import android.annotation.SuppressLint
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.frontend.model.CurrentDataUtils
import com.android.frontend.view_models.ProductViewModel

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsPage(productViewModel: ProductViewModel) {
    val productId = CurrentDataUtils.currentProductId
    val productDetails = productViewModel.productDetailsLiveData.observeAsState().value
    productViewModel.getProductDetails(productId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
            )
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier.consumeWindowInsets(innerPadding),
                contentPadding = innerPadding
            ) {
                item {
                    Text(text = "Product Details")
                    Spacer(modifier = Modifier.height(16.dp))
                    Log.d("ProductDetailsPage", "Product details: $productDetails")
                    if (productDetails != null) {
                        Column {
                            Text(text = "Product Name: ${productDetails.title}")
                            Text(text = "Product Category: ${productDetails.productCategory.name}")
                        }
                    }
                }
            }
        }
    )
}

