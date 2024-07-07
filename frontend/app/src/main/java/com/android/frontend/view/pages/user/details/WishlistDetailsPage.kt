package com.android.frontend.view.pages.user.details

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.AddTask
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.android.frontend.dto.ProductDTO
import com.android.frontend.ui.theme.colors.CardColorScheme
import com.android.frontend.ui.theme.colors.OutlinedButtonColorScheme
import com.android.frontend.view.component.ProductPrice
import com.android.frontend.view.component.ShippingCost


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
    val showPermitDialog = remember { mutableStateOf(false) } // Add state for permit dialog
    val shareLink = "https://example.com/api/v1/wishlist/getWishlistById/$wishlistId"
    val wishlistVisibility = CurrentDataUtils.CurrentWishlistVisibility
    Log.d("DEBUG", "Wishlist Visibility: $wishlistVisibility")

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
                    IconButton(onClick = {
                        navController.navigate(Navigation.WishlistsPage.route)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (wishlistVisibility == "SHARED") {
                        IconButton(onClick = {
                            showPermitDialog.value = true // Set state to show permit dialog
                        }) {
                            Icon(Icons.Default.AddTask, contentDescription = stringResource(id = R.string.permit_user_Email))
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    IconButton(onClick = {
                        CurrentDataUtils.currentWishlistId = wishlistId
                        navController.navigate(Navigation.WishlistUpdatePage.route)
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit_wishlist))
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = { showShareDialog.value = true }) {
                        Icon(Icons.Default.Share, contentDescription = stringResource(id = R.string.share))
                        Spacer(modifier = Modifier.width(16.dp))
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
                        wishlistViewModel.deleteSharedWishlistAccessByWishlistId(context, wishlistId)
                        //wishlistViewModel.deleteWishlist(context, wishlistId)
                        navController.navigate(Navigation.WishlistsPage.route)
                        showDialog.value = false
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

    if (showPermitDialog.value) {
        PermitDialog(
            showDialog = showPermitDialog,
            onConfirm = { email ->
                Log.d("DEBUG", "Email: $email")
                wishlistViewModel.shareWishlist(context, CurrentDataUtils.currentWishlistId, email)
                navController.navigate("${Navigation.WishlistDetailsPage}/${wishlistId}")
            }
        )
    }
}

@Composable
fun PermitDialog(
    showDialog: MutableState<Boolean>,
    onConfirm: (String) -> Unit
) {
    val email = remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = {
            Text(text = stringResource(id = R.string.permit_user_Email))
        },
        text = {
            Column {
                Text(text = stringResource(id = R.string.enter_email))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text(text = stringResource(id = R.string.email)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    showDialog.value = false
                    onConfirm(email.value.text)
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


@Composable
fun ProductsWishlistCard(
    productDTO: ProductDTO,
    navController: NavController,
    imageUri: Uri?,
    onRemoveFromWishlist: (ProductDTO) -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }

    Card(
        border = BorderStroke(2.dp, Color.Gray),
        colors = CardColorScheme.cardColors(),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp, 8.dp)
            .clickable {
                navController.navigate("${Navigation.ProductDetailsPage}/${productDTO.id}")
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
                contentDescription = stringResource(id = R.string.product_image),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Text(
                    text = productDTO.name + " - ",
                    fontSize = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = productDTO.weight,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = productDTO.brand.name,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            ProductPrice(productDTO)

            Spacer(modifier = Modifier.height(4.dp))

            ShippingCost(productDTO)

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    colors = OutlinedButtonColorScheme.outlinedButtonColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(0.dp),
                    onClick = {
                    }
                ) {
                    Icon(
                        modifier = Modifier.padding(0.dp),
                        imageVector = Icons.Default.Remove,
                        contentDescription = stringResource(id = R.string.remove_from_wishlist)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        modifier = Modifier.padding(0.dp),
                        text = stringResource(id = R.string.remove_from_wishlist),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
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


