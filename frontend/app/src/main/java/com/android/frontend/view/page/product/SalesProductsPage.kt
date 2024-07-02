package com.android.frontend.view.page.product

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.frontend.view_models.CartViewModel
import com.android.frontend.view_models.ProductViewModel
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.frontend.R
import com.android.frontend.persistence.SecurePreferences
import com.android.frontend.dto.ProductDTO
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.navigation.Navigation
import com.android.frontend.ui.theme.colors.ButtonColorScheme

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesProductsPage(navController: NavController, productViewModel: ProductViewModel, cartViewModel: CartViewModel) {

    val context = LocalContext.current
    val products = productViewModel.productsLiveData.observeAsState().value

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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(products ?: emptyList()) { productDTO ->
                    //SalesProductCard(productDTO, navController, productViewModel, cartViewModel)
                    ProductsCard(productDTO, navController, productViewModel, cartViewModel)
                }
            }
        }
    )
}
