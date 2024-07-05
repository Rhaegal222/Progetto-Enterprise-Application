package com.android.frontend.view.pages.admin.browse

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ModeEdit
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.android.frontend.R
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.ProductDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.view.component.ErrorDialog
import com.android.frontend.view_models.admin.ProductViewModel


@SuppressLint("SuspiciousIndentation", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPage(navController: NavHostController, viewModel: ProductViewModel = viewModel()) {
    val context = LocalContext.current
    val products by viewModel.productsLiveData.observeAsState()
    val productImages by viewModel.productImagesLiveData.observeAsState(emptyMap())

    val isLoading by viewModel.isLoading.observeAsState(false)
    val hasError by viewModel.hasError.observeAsState(false)

    LaunchedEffect(Unit) {
        Log.d("DEBUG", "${getCurrentStackTrace()} Fetching all products")
        viewModel.fetchAllProducts(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Products") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Navigation.AddProductPage.route)
                    }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.add_product)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Navigation.AdminMenu.route)  }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        }
    ){
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (hasError) {
            ErrorDialog(
                title = stringResource(id = R.string.fetching_error),
                onDismiss = { navController.popBackStack() },
                onRetry = { viewModel.fetchAllProducts(context) },
                errorMessage = stringResource(id = R.string.products_load_failed)
            )
        } else {
            products?.let { it1 -> ProductContent(it1, navController, viewModel, productImages) }
        }
    }
}


@Composable
fun ProductContent(products : List<ProductDTO>, navController: NavHostController, viewModel: ProductViewModel, productImages: Map<Long, Uri>) {
    Spacer(modifier = Modifier.height(70.dp))
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(products) { productDTO ->
            ProductsCard(productDTO, navController, viewModel, productImages[productDTO.id])
        }
    }
}

@Composable
fun ProductsCard(productDTO: ProductDTO, navController: NavController, viewModel: ProductViewModel, imageUri: Uri?) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .height(250.dp)
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

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${productDTO.price}€",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    fontSize = 13.sp
                )

                Row {
                    Button(
                        colors = ButtonColorScheme.buttonColors(),
                        modifier = Modifier.size(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            navController.navigate("${Navigation.EditProductPage}/${productDTO.id}")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ModeEdit,
                            contentDescription = stringResource(id = R.string.edit_product),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        colors = ButtonColorScheme.buttonColors(),
                        modifier = Modifier.size(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(id = R.string.delete),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        viewModel.deleteProduct(productDTO.id, context)
                        showDialog = false
                    }
                ) {
                    Text(stringResource(id = R.string.continuee))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            title = { Text(stringResource(id = R.string.delete_product)) },
            text = { Text(stringResource(id = R.string.sure_delete_product)) }
        )
    }
}
