package com.android.frontend.view.pages.user.browse

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.android.frontend.R
import com.android.frontend.persistence.SecurePreferences
import com.android.frontend.dto.ProductDTO
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.navigation.Navigation
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.view_models.user.CartViewModel
import com.android.frontend.view_models.user.ProductViewModel

enum class SortOption(val displayName: String) {
    ALPHABETICAL("Alphabetical"),
    REVERSE_ALPHABETICAL("Reverse Alphabetical"),
    PRICE_ASCENDING("Price: Low to High"),
    PRICE_DESCENDING("Price: High to Low")
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllProductsPage(navController: NavController, productViewModel: ProductViewModel, cartViewModel: CartViewModel) {
    val context = LocalContext.current
    val products by productViewModel.productsLiveData.observeAsState()
    val productImages by productViewModel.productImagesLiveData.observeAsState()
    var isLoading by remember { mutableStateOf(true) }
    var selectedSortOption by remember { mutableStateOf(SortOption.ALPHABETICAL) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        productViewModel.fetchAllProducts(context)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Products") },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Sort By", modifier = Modifier.padding(end = 8.dp))
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Sort")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                SortOption.entries.forEach { option ->
                                    DropdownMenuItem(
                                        onClick = {
                                            selectedSortOption = option
                                            expanded = false
                                        },
                                        text = { Text(option.displayName) }
                                    )
                                }
                            }
                        }
                    }
                }
            )
        },
        content = { innerPadding ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val sortedProducts = when (selectedSortOption) {
                    SortOption.ALPHABETICAL -> products?.sortedBy { it.name }
                    SortOption.REVERSE_ALPHABETICAL -> products?.sortedByDescending { it.name }
                    SortOption.PRICE_ASCENDING -> products?.sortedBy { it.name }
                    SortOption.PRICE_DESCENDING -> products?.sortedByDescending { it.price }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(sortedProducts ?: emptyList()) { productDTO ->
                        ProductsCard(productDTO, navController, productViewModel, cartViewModel, productImages?.get(productDTO.id))
                    }
                }
            }
        }
    )
}

@Composable
fun ProductsCard(
    productDTO: ProductDTO,
    navController: NavController,
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    imageUri: Uri?
) {
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
                CurrentDataUtils.currentProductImageUri = imageUri
                val route = if (productDTO.onSale) {
                    Navigation.SaleProductDetailsPage.route
                } else {
                    Navigation.ProductDetailsPage.route
                }
                navController.navigate(route)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            val painter = if (imageUri != null) {
                rememberAsyncImagePainter(model = imageUri)
            } else {
                painterResource(id = R.drawable.product_placeholder)
            }
            Image(
                painter = painter,
                contentDescription = "Product Image",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(80.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = productDTO.name,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 2,
                modifier = Modifier.heightIn(min = 40.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${productDTO.brand.name}",
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (productDTO.onSale && productDTO.discountedPrice != null) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${productDTO.price}€",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        fontSize = 18.sp,
                        textDecoration = TextDecoration.LineThrough
                    )

                    Text(
                        text = "${productDTO.discountedPrice}€",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        fontSize = 18.sp
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${productDTO.price}€",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                colors = ButtonColorScheme.buttonColors(),
                modifier = Modifier.size(46.dp),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(10.dp),
                onClick = {
                    cartViewModel.addProductToCart(productDTO.id, 1, context)
                }
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    }
}
