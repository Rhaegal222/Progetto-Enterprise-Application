package com.android.frontend.view.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.frontend.dto.BrandDTO
import com.android.frontend.dto.ProductCategoryDTO
import com.android.frontend.dto.ProductDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.view_models.admin.ProductCategoryBrandViewModel
import java.math.BigDecimal

@Composable
fun AddProductPage(navController: NavHostController, viewModel: ProductCategoryBrandViewModel = viewModel()) {
    val context = LocalContext.current

    // Fetch data when the page is opened
    LaunchedEffect(Unit) {
        viewModel.fetchAllData(context)
    }

    val isLoading by viewModel.isLoading.observeAsState(true)
    val title by viewModel.title.observeAsState("")
    val description by viewModel.description.observeAsState("")
    val ingredients by viewModel.ingredients.observeAsState("")
    val nutritionalValues by viewModel.nutritionalValues.observeAsState("")
    val productPrice by viewModel.productPrice.observeAsState(BigDecimal.ZERO)
    val deliveryPrice by viewModel.deliveryPrice.observeAsState(BigDecimal.ZERO)
    val productWeight by viewModel.productWeight.observeAsState("")
    val availability by viewModel.availability.observeAsState(ProductDTO.Availability.UNAVAILABLE)
    val quantity by viewModel.quantity.observeAsState(0)
    val allBrands by viewModel.allBrands.observeAsState(emptyList())
    val allCategories by viewModel.allCategories.observeAsState(emptyList())

    var selectedBrand by remember { mutableStateOf<BrandDTO?>(null) }
    var selectedCategory by remember { mutableStateOf<ProductCategoryDTO?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let {
//            viewModel.uploadImage(context, it)
//        }
//    }

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Add Product") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Add New Product", fontSize = 24.sp)

                    TextField(
                        value = title,
                        onValueChange = { viewModel.title.value = it },
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
                        value = productPrice.toString(),
                        onValueChange = { viewModel.productPrice.value = it.toBigDecimalOrNull() ?: BigDecimal.ZERO },
                        label = { Text("Product Price") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = deliveryPrice.toString(),
                        onValueChange = { viewModel.deliveryPrice.value = it.toBigDecimalOrNull() ?: BigDecimal.ZERO },
                        label = { Text("Delivery Price") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = productWeight,
                        onValueChange = { viewModel.productWeight.value = it },
                        label = { Text("Product Weight") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = quantity.toString(),
                        onValueChange = { viewModel.quantity.value = it.toIntOrNull() ?: 0 },
                        label = { Text("Quantity") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Availability ChoiceBox
                    val availabilityOptions = ProductDTO.Availability.values().toList()
                    var expandedAvailability by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = availability.name,
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
                                DropdownMenuItem(onClick = {
                                    viewModel.availability.value = option
                                    expandedAvailability = false
                                }) {
                                    Text(option.name)
                                }
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
                                DropdownMenuItem(onClick = {
                                    selectedBrand = brand
                                    viewModel.brand.value = brand
                                    expandedBrand = false
                                }) {
                                    Text(brand.name)
                                }
                            }
                        }
                    }

                    // Category ChoiceBox
                    var expandedCategory by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = selectedCategory?.categoryName ?: "Select Category",
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
                                DropdownMenuItem(onClick = {
                                    selectedCategory = category
                                    viewModel.productCategory.value = category
                                    expandedCategory = false
                                }) {
                                    Text(category.categoryName)
                                }
                            }
                        }
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
        )

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("Success") },
                text = { Text("Product added successfully!") },
                confirmButton = {
                    TextButton(onClick = {
                        showSuccessDialog = false
                        navController.navigate(Navigation.AdminMenu.route)
                        // Navigate back or perform other actions
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
