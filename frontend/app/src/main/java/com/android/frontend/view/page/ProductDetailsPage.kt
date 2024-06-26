package com.android.frontend.view.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.frontend.controller.models.ProductDTO

import com.android.frontend.view_models.ProductViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsPage(productViewModel: ProductViewModel, productId: Int) {
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
                item {
                    productDetails.value?.let { product ->
                        ProductDetailsCard(product)
                    }
                }
            }
        }
    )
}

@Composable
fun ProductDetailsCard(product: ProductDTO) {
    Card (
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.surface)
        ) {
            Text(text = product.title)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = product.productPrice.toString()+"€")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = product.brand.name)
            Spacer(modifier = Modifier.height(4.dp))
            //product.productImages?.let { Text(text = it.joinToString()) }
        }
    }
}

