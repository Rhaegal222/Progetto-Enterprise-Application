package com.android.frontend.view.pages.user.browse

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.frontend.R
import com.android.frontend.view_models.user.CartViewModel
import com.android.frontend.view_models.user.ProductViewModel
import com.android.frontend.view.component.ProductCard

enum class SortOption(val displayName: String) {
    ALPHABETICAL("Alphabetical"),
    REVERSE_ALPHABETICAL("Reverse Alphabetical"),
    PRICE_ASCENDING("Price: Low to High"),
    PRICE_DESCENDING("Price: High to Low")
}

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsPage(navController: NavController, productViewModel: ProductViewModel, cartViewModel: CartViewModel) {
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
                        ProductCard(productDTO, navController, productViewModel, cartViewModel, productImages?.get(productDTO.id))
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
