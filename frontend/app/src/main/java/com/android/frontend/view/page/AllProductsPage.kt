package com.android.frontend.view.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.frontend.controller.models.ProductDTO
import com.android.frontend.view_models.ProductViewModel
import kotlinx.coroutines.launch

//import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllProductsPage(navController: NavController, productViewModel: ProductViewModel) {
    val products =productViewModel.productsLiveData.observeAsState().value

    LaunchedEffect(key1 = productViewModel) {
        productViewModel.fetchAllProducts()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Products") },
            )
        },


        content = { innerPadding ->
            LazyColumn(
                // consume insets as scaffold doesn't do it by default
                modifier = Modifier.consumeWindowInsets(innerPadding),
                contentPadding = innerPadding
            ) {
                for (productDTO in products ?: emptyList())
                    item {
                        ProductsCard(productDTO , navController, productViewModel)
                    }

            }
        }
    )
}
@Composable
fun ProductsCard(productDTO: ProductDTO, navController: NavController, productViewModel: ProductViewModel) {
    val coroutineScope = rememberCoroutineScope()
    Card (

        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
    ){
        LazyColumn {
            coroutineScope.launch {
                productViewModel.setProduct(productDTO)
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.surface)
                ) {
                    Text(text = productDTO.title)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = productDTO.description)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = productDTO.ingredients)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = productDTO.nutritionalValues)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = productDTO.productPrice.toString())
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = productDTO.deliveryPrice.toString())
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = productDTO.brand.toString())
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = productDTO.productWeight)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = productDTO.quantity.toString())
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = productDTO.availability.toString())
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = productDTO.productCategory.toString())
                    Spacer(modifier = Modifier.height(4.dp))

                    //productDTO.productImages?.let { Text(text = it.joinToString()) }
                }
            }
        }
    }
}
