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

enum class SortOption(val displayName: String) {
    ALPHABETICAL("Alphabetical"),
    REVERSE_ALPHABETICAL("Reverse Alphabetical"),
    PRICE_ASCENDING("Price: Low to High"),
    PRICE_DESCENDING("Price: High to Low")
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsPage(navController: NavController, cartViewModel: CartViewModel, productViewModel: ProductViewModel = viewModel()) {
    val context = LocalContext.current
    val products by productViewModel.productsLiveData.observeAsState(emptyList())
    val productImages by productViewModel.productImagesLiveData.observeAsState(emptyMap())
    val categories by productViewModel.categoriesLiveData.observeAsState(emptyList())
    val brands by productViewModel.brandsLiveData.observeAsState(emptyList())

    var selectedSortOption by remember { mutableStateOf(SortOption.ALPHABETICAL) }
    var expandedSort by remember { mutableStateOf(false) }
    var expandedFilter by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf("") }
    var selectedBrand by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var filterType by remember { mutableStateOf("") }

    var dialogInput by remember { mutableStateOf("") }

    val isLoading by productViewModel.isLoading.observeAsState(false)
    val hasError by productViewModel.hasError.observeAsState(false)

    LaunchedEffect(Unit) {
        Log.d("DEBUG", "${getCurrentStackTrace()} Fetching all products, categories, and brands")
        productViewModel.fetchAllProducts(context)
        productViewModel.fetchAllCategories(context)
        productViewModel.fetchAllBrands(context)
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
        }
    ) {
        Column {
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
                val filteredProducts = products.filter {
                    (selectedCategory.isEmpty() || it.category.name == selectedCategory) &&
                            (selectedBrand.isEmpty() || it.brand.name == selectedBrand)
                }

                val sortedProducts = when (selectedSortOption) {
                    SortOption.ALPHABETICAL -> filteredProducts.sortedBy { it.name }
                    SortOption.REVERSE_ALPHABETICAL -> filteredProducts.sortedByDescending { it.name }
                    SortOption.PRICE_ASCENDING -> filteredProducts.sortedBy { it.price }
                    SortOption.PRICE_DESCENDING -> filteredProducts.sortedByDescending { it.price }
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
        if (filterType == priceRangeString) {
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
                    val (minPrice, maxPrice) = dialogInput.split("-").map { it.trim().toDouble() }
                    productViewModel.fetchProductsByPriceRange(context, minPrice, maxPrice)
                    dialogInput = ""
                }
            )
        } else {
            FilterDialog(
                filterType = filterType,
                tempSelectedCategory = selectedCategory,
                tempSelectedBrand = selectedBrand,
                categories = categories.map { it.name },
                brands = brands.map { it.name },
                onDismiss = { showDialog = false },
                onConfirmCategory = { category ->
                    selectedCategory = category
                    productViewModel.fetchProductsByCategory(context, category)
                    showDialog = false
                },
                onConfirmBrand = { brand ->
                    selectedBrand = brand
                    productViewModel.fetchProductsByBrand(context, brand)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun FilterDialog(
    filterType: String,
    tempSelectedCategory: String,
    tempSelectedBrand: String,
    categories: List<String>,
    brands: List<String>,
    onDismiss: () -> Unit,
    onConfirmCategory: (String) -> Unit,
    onConfirmBrand: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(tempSelectedCategory) }
    var selectedBrand by remember { mutableStateOf(tempSelectedBrand) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select $filterType") },
        text = {
            Column {
                when (filterType) {
                    "Category" -> {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 200.dp) // Limita l'altezza a 5 elementi circa
                        ) {
                            items(categories) { category ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    RadioButton(
                                        selected = selectedCategory == category,
                                        onClick = { selectedCategory = category }
                                    )
                                    Text(category)
                                }
                            }
                        }
                    }
                    "Brand" -> {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 200.dp) // Limita l'altezza a 5 elementi circa
                        ) {
                            items(brands) { brand ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                ) {
                                    RadioButton(
                                        selected = selectedBrand == brand,
                                        onClick = { selectedBrand = brand }
                                    )
                                    Text(brand)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    if (filterType == "Category") {
                        onConfirmCategory(selectedCategory)
                    } else if (filterType == "Brand") {
                        onConfirmBrand(selectedBrand)
                    }
                }
            ) {
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
            ) {
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
