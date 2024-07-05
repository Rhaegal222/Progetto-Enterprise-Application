package com.android.frontend.view.pages.user.details

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.frontend.R
import com.android.frontend.navigation.Navigation
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.view_models.user.WishlistViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.android.frontend.dto.ProductDTO


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WishlistDetailsPage(
    navController: NavController,
    wishlistViewModel: WishlistViewModel = viewModel(),
) {
    val context = LocalContext.current
    val wishlistId = CurrentDataUtils.currentWishlistId
    val wishlistName = CurrentDataUtils.CurrentWishlistName
    val wishlistDetails = wishlistViewModel.wishlistDetailsLiveData.observeAsState().value
    val products = wishlistViewModel.productsLiveData.observeAsState().value
    val showDialog = remember { mutableStateOf(false) }

    wishlistViewModel.getWishlistDetails(context, wishlistId, wishlistName)

    LaunchedEffect(Unit) {
        wishlistViewModel.getWishlistDetails(context, wishlistId, wishlistName)
    }
    Log.d("DEBUG", "Wishlist ID: $wishlistId")
    Log.d("DEBUG", "Wishlist Name: $wishlistName")
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = wishlistName)
            },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                })
        },

        content = { innerPadding ->
            if (wishlistDetails != null) {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {

                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(products ?: emptyList()) { productDTO ->
                    Log.d("DEBUG", "Product: $productDTO")
                    ProductsWishlistCard(productDTO = productDTO, navController = navController, imageUri = null)
                }
            }

        },
        floatingActionButton = {
            Button(
                onClick = {
                    showDialog.value = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(0.dp)
            ) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = "Remove Wishlist",
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                        .padding(0.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.remove_wishlist)
                )
            }
        }
    )

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(text = "Conferma")
            },
            text = {
                Text("Sei sicuro di voler cancellare questa wishlist?")
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        showDialog.value = false
                        wishlistViewModel.deleteWishlist(context, wishlistId)
                        navController.popBackStack()
                    }
                ) {
                    Text("Conferma")
                }
            },
            dismissButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text("Annulla")
                }
            }
        )
    }
}

@Composable
fun ProductsWishlistCard(
    productDTO: ProductDTO,
    navController: NavController,
    imageUri: Uri?
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .height(250.dp)
            .clickable {
                CurrentDataUtils.currentProductId = productDTO.id
                CurrentDataUtils.currentProductImageUri = imageUri
                val route = if (productDTO.onSale) {
                    Navigation.SaleProductDetailsPage.route
                } else {
                    Navigation.ProductDetailsPage.route
                }
                navController.navigate(route)
            }
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
                contentDescription = "Product Image",
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

            if (productDTO.onSale && productDTO.salePrice != null) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${productDTO.price}€",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        fontSize = 18.sp,
                        textDecoration = TextDecoration.LineThrough
                    )

                    Text(
                        text = "${productDTO.salePrice}€",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        fontSize = 18.sp
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${productDTO.price}€",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        fontSize = 18.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}
