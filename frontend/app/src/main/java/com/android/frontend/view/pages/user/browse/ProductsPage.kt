package com.android.frontend.view.pages.user.browse

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.frontend.R
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.navigation.Navigation
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.ui.theme.colors.OutlinedTextFieldColorScheme
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
@Composable
fun ProductsPage(navController: NavController, cartViewModel: CartViewModel, productViewModel: ProductViewModel = viewModel()) {

    var searchQuery by remember { mutableStateOf(CurrentDataUtils.searchQuery) }
    var focusOnTextField by remember { mutableStateOf(false) }
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val categoryString = stringResource(id = R.string.category)
    val brandString = stringResource(id = R.string.brand)
    val priceRangeString = stringResource(id = R.string.price_range)

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
        if (searchQuery.isEmpty())
            productViewModel.fetchAllProducts(context)
        else
            productViewModel.searchProducts(context, CurrentDataUtils.searchQuery)

        productViewModel.fetchAllCategories(context)
        productViewModel.fetchAllBrands(context)
        productViewModel.getAllLoggedUserWishlists(context)
    }

    Scaffold(
        topBar = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = {
                    if (focusOnTextField)
                        IconButton(
                            modifier = Modifier.padding(0.dp),
                            onClick = {
                                searchQuery = ""
                                focusOnTextField = true
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                modifier = Modifier.padding(0.dp),
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.cancel),
                            )
                        }
                    else
                        Icon(
                            modifier = Modifier.padding(0.dp),
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(id = R.string.search),
                        )
                },
                trailingIcon = {
                    if (!focusOnTextField && searchQuery.isNotEmpty())
                        IconButton(
                            modifier = Modifier.padding(0.dp),
                            onClick = {
                                searchQuery = ""
                                CurrentDataUtils.searchQuery = ""
                                focusOnTextField = false
                                focusManager.clearFocus()
                                productViewModel.fetchAllProducts(context)
                            }
                        ) {
                            Icon(
                                modifier = Modifier.padding(0.dp),
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.cancel),
                            )
                        } else
                        IconButton(
                            modifier = Modifier.padding(0.dp),
                            onClick = {
                                CurrentDataUtils.searchQuery = searchQuery
                                CurrentDataUtils.searchSuggestions.toMutableList().add(searchQuery)
                                navController.navigate(Navigation.ProductsPage.route)
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                modifier = Modifier.padding(0.dp),
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(id = R.string.cancel),
                            )
                        }
                },
                singleLine = true,
                colors = OutlinedTextFieldColorScheme.colors(),
                placeholder = { Text(text = stringResource(id = R.string.search)) },
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        focusOnTextField = focusState.isFocused
                    },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        productViewModel.searchProducts(context, searchQuery)
                        focusManager.clearFocus()
                    }
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (hasError) {
                ErrorDialog(
                    title = stringResource(id = R.string.fetching_error),
                    onDismiss = { navController.popBackStack() },
                    onRetry = {
                        productViewModel.fetchAllProducts(context)
                        productViewModel.fetchAllCategories(context)
                        productViewModel.fetchAllBrands(context)
                        productViewModel.getAllLoggedUserWishlists(context) },
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        IconButton(
                            onClick = { expandedSort = !expandedSort }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SortByAlpha,
                                contentDescription = stringResource(id = R.string.sort),
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        DropdownMenu(
                            expanded = expandedSort,
                            onDismissRequest = { expandedSort = false },
                            modifier = Modifier.padding(16.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.displayName) },
                                    onClick = {
                                        selectedSortOption = option
                                        expandedSort = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box {
                        IconButton(
                            onClick = { expandedFilter = !expandedFilter }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterAlt,
                                contentDescription = stringResource(id = R.string.filter),
                            )
                        }
                        DropdownMenu(
                            expanded = expandedFilter,
                            onDismissRequest = { expandedFilter = false },
                            modifier = Modifier.padding(16.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text(categoryString) },
                                onClick = {
                                    filterType = categoryString
                                    showDialog = true
                                    expandedFilter = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(brandString) },
                                onClick = {
                                    filterType = brandString
                                    showDialog = true
                                    expandedFilter = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(priceRangeString) },
                                onClick = {
                                    filterType = priceRangeString
                                    showDialog = true
                                    expandedFilter = false
                                }
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }
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
