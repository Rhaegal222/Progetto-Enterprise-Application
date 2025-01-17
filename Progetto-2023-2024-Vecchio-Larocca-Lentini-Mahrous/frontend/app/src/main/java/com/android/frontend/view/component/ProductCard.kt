package com.android.frontend.view.component

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.android.frontend.R
import com.android.frontend.dto.ProductDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.ui.theme.colors.CardColorScheme
import com.android.frontend.ui.theme.colors.OutlinedButtonColorScheme
import com.android.frontend.view_models.user.CartViewModel

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
                        cartViewModel.addProductToCart(productDTO.id, 1, context)
                    }
                ) {
                    Icon(
                        modifier = Modifier.padding(0.dp),
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = stringResource(id = R.string.add_to_cart)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        modifier = Modifier.padding(0.dp),
                        text = stringResource(id = R.string.add_to_cart),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


