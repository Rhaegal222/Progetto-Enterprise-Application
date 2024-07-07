package com.android.frontend.view.pages.user.details

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddShoppingCart
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
import com.android.frontend.dto.WishlistDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.ui.theme.colors.OutlinedButtonColorScheme
import com.android.frontend.view.component.DropdownButtonMenu
import com.android.frontend.view.component.ErrorDialog
import com.android.frontend.view.component.ProductPrice
import com.android.frontend.view.component.ShippingCost
import com.android.frontend.view_models.user.CartViewModel
import com.android.frontend.view_models.user.ProductDetailsViewModel
import com.android.frontend.view_models.user.WishlistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProductDetailsPage(
    navController: NavController,
    cartViewModel: CartViewModel,
    productId: Long,
    productDetailsViewModel: ProductDetailsViewModel = viewModel(),
    wishlistViewModel: WishlistViewModel = viewModel(),
) {
    val context = LocalContext.current

    val isLoading by productDetailsViewModel.isLoading.observeAsState(false)
    val hasError by productDetailsViewModel.hasError.observeAsState(false)

    LaunchedEffect(Unit) {
        productDetailsViewModel.getProductDetails(context, productId)
    }

    val productDetails by productDetailsViewModel.productDetails.observeAsState()
    val prodImage by productDetailsViewModel.prodImageLiveData.observeAsState()
    val wishlists by wishlistViewModel.wishlistLiveData.observeAsState(emptyList())

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
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.navigate(Navigation.ProductsPage.route)
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    },
                )
            },
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding)
            ) {
                productDetails?.let { product ->
                    item { DetailContentImageHeader(prodImage) }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item { ActionButtons(product, wishlists, cartViewModel, wishlistViewModel) }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item { ProductHeader(product) }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    item { PriceSection(product) }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item { ShippingPriceSection(product) }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item { DescriptionSection(product) }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item { SpecificationsSection(product) }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    if (product.nutritionalValues != null) {
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                        item { NutritionalInfoSection(product) }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailContentImageHeader(imageUri: Uri?) {
    Card(
        shape = RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp),
        modifier = Modifier
            .blur(1.dp)
            .padding(16.dp)
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
fun ActionButtons(
    product: ProductDTO,
    wishlists: List<WishlistDTO>,
    cartViewModel: CartViewModel,
    wishlistViewModel: WishlistViewModel
) {

    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(40.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            colors = OutlinedButtonColorScheme.outlinedButtonColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            onClick = {
                cartViewModel.addProductToCart(product.id, 1, context)
            }
        ) {
            Icon(
                imageVector = Icons.Default.AddShoppingCart,
                contentDescription = stringResource(id = R.string.add_to_cart)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(id = R.string.add_to_cart),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        DropdownButtonMenu(product, wishlists, wishlistViewModel)
    }
}

@Composable
fun ProductHeader(product: ProductDTO) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = product.name,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = product.brand.name,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = product.category.name,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun PriceSection(product: ProductDTO) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        ProductPrice(productDTO = product)
    }
}

@Composable
fun ShippingPriceSection(product: ProductDTO) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        ShippingCost(productDTO = product)
    }
}

@Composable
fun DescriptionSection(product: ProductDTO) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionTitle("Descrizione")
        Text(
            text = product.description ?: "Nessuna descrizione disponibile",
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
        )
    }
}

@Composable
fun SpecificationsSection(product: ProductDTO) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionTitle("Specifiche")
        SpecificationItem("Peso", product.weight)
        product.ingredients?.let { SpecificationItem("Ingredienti", it) }
    }
}

@Composable
fun NutritionalInfoSection(product: ProductDTO) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionTitle("Valori Nutrizionali")
        Text(
            text = product.nutritionalValues ?: "Informazioni non disponibili",
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SpecificationItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
        )
    }
}