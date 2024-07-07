package com.android.frontend.view.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.android.frontend.R
import com.android.frontend.dto.ProductDTO
import com.android.frontend.dto.WishlistDTO
import com.android.frontend.ui.theme.colors.IconButtonColorScheme
import com.android.frontend.ui.theme.colors.OutlinedCardColorScheme
import com.android.frontend.ui.theme.colors.TextButtonColorScheme
import com.android.frontend.view_models.user.WishlistViewModel

@Composable
fun DropdownButtonMenu(
    productDTO: ProductDTO,
    wishlists: List<WishlistDTO>,
    wishlistViewModel: WishlistViewModel
) {
    val context = LocalContext.current
    val expanded = remember { mutableStateOf(false) }
    val selectedWishlist = remember { mutableStateOf<String?>(null) }
    val selectedWishlistId = remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    val errorMessage by remember { mutableStateOf("") }
    val addProductResult by wishlistViewModel.addProductResult

    Box {
        OutlinedCard(
            colors = OutlinedCardColorScheme.outlinedCardColors(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, OutlinedCardColorScheme.outlinedCardBorder()),
            modifier = Modifier.padding(0.dp).width(180.dp).height(40.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(0.dp)
            ) {
                TextButton(
                    modifier = Modifier.padding(0.dp).width(140.dp),
                    colors = TextButtonColorScheme.textButtonColorsCard(),
                    onClick = {
                        selectedWishlistId.value?.let { wishlistId ->
                            wishlistViewModel.addProductToWishlist(context, wishlistId, productDTO.id)
                        }
                    },
                ) {
                    if ("${selectedWishlist.value}" == "null") {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = stringResource(id = R.string.add_wishlist)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text =
                        if ("${selectedWishlist.value}" == "null") {
                            stringResource(id = R.string.add_to_wishlist)
                        } else {
                            selectedWishlist.value!!
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(0.dp)
                    )
                }
                IconButton(
                    modifier = Modifier.padding(0.dp).width(30.dp),
                    colors = IconButtonColorScheme.iconButtonColorsCard(),
                    onClick = {
                        expanded.value = true
                    }
                ) {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = "Expand",
                        modifier = Modifier.padding(0.dp)
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded.value,
            offset = DpOffset(0.dp, 0.dp),
            onDismissRequest = { expanded.value = false },
            modifier = Modifier
                .background(color = Color.White)
                .width(180.dp),
            scrollState = rememberScrollState(),
            border = BorderStroke(1.dp, OutlinedCardColorScheme.outlinedCardBorder()),
            shape = RoundedCornerShape(12.dp)
        ) {

            if (wishlists.isEmpty()) {
                DropdownMenuItem(
                    modifier = Modifier.padding(0.dp),
                    onClick = {
                        expanded.value = false
                    },
                    text = {
                        Text(
                            stringResource(id = R.string.no_wishlists),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        HorizontalDivider()
                    }
                )
            } else {
                wishlists.forEach { wishlist ->
                    DropdownMenuItem(
                        modifier = Modifier.padding(0.dp),
                        onClick = {
                            selectedWishlist.value = wishlist.wishlistName
                            selectedWishlistId.value = wishlist.id
                            expanded.value = false
                        },
                        text = {
                            Text(
                                wishlist.wishlistName,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            HorizontalDivider()
                        }
                    )
                }
            }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text(stringResource(id = R.string.success)) },
                text = { Text(stringResource(id = R.string.product_added_success_to_wishlist)) },
                confirmButton = {
                    TextButton(onClick = {
                        showSuccessDialog = false
                    }) {
                        Text(stringResource(id = R.string.okay))
                    }
                }
            )
        }

        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                title = { Text(stringResource(id = R.string.error)) },
                text = { Text(errorMessage) },
                confirmButton = {
                    TextButton(onClick = {
                        showErrorDialog = false
                    }) {
                        Text(stringResource(id = R.string.okay))
                    }
                }
            )
        }
    }
}

