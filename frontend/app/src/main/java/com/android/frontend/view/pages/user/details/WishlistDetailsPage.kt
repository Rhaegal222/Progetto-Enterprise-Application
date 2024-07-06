package com.android.frontend.view.pages.user.details

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
    wishlistId: String,
    wishlistViewModel: WishlistViewModel = viewModel(),
) {
    val context = LocalContext.current
    val wishlistName = CurrentDataUtils.CurrentWishlistName
    val wishlistDetails = wishlistViewModel.wishlistDetailsLiveData.observeAsState().value
    val products by wishlistViewModel.productsLiveData.observeAsState(emptyList())
    val productImages by wishlistViewModel.productImagesLiveData.observeAsState(emptyMap())
    val showDialog = remember { mutableStateOf(false) }
    val showShareDialog = remember { mutableStateOf(false) }
    val shareLink = "${CurrentDataUtils.backendBaseUrl}wishlist/getWishlistById/$wishlistId"

    LaunchedEffect(Unit) {
        wishlistViewModel.getWishlistDetails(context, wishlistId)
    }

    Log.d("DEBUG", "Wishlist ID: $wishlistId")
    Log.d("DEBUG", "Wishlist Name: $wishlistName")
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(wishlistName)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        CurrentDataUtils.currentWishlistId = wishlistId
                        navController.navigate(Navigation.WishlistUpdatePage.route)
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit_wishlist))
                    }
                }
            )
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
                items(products) { productDTO ->
                    Log.d("DEBUG", "Product: $productDTO")
                    ProductsWishlistCard(
                        productDTO = productDTO,
                        navController = navController,
                        imageUri = productImages[productDTO.id],
                        onRemoveFromWishlist = { product ->
                            wishlistViewModel.removeProductFromWishlist(
                                context,
                                product.id,
                                wishlistId
                            )
                        }
                    )
                }
            }

        },
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = { showShareDialog.value = true },
                    containerColor = Color.Blue,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = stringResource(id = R.string.share))
                }
                Button(
                    onClick = {
                        showDialog.value = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(0.dp)
                ) {
                    Icon(
                        Icons.Default.Remove,
                        contentDescription = stringResource(id = R.string.remove_wishlist),
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
        }
    )

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(text = stringResource(id = R.string.confirm))
            },
            text = {
                Text(text = stringResource(id = R.string.Ask_Remove_wishlist))
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        showDialog.value = false
                        wishlistViewModel.deleteWishlist(context, wishlistId)
                        navController.popBackStack()
                    }
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }

    if (showShareDialog.value) {
        AlertDialog(
            onDismissRequest = { showShareDialog.value = false },
            title = {
                Text(text = stringResource(id = R.string.share))
            },
            text = {
                Text(text = shareLink)
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        showShareDialog.value = false
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Wishlist Link", shareLink)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text(text = stringResource(id = R.string.copy_link))
                }
            },
            dismissButton = {
                Button(
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        showShareDialog.value = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}


@Composable
fun ProductsWishlistCard(
    productDTO: ProductDTO,
    navController: NavController,
    imageUri: Uri?,
    onRemoveFromWishlist: (ProductDTO) -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .clickable {
                if (productDTO.onSale) {
                    navController.navigate("${Navigation.SaleProductDetailsPage}/${productDTO.id}")
                } else {
                    navController.navigate("${Navigation.ProductDetailsPage}/${productDTO.id}")
                }
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

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { showDialog.value = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.remove_from_wishlist),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(text = stringResource(id = R.string.confirm))
            },
            text = {
                Text(text = stringResource(id = R.string.Ask_remove_product_wishlist))
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    onClick = {
                        onRemoveFromWishlist(productDTO)
                        showDialog.value = false
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            }
        )
    }
}
