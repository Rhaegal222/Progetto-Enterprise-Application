package com.android.frontend.view.pages.user.browse

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.frontend.R
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.view.component.ErrorDialog
import com.android.frontend.view_models.user.CartViewModel
import com.android.frontend.view_models.user.ProductViewModel
import com.android.frontend.view.component.ProductCard
import com.android.frontend.view_models.user.AddressViewModel

enum class SortOption(val displayName: String) {
    ALPHABETICAL("Alphabetical"),
    REVERSE_ALPHABETICAL("Reverse Alphabetical"),
    PRICE_ASCENDING("Price: Low to High"),
    PRICE_DESCENDING("Price: High to Low")
}

@SuppressLint("SuspiciousIndentation", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsPage(navController: NavController, cartViewModel: CartViewModel, productViewModel: ProductViewModel = viewModel()) {
    val context = LocalContext.current
    val products by productViewModel.productsLiveData.observeAsState(emptyList())
    val productImages by productViewModel.productImagesLiveData.observeAsState(emptyMap())

    var selectedSortOption by remember { mutableStateOf(SortOption.ALPHABETICAL) }
    var expandedSort by remember { mutableStateOf(false) }
    var expandedFilter by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }
    var dialogInput by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf("") }

    val isLoading by productViewModel.isLoading.observeAsState(false)
    val hasError by productViewModel.hasError.observeAsState(false)

    LaunchedEffect(Unit) {
        Log.d("DEBUG", "${getCurrentStackTrace()} Fetching all products")
        productViewModel.fetchAllProducts(context)
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
                                SortOption.entries.forEach { option ->
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
        }
    ) {
        Column{
            Spacer(modifier = Modifier.height(65.dp))
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (hasError) {
                ErrorDialog(
                    title = stringResource(id = R.string.fetching_error),
                    onDismiss = { navController.popBackStack() },
                    onRetry = { productViewModel.fetchAllProducts(context) },
                    errorMessage = stringResource(id = R.string.products_load_failed)
                )
            } else {
                val sortedProducts = when (selectedSortOption) {
                    SortOption.ALPHABETICAL -> products.sortedBy { it.name }
                    SortOption.REVERSE_ALPHABETICAL -> products.sortedByDescending { it.name }
                    SortOption.PRICE_ASCENDING -> products.sortedBy { it.price }
                    SortOption.PRICE_DESCENDING -> products.sortedByDescending { it.price }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(sortedProducts) { productDTO ->
                        ProductCard(
                            productDTO,
                            navController,
                            cartViewModel,
                            productImages[productDTO.id]
                        )
                    }
                }
            }
        }
    }

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
            Button(
                shape = RoundedCornerShape(12.dp),
                onClick = onConfirm
            )
            {
                Text(stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            Button(
                shape = RoundedCornerShape(12.dp),
                onClick = onDismiss
            ) {
                Text(stringResource(id = R.string.dismiss))
            }
        }
    )
}
