package com.android.frontend.view.page.product

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.frontend.R
import com.android.frontend.persistence.SecurePreferences
import com.android.frontend.dto.ProductDTO
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.navigation.Navigation
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.view_models.user.CartViewModel
import com.android.frontend.view_models.user.ProductViewModel

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllProductsPage(navController: NavController, productViewModel: ProductViewModel, cartViewModel: CartViewModel) {

    val context = LocalContext.current

    val products = productViewModel.productsLiveData.observeAsState().value

    LaunchedEffect(Unit) {
        productViewModel.fetchAllProducts(context)
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("All Products") },
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
                    ProductsCard(productDTO, navController, productViewModel, cartViewModel)                }
            }
        }
    )
}

@Composable
fun ProductsCard(productDTO: ProductDTO, navController: NavController, productViewModel: ProductViewModel, cartViewModel: CartViewModel) {
    val context = LocalContext.current
    val userId = SecurePreferences.getUser(context)?.id ?: ""

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .height(250.dp)
            .clickable {
                CurrentDataUtils.currentProductId = productDTO.id
                navController.navigate(Navigation.ProductDetailsPage.route)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.product_placeholder), // Usa l'immagine del prodotto reale
                contentDescription = "Product Image",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(80.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = productDTO.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2,
                modifier = Modifier.heightIn(min = 40.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = productDTO.brand.name,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${productDTO.productPrice}€",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    fontSize = 13.sp
                )

                Button(
                    colors = ButtonColorScheme.buttonColors(),
                    modifier = Modifier.size(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = {
                        cartViewModel.addProductToCart(userId, productDTO.id, 1, context)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}