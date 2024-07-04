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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
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
fun ProductsPage(navController: NavController, cartViewModel: CartViewModel, productViewModel: ProductViewModel = viewModel()) {
    val context = LocalContext.current
    val products by productViewModel.productsLiveData.observeAsState()
    val productImages by productViewModel.productImagesLiveData.observeAsState()
    var isLoading by remember { mutableStateOf(true) }
    var selectedSortOption by remember { mutableStateOf(SortOption.ALPHABETICAL) }
    var expandedSort by remember { mutableStateOf(false) }
    var expandedFilter by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var dialogInput by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        productViewModel.fetchAllProducts(context)
        isLoading = false
    }

    val categoryString = stringResource(id = R.string.category)
    val brandString = stringResource(id = R.string.brand)
    val priceRangeString = stringResource(id = R.string.price_range)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.all_products)) },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(id = R.string.sort_by), modifier = Modifier.padding(end = 8.dp))
                        Box {
                            IconButton(onClick = { expandedSort = true }) {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = stringResource(id = R.string.sort))
                            }
                            DropdownMenu(
                                expanded = expandedSort,
                                onDismissRequest = { expandedSort = false }
                            ) {
                                SortOption.values().forEach { option ->
                                    DropdownMenuItem(
                                        onClick = {
                                            selectedSortOption = option
                                            expandedSort = false
                                        },
                                        text = { Text(option.displayName) }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(stringResource(id = R.string.filter_by), modifier = Modifier.padding(end = 8.dp))
                        Box {
                            IconButton(onClick = { expandedFilter = true }) {
                                Icon(Icons.Filled.FilterList, contentDescription = stringResource(id = R.string.filter))
                            }
                            DropdownMenu(
                                expanded = expandedFilter,
                                onDismissRequest = { expandedFilter = false }
                            ) {
                                DropdownMenuItem(onClick = {
                                    expandedFilter = false
                                    filterType = categoryString
                                    showDialog = true
                                }, text = { Text(categoryString) })

                                DropdownMenuItem(onClick = {
                                    expandedFilter = false
                                    filterType = brandString
                                    showDialog = true
                                }, text = { Text(brandString) })

                                DropdownMenuItem(onClick = {
                                    expandedFilter = false
                                    filterType = priceRangeString
                                    showDialog = true
                                }, text = { Text(priceRangeString) })
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
                    SortOption.PRICE_ASCENDING -> products?.sortedBy { it.price }
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

    if (showDialog) {
        InputDialog(
            filterType = filterType,
            dialogInput = dialogInput,
            onInputChange = { dialogInput = it },
            onDismiss = {
                showDialog = false
                dialogInput = ""
            },
            onConfirm = {
                showDialog = false
                when (filterType) {
                    categoryString -> productViewModel.fetchProductsByCategory(context, dialogInput)
                    brandString -> productViewModel.fetchProductsByBrand(context, dialogInput)
                    priceRangeString -> {
                        val (minPrice, maxPrice) = dialogInput.split("-").map { it.trim().toDouble() }
                        productViewModel.fetchProductsByPriceRange(context, minPrice, maxPrice)
                    }
                }
                dialogInput = ""
            }
        )
    }
}

@Composable
fun InputDialog(
    filterType: String,
    dialogInput: String,
    onInputChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val priceRangeString = stringResource(id = R.string.price_range)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter $filterType") },
        text = {
            Column {
                TextField(
                    value = dialogInput,
                    onValueChange = onInputChange,
                    placeholder = { Text("Enter $filterType") },
                )
                if (filterType == priceRangeString) {
                    Text(text = stringResource(id = R.string.price_range_format))
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(id = R.string.dismiss))
            }
        }
    )
}

@Composable
fun ProductsCard(
    productDTO: ProductDTO,
    navController: NavController,
    viewModel: ProductViewModel,
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
                rememberImagePainter(data = imageUri)
            } else {
                painterResource(id = R.drawable.product_placeholder)
            }
            Image(
                painter = painter,
                contentDescription = stringResource(id = R.string.product_image),
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
                text = productDTO.brand.name,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (productDTO.onSale && productDTO.salePrice != null) {
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
                        text = "${productDTO.salePrice}€",
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
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
                        contentDescription = stringResource(id = R.string.add_to_cart)
                    )
                }
            }
        }
    }
}

