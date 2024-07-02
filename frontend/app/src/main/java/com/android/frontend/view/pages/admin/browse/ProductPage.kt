package com.android.frontend.view.pages.admin.browse

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
import com.android.frontend.R
import com.android.frontend.dto.ProductDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.view_models.admin.ProductCategoryBrandViewModel

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductPage(navController: NavHostController, viewModel: ProductCategoryBrandViewModel = viewModel()) {
    val context = LocalContext.current
    val products by viewModel.productsLiveData.observeAsState()

    LaunchedEffect(Unit) {
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
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(products ?: emptyList()) { productDTO ->
                    ProductsCard(productDTO, navController, viewModel)
                }
            }
        }
    )
}

@Composable
fun ProductsCard(productDTO: ProductDTO, navController: NavController, viewModel: ProductCategoryBrandViewModel) {
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
            Image(
                painter = painterResource(id = R.drawable.product_placeholder),
                contentDescription = "Product Image",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(80.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = productDTO.title,
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
                    text = "${productDTO.productPrice}€",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    fontSize = 13.sp
                )

                Row {
                    Button(
                        colors = ButtonColorScheme.buttonColors(),
                        modifier = Modifier.size(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            // funzione per modificare il prodotto
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ModeEdit,
                            contentDescription = "Modify",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        colors = ButtonColorScheme.buttonColors(),
                        modifier = Modifier.size(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
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
                    onClick = {
                        viewModel.deleteProduct(productDTO.id.toString(), context)
                        showDialog = false
                    }
                ) {
                    Text("Continua")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Annulla")
                }
            },
            title = { Text("Elimina Prodotto") },
            text = { Text("Sei sicuro di voler eliminare questo prodotto?") }
        )
    }
}
