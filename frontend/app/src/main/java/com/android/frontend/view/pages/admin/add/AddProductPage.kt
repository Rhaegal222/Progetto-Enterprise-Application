package com.android.frontend.view.pages.admin.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.frontend.dto.BrandDTO
import com.android.frontend.dto.CategoryDTO
import com.android.frontend.dto.ProductDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.view_models.admin.ProductViewModel
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductPage(navController: NavHostController, viewModel: ProductViewModel = viewModel()) {
    val context = LocalContext.current

    // Fetch data when the page is opened
    LaunchedEffect(Unit) {
        viewModel.fetchAllData(context)
    }

    val isLoading by viewModel.isLoading.observeAsState(true)

    val name by viewModel.name.observeAsState("")
    val description by viewModel.description.observeAsState("")
    val ingredients by viewModel.ingredients.observeAsState("")
    val nutritionalValues by viewModel.nutritionalValues.observeAsState("")
    val weight by viewModel.weight.observeAsState("")
    val quantity by viewModel.quantity.observeAsState(0)
    val price by viewModel.price.observeAsState(BigDecimal.ZERO)
    val shippingCost by viewModel.shippingCost.observeAsState(BigDecimal.ZERO)
    val availability by viewModel.availability.observeAsState(ProductDTO.Availability.IN_STOCK)
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
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Add Product") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(Navigation.ProductPage.route) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Add New Product", fontSize = 24.sp)

                // Availability ChoiceBox
                val availabilityOptions = ProductDTO.Availability.entries
                var expandedAvailability by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = selectedAvailability?.name ?: "Select Availability",
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
                        text = selectedBrand?.name ?: "Select Brand",
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
                        text = selectedCategory?.name ?: "Select Category",
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
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = description,
                    onValueChange = { viewModel.description.value = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = ingredients,
                    onValueChange = { viewModel.ingredients.value = it },
                    label = { Text("Ingredients") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = nutritionalValues,
                    onValueChange = { viewModel.nutritionalValues.value = it },
                    label = { Text("Nutritional Values") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = price.toString(),
                    onValueChange = {
                        viewModel.price.value = it.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    },
                    label = { Text("Product Price") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = shippingCost.toString(),
                    onValueChange = {
                        viewModel.shippingCost.value =
                            it.toBigDecimalOrNull() ?: BigDecimal.ZERO
                    },
                    label = { Text("Delivery Price") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = weight,
                    onValueChange = { viewModel.weight.value = it },
                    label = { Text("Product Weight") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = quantity.toString(),
                    onValueChange = { viewModel.quantity.value = it.toIntOrNull() ?: 0 },
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth()
                )

                // On Sale Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("On Sale", modifier = Modifier.weight(1f))
                    Switch(
                        checked = onSale,
                        onCheckedChange = { viewModel.onSale.value = it }
                    )
                }

                if (onSale) {
                    TextField(
                        value = discountedPrice.toString(),
                        onValueChange = { viewModel.salePrice.value = it.toBigDecimalOrNull() ?: BigDecimal.ZERO },
                        label = { Text("Discounted Price") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = {
                        viewModel.addProduct(context)
                        showSuccessDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Product")
                }
            }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("Success") },
                text = { Text("Product added successfully! Do you want to upload an image for the product?") },
                confirmButton = {
                    TextButton(onClick = {
                        showSuccessDialog = false
                        showImageUploadDialog = true
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showSuccessDialog = false
                        navController.navigate(Navigation.ProductPage.route)
                    }) {
                        Text("No")
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
                title = { Text("Success") },
                text = { Text("Product image uploaded successfully!") },
                confirmButton = {
                    TextButton(onClick = {
                        showImageSuccessDialog = false
                        navController.navigate(Navigation.ProductPage.route)
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}