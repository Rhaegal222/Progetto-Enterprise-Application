package com.android.frontend.view.pages.admin.add

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.dto.BrandDTO
import com.android.frontend.dto.CategoryDTO
import com.android.frontend.dto.ProductDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.view.component.ErrorDialog
import com.android.frontend.view_models.admin.AddProductViewModel
import java.math.BigDecimal

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductPage(navController: NavHostController, viewModel: AddProductViewModel = viewModel()) {
    val context = LocalContext.current

    val isLoading by viewModel.isLoading.observeAsState(false)
    val hasError by viewModel.hasError.observeAsState(false)

    LaunchedEffect(Unit) {
        viewModel.fetchAllBrands(context)
        viewModel.fetchAllCategories(context)
    }

    val name by viewModel.name.observeAsState("")
    val description by viewModel.description.observeAsState("")
    val ingredients by viewModel.ingredients.observeAsState("")
    val nutritionalValues by viewModel.nutritionalValues.observeAsState("")
    val weight by viewModel.weight.observeAsState("")
    val quantity by viewModel.quantity.observeAsState(0)
    val price by viewModel.price.observeAsState(BigDecimal.ZERO)
    val shippingCost by viewModel.shippingCost.observeAsState(BigDecimal.ZERO)
    val allBrands by viewModel.allBrands.observeAsState(emptyList())
    val allCategories by viewModel.allCategories.observeAsState(emptyList())
    val onSale by viewModel.onSale.observeAsState(false)
    val discountedPrice by viewModel.salePrice.observeAsState(BigDecimal.ZERO)

    val productId by viewModel.productId.observeAsState(-1L)

    var selectedBrand by remember { mutableStateOf<BrandDTO?>(null) }
    var selectedCategory by remember { mutableStateOf<CategoryDTO?>(null) }
    var selectedAvailability by remember { mutableStateOf<ProductDTO.Availability?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showImageUploadDialog by remember { mutableStateOf(false) }
    var showImageSuccessDialog by remember { mutableStateOf(false) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                productId?.let { it1 -> viewModel.uploadProductImage(context, it1, it) }
                showImageSuccessDialog = true
            }
        }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (hasError) {
        ErrorDialog(
            title = stringResource(id = R.string.fetching_error),
            onDismiss = { navController.popBackStack() },
            onRetry = {
                viewModel.fetchAllBrands(context)
                viewModel.fetchAllCategories(context)
            },
            errorMessage = stringResource(id = R.string.add_product_load_failed)
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.add_product)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(Navigation.ProductPage.route) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
                        }
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                Text("Add New Product", fontSize = 24.sp)

                // Availability ChoiceBox
                val availabilityOptions = ProductDTO.Availability.entries
                var expandedAvailability by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = selectedAvailability?.name ?: stringResource(id = R.string.select_availability),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { expandedAvailability = true })
                            .padding(16.dp)
                    )
                    DropdownMenu(
                        expanded = expandedAvailability,
                        onDismissRequest = { expandedAvailability = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        availabilityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name) },
                                onClick = {
                                    selectedAvailability = option
                                    viewModel.availability.value = option
                                    expandedAvailability = false
                                }
                            )
                        }
                    }
                }

                // Brand ChoiceBox
                var expandedBrand by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = selectedBrand?.name ?: stringResource(id = R.string.select_brand),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { expandedBrand = true })
                            .padding(16.dp)
                    )
                    DropdownMenu(
                        expanded = expandedBrand,
                        onDismissRequest = { expandedBrand = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        allBrands.forEach { brand ->
                            DropdownMenuItem(
                                text = { Text(brand.name) },
                                onClick = {
                                    selectedBrand = brand
                                    viewModel.brand.value = brand
                                    expandedBrand = false
                                }
                            )
                        }
                    }
                }

                // Category ChoiceBox
                var expandedCategory by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = selectedCategory?.name ?: stringResource(id = R.string.select_category),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { expandedCategory = true })
                            .padding(16.dp)
                    )
                    DropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        allCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategory = category
                                    viewModel.category.value = category
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                TextField(
                    value = name,
                    onValueChange = { viewModel.name.value = it },
                    label = { Text(stringResource(id = R.string.product_name)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = description,
                    onValueChange = { viewModel.description.value = it },
                    label = { Text(stringResource(id = R.string.product_description)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = ingredients,
                    onValueChange = { viewModel.ingredients.value = it },
                    label = { Text(stringResource(id = R.string.ingredients)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = nutritionalValues,
                    onValueChange = { viewModel.nutritionalValues.value = it },
                    label = { Text(stringResource(id = R.string.nutritional_values)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = price.toString(),
                    onValueChange = {
                        viewModel.price.value = it.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    },
                    label = { Text(stringResource(id = R.string.product_price)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = shippingCost.toString(),
                    onValueChange = {
                        viewModel.shippingCost.value =
                            it.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    },
                    label = { Text(stringResource(id = R.string.shipping_cost)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = weight,
                    onValueChange = { viewModel.weight.value = it },
                    label = { Text(stringResource(id = R.string.product_weight)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = quantity.toString(),
                    onValueChange = { viewModel.quantity.value = it.toIntOrNull() ?: 0 },
                    label = { Text(stringResource(id = R.string.quantity_availabile)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // On Sale Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.on_sale), modifier = Modifier.weight(1f))
                    Switch(
                        checked = onSale,
                        onCheckedChange = { viewModel.onSale.value = it }
                    )
                }

                if (onSale) {
                    TextField(
                        value = discountedPrice.toString(),
                        onValueChange = { viewModel.salePrice.value = it.toBigDecimalOrNull() ?: BigDecimal.ZERO },
                        label = { Text(stringResource(id = R.string.discounted_price)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = {
                        viewModel.addProduct(context)
                        showSuccessDialog = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.add_product))
                }
            }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text(stringResource(id = R.string.success)) },
                text = { Text(stringResource(id = R.string.add_image_product)) },
                confirmButton = {
                    TextButton(onClick = {
                        showSuccessDialog = false
                        showImageUploadDialog = true
                    }) {
                        Text(stringResource(id = R.string.yes))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showSuccessDialog = false
                        navController.navigate(Navigation.ProductPage.route)
                    }) {
                        Text(stringResource(id = R.string.no))
                    }
                }
            )
        }

        if (showImageUploadDialog) {
            launcher.launch("image/*")
            showImageUploadDialog = false
        }

        if (showImageSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showImageSuccessDialog = false },
                title = { Text(stringResource(id = R.string.success)) },
                text = { Text(stringResource(id = R.string.add_product_image_successfully)) },
                confirmButton = {
                    TextButton(onClick = {
                        showImageSuccessDialog = false
                        navController.navigate(Navigation.ProductPage.route)
                    }) {
                        Text(stringResource(id = R.string.okay))
                    }
                }
            )
        }
    }
}