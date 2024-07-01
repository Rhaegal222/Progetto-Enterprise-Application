package com.android.frontend.view.page.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.frontend.R
import com.android.frontend.dto.ProductDTO
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.persistence.SecurePreferences
import com.android.frontend.view_models.CartViewModel
import com.android.frontend.view_models.ProductViewModel


@Composable
fun ProductDetailsPage(productViewModel: ProductViewModel, cartViewModel: CartViewModel) {
    val context = LocalContext.current
    val productId = CurrentDataUtils.currentProductId
    val productDetails = productViewModel.productDetailsLiveData.observeAsState().value
    val userId = SecurePreferences.getUser(context)?.id ?: ""

    productViewModel.getProductDetails(context, productId)

    Scaffold { padding ->
        Column {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
                    .padding(padding)
            ) {
                productDetails?.let { productItem ->
                    DetailContentImageHeader(productItem = productItem)
                    Spacer(modifier = Modifier.height(24.dp))
                    DetailContentDescription(productItem = productItem)
                }
            }

            Column {
                productDetails?.let {
                    DetailButtonAddCart(
                        productItem = it,
                        onClickToCart = { productItem ->
                            cartViewModel.addProductToCart(userId, productItem.id, 1, context)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun DetailContentImageHeader(
    productItem: ProductDTO
) {
    Card(
        shape = RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp),
        modifier = Modifier
            .blur(1.dp)
            .fillMaxWidth(),
    ) {
        Image(
            painter = painterResource(id = R.drawable.product_placeholder), // Usa l'immagine del prodotto reale
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
                    text = productItem.title,
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
            text = "${productItem.productPrice}€",
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
            text = productItem.description,
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
                    text = productItem.ingredients,
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
