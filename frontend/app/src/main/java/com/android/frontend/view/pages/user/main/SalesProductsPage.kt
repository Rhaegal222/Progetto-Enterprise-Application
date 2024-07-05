package com.android.frontend.view.pages.user.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.frontend.view.component.ProductCard
import com.android.frontend.view_models.user.CartViewModel
import com.android.frontend.view_models.user.ProductViewModel

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesProductsPage(
    navController: NavController,
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {

    val context = LocalContext.current
    val products = productViewModel.productsLiveData.observeAsState().value
    val productImages = productViewModel.productImagesLiveData.observeAsState().value

    LaunchedEffect(Unit) {
        productViewModel.fetchSalesProducts(context)
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Offerte del giorno") },
            )
        },
        content = { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(products ?: emptyList()) { productDTO ->
                    ProductCard(
                        productDTO,
                        navController,
                        cartViewModel,
                        WishlistView(),
                        productImages?.get(productDTO.id)
                    )
                }
            }
        }
    )
}
