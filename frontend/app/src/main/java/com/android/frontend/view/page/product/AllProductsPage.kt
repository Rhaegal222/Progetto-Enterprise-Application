package com.android.frontend.view.page.product

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.frontend.controller.models.ProductDTO
import com.android.frontend.model.CurrentDataUtils
import com.android.frontend.navigation.Navigation
import com.android.frontend.view_models.ProductViewModel

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllProductsPage(navController: NavController, productViewModel: ProductViewModel) {
    val products = productViewModel.productsLiveData.observeAsState().value
    productViewModel.fetchAllProducts()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Products") },
            )
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier.consumeWindowInsets(innerPadding),
                contentPadding = innerPadding
            ) {
                items(products ?: emptyList()) { productDTO ->
                    ProductsCard(productDTO , navController, productViewModel)
                }
            }
        }
    )
}

@Composable
fun ProductsCard(productDTO: ProductDTO, navController: NavController, productViewModel: ProductViewModel) {
    Card (

        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
            .clickable {

                CurrentDataUtils.currentProductId = productDTO.id
                Log.d("AllProductsPage", "Current product id: ${CurrentDataUtils.currentProductId}")
                navController.navigate(Navigation.ProductDetailsPage.route)
            }
    ){
        LazyColumn {
                productViewModel.setProduct(productDTO)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.surface)
                ) {
                    Text(
                        text = productDTO.title,
                        modifier = Modifier.clickable {
                            //navController.navigate("product/${productDTO.id}")
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = productDTO.productPrice.toString()+"€")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = productDTO.brand.name)
                    Spacer(modifier = Modifier.height(4.dp))
                    //productDTO.productImages?.let { Text(text = it.joinToString()) }
                }
            }
        }
    }
}
