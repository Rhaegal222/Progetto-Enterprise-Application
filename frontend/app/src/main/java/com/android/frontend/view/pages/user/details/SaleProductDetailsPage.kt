package com.android.frontend.view.pages.user.details

import android.annotation.SuppressLint
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.frontend.R
import com.android.frontend.dto.ProductDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.persistence.SecurePreferences
import com.android.frontend.view.component.ErrorDialog
import com.android.frontend.view_models.user.CartViewModel
import com.android.frontend.view_models.user.ProductDetailsViewModel
import com.android.frontend.view_models.user.ProductViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SaleProductDetailsPage(navController: NavController, cartViewModel: CartViewModel, productId: Long, productDetailsViewModel: ProductDetailsViewModel = viewModel()) {
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
            Scaffold {padding ->
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
                            DetailContentDescriptionWithDiscount(productItem = productItem)
                        }
                    }

                    Column {
                        productDetails?.let {
                            DetailButtonAddCartWithDiscount(
                                productItem = it,
                                onClickToCart = { productItem ->
                                    cartViewModel.addProductToCart(productItem.id, 1, context)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun DetailContentDescriptionWithDiscount(
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

        if (productItem.onSale && productItem.salePrice != null) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${productItem.price}€",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textDecoration = TextDecoration.LineThrough
                )

                Text(
                    text = "${productItem.salePrice}€",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        } else {
            Text(
                text = "${productItem.price}€",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Text(
            text = "Description",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${productItem.description}",
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
fun DetailButtonAddCartWithDiscount(
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