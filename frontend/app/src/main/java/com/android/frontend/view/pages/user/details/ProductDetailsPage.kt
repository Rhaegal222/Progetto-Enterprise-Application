package com.android.frontend.view.pages.user.details

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.android.frontend.R
import com.android.frontend.dto.ProductDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.view.component.DropdownButtonMenu
import com.android.frontend.view.component.ErrorDialog
import com.android.frontend.view_models.user.CartViewModel
import com.android.frontend.view_models.user.ProductDetailsViewModel
import com.android.frontend.view_models.user.WishlistViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProductDetailsPage(navController: NavController, cartViewModel: CartViewModel, productId: Long, productDetailsViewModel: ProductDetailsViewModel = viewModel()) {
    val context = LocalContext.current

    val isLoading by productDetailsViewModel.isLoading.observeAsState(false)
    val hasError by productDetailsViewModel.hasError.observeAsState(false)

    LaunchedEffect(Unit) {
        productDetailsViewModel.getProductDetails(context, productId)
    }

    val productDetails by productDetailsViewModel.productDetails.observeAsState()
    val prodImage by productDetailsViewModel.prodImageLiveData.observeAsState()


    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (hasError) {
        ErrorDialog(
            title = stringResource(id = R.string.fetching_error),
            onDismiss = { navController.popBackStack() },
            onRetry = {
                productDetailsViewModel.getProductDetails(context, productId)
            },
            errorMessage = stringResource(id = R.string.edit_product_load_failed)
        )
    } else {
        Scaffold(
            topBar = {
                androidx.compose.material.TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigate(Navigation.ProductsPage.route)
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    },
                    backgroundColor = Color.Transparent,
                    elevation = 0.dp
                )
            }
        ) {
            Scaffold { padding ->
                Column {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(1f)
                            .padding(padding)
                    ) {
                        productDetails?.let { productItem ->
                            DetailContentImageHeader(prodImage)
                            Spacer(modifier = Modifier.height(24.dp))
                            DetailContentDescription(productItem = productItem)
                        }
                    }

                    Column {
                        productDetails?.let {
                            DetailButtonAddCart(
                                productItem = it,
                                onClickToCart = { productItem ->
                                    cartViewModel.addProductToCart(productItem.id, 1, context)
                                }
                            )
                            // DropdownButtonMenu(productDTO,wishlists,wishlistViewModel= WishlistViewModel())
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DetailContentImageHeader(
    imageUri: Uri?
) {
    Card(
        shape = RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp),
        modifier = Modifier
            .blur(1.dp)
            .fillMaxWidth(),
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
                .height(350.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
fun DetailContentDescription(
    modifier: Modifier = Modifier,
    productItem: ProductDTO
) {
    Column(
        modifier = modifier.padding(start = 16.dp, end = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = productItem.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = productItem.brand.name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorite",
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${productItem.price}€",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.End),
            fontSize = 18.sp
        )

        Text(
            text = "Description",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${ productItem.description }",
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Ingredients",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )

            Card(
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.align(Alignment.CenterVertically
                )
            ) {
                Text(
                    text = "${productItem.ingredients}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Quantità",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
            Card(
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    text = productItem.quantity.toString(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun DetailButtonAddCart(
    modifier: Modifier = Modifier,
    productItem: ProductDTO,
    onClickToCart: (ProductDTO) -> Unit
) {
    Button(
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = { onClickToCart.invoke(productItem) }
    ) {
        Text(
            text = "Add to Cart",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )
    }
}