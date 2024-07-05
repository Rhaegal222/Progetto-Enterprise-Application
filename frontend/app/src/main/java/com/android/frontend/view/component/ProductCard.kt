package com.android.frontend.view.component

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.android.frontend.R
import com.android.frontend.dto.ProductDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.ui.theme.colors.CardColorScheme
import com.android.frontend.ui.theme.colors.OutlinedButtonColorScheme
import com.android.frontend.view_models.user.CartViewModel
import com.android.frontend.view_models.user.WishlistViewModel

@Composable
fun ProductCard(
    productDTO: ProductDTO,
    navController: NavController,
    cartViewModel: CartViewModel,
    imageUri: Uri?
) {
    val context = LocalContext.current

    Card(
        border = BorderStroke(2.dp, Color.Gray),
        colors = CardColorScheme.cardColors(),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp, 8.dp)
            .height(400.dp)
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
                contentDescription = stringResource(id = R.string.product_image),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = productDTO.name,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

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

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    colors = OutlinedButtonColorScheme.outlinedButtonColors(),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        cartViewModel.addProductToCart(productDTO.id, 1, context)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.add_to_cart)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.add_to_cart),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                WishlistDropdownMenu(productDTO,wishlistViewModel = WishlistViewModel())
            }
        }
    }
}

@Composable
fun WishlistDropdownMenu(
    productDTO: ProductDTO,
    wishlistViewModel: WishlistViewModel
) {
    val context = LocalContext.current
    val wishlists by wishlistViewModel.wishlistLiveData.observeAsState()
    val expanded = remember { mutableStateOf(false) }
    val selectedWishlist = remember { mutableStateOf<String?>(null) }

    Box {
        OutlinedButton(
            colors = OutlinedButtonColorScheme.outlinedButtonColors(),
            shape = RoundedCornerShape(12.dp),
            onClick = { expanded.value = true }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = R.string.add_to_wishlist)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = selectedWishlist.value ?: stringResource(id = R.string.add_to_wishlist),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            wishlists?.forEach { wishlist ->
                DropdownMenuItem(
                    onClick = {
                        selectedWishlist.value = wishlist.wishlistName
                        Log.d("DEBUG", "Selected wishlist: ${wishlist.id}")
                        Log.d("DEBUG", "Selected wishlist: ${wishlist.wishlistName}")
                        Log.d("DEBUG", "Selected wishlist: ${wishlist.products}")
                        Log.d("DEBUG", "Selected product id: ${productDTO.id}")
                        wishlistViewModel.addProductToWishlist(context, wishlist.id, productDTO.id)
                        expanded.value = false
                    },
                    text = { Text(wishlist.wishlistName) }
                )
            }
        }
    }

    // Trigger fetching wishlists when the composable is first displayed
    LaunchedEffect(Unit) {
        wishlistViewModel.getAllLoggedUserWishlists(context)
    }
}


@Composable
fun ProductPrice(productDTO: ProductDTO){
    if (productDTO.onSale && productDTO.salePrice != null) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 30.dp)
        ) {
            Text(
                text = productDTO.salePrice.toString()+" €",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .heightIn(min = 35.dp)
            ) {
                Text(
                    text = productDTO.price.toString()+" €",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.LineThrough,
                    textAlign = TextAlign.Center,
                    color = Color.Red
                )
            }
        }
    } else {
        Text(
            text = productDTO.salePrice.toString()+" €",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun ShippingCost(productDTO: ProductDTO){
    Row {
        Text(
            text = stringResource(id = R.string.shipping_price),
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = productDTO.shippingCost.toString()+" €",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}