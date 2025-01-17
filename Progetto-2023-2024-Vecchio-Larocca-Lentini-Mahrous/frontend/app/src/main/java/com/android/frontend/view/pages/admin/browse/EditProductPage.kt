package com.android.frontend.view.pages.admin.browse

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.android.frontend.R
import com.android.frontend.dto.ProductDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.view.component.ErrorDialog
import com.android.frontend.view_models.admin.EditProductViewModel
import java.math.BigDecimal

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductPage(navController: NavHostController, viewModel: EditProductViewModel = viewModel(), productId: Long) {
    val context = LocalContext.current

    val isLoading by viewModel.isLoading.observeAsState(false)
    val hasError by viewModel.hasError.observeAsState(false)

    LaunchedEffect(Unit) {
        viewModel.getProductDetails(context, productId)
        viewModel.fetchAllBrands(context)
        viewModel.fetchAllCategories(context)
    }

    val productDetails by viewModel.productDetails.observeAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var nutritionalValues by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(0) }
    var price by remember { mutableStateOf(BigDecimal.ZERO) }
    var shippingCost by remember { mutableStateOf(BigDecimal.ZERO) }
    var onSale by remember { mutableStateOf(false) }
    var salePrice by remember { mutableStateOf(BigDecimal.ZERO) }
    var availability by remember { mutableStateOf(ProductDTO.Availability.IN_STOCK) }

    val prodImage by viewModel.prodImageLiveData.observeAsState()

    val allBrands by viewModel.allBrands.observeAsState(emptyList())
    val allCategories by viewModel.allCategories.observeAsState(emptyList())

    val selectedBrand by viewModel.brand.observeAsState()
    val selectedCategory by viewModel.category.observeAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(productDetails) {
        productDetails?.let {
            name = it.name ?: ""
            description = it.description ?: ""
            ingredients = it.ingredients ?: ""
            nutritionalValues = it.nutritionalValues ?: ""
            weight = it.weight ?: ""
            quantity = it.quantity ?: 0
            price = it.price ?: BigDecimal.ZERO
            shippingCost = it.shippingCost ?: BigDecimal.ZERO
            onSale = it.onSale ?: false
            salePrice = it.salePrice ?: BigDecimal.ZERO
            availability = it.availability ?: ProductDTO.Availability.IN_STOCK
        }
    }

    LaunchedEffect(productDetails) {
        productDetails?.let {
            viewModel.brand.value = it.brand
            viewModel.category.value = it.category
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.replacePhotoProduct(context, productId, it)
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
                // Retry fetching data
                viewModel.getProductDetails(context, productId)
                viewModel.fetchAllBrands(context)
                viewModel.fetchAllCategories(context)
            },
            errorMessage = stringResource(id = R.string.edit_product_load_failed)
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(id = R.string.edit_product)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(Navigation.ProductPage.route) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
                        }
                    }
                )
            }
        ) { paddingValues ->
            Spacer(modifier = Modifier.height(70.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(16.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Spacer(modifier = Modifier.height(25.dp))

                Box(modifier = Modifier.size(160.dp)) {
                    if (prodImage != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = prodImage),
                            contentDescription = stringResource(id = R.string.product_image),
                            modifier = Modifier
                                .size(160.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.product_placeholder),
                            contentDescription = stringResource(id = R.string.product_image),
                            modifier = Modifier
                                .size(160.dp)
                                .clip(CircleShape)
                        )
                    }
                }

                IconButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.End)
                        .size(40.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(id = R.string.edit_product_image)
                    )
                }

                // Availability ChoiceBox
                val availabilityOptions = ProductDTO.Availability.entries
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
                            DropdownMenuItem(
                                text = { Text(option.name) },
                                onClick = {
                                    availability = option
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
                        text = selectedBrand?.name ?: productDetails?.brand?.name ?: stringResource(
                            id = R.string.select_brand
                        ),
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
                        allBrands.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name) },
                                onClick = {
                                    viewModel.brand.value = option
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
                        text = selectedCategory?.name ?: productDetails?.category?.name
                        ?: stringResource(id = R.string.select_category),
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
                        allCategories.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name) },
                                onClick = {
                                    viewModel.category.value = option
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(id = R.string.product_name)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(id = R.string.product_description)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = ingredients,
                    onValueChange = { ingredients = it },
                    label = { Text(stringResource(id = R.string.ingredients)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = nutritionalValues,
                    onValueChange = { nutritionalValues = it },
                    label = { Text(stringResource(id = R.string.nutritional_values)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text(stringResource(id = R.string.product_weight)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = quantity.toString(),
                    onValueChange = { quantity = it.toIntOrNull() ?: 0 },
                    label = { Text(stringResource(id = R.string.quantity_availabile)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = price.toPlainString(),
                    onValueChange = { price = it.toBigDecimalOrNull() ?: BigDecimal.ZERO },
                    label = { Text(stringResource(id = R.string.product_price)) },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = shippingCost.toPlainString(),
                    onValueChange = { shippingCost = it.toBigDecimalOrNull() ?: BigDecimal.ZERO },
                    label = { Text(stringResource(id = R.string.shipping_cost)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(id = R.string.on_sale), modifier = Modifier.padding(end = 8.dp))
                    Checkbox(
                        checked = onSale,
                        onCheckedChange = { onSale = it }
                    )

                    Spacer(modifier = Modifier.width(8.dp)) // Adjust spacing if needed

                    if (onSale) {
                        TextField(
                            value = salePrice.toPlainString(),
                            onValueChange = { salePrice = it.toBigDecimalOrNull() ?: BigDecimal.ZERO },
                            label = { Text(stringResource(id = R.string.discounted_price)) },
                            modifier = Modifier.weight(1f) // Adjusted to use weight for equal distribution
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.updateProduct(
                            context = context,
                            productId = productId,
                            name = name,
                            description = description,
                            ingredients = ingredients,
                            nutritionalValues = nutritionalValues,
                            weight = weight,
                            quantity = quantity,
                            price = price,
                            shippingCost = shippingCost,
                            availability = availability,
                            onSale = onSale,
                            salePrice = salePrice
                        )
                        showSuccessDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.save_changes))
                }
            }

            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    title = { Text(stringResource(id = R.string.success)) },
                    text = { Text(stringResource(id = R.string.product_updated_successfully)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                showSuccessDialog = false
                                navController.navigate(Navigation.ProductPage.route)
                            }
                        ) {
                            Text(stringResource(id = R.string.ok))
                        }
                    }
                )
            }
        }
    }
}
