package com.android.frontend.view.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.PopupMenu
import androidx.activity.ComponentActivity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import com.android.frontend.R
import com.android.frontend.ui.theme.colors.OutlinedButtonColorScheme
import com.android.frontend.dto.ProductDTO
import com.android.frontend.dto.WishlistDTO
import com.android.frontend.view_models.user.CartViewModel
import com.android.frontend.view_models.user.WishlistViewModel

@Composable
fun AddToWishlistButton(
    productDTO: ProductDTO,
    wishlistViewModel: WishlistViewModel,
    cartViewModel: CartViewModel
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity ?: return

    OutlinedButton(
        onClick = {
            activity.lifecycleScope.launch {
                val wishlists = withContext(Dispatchers.IO) {
                    wishlistViewModel.getAllLoggedUserWishlists(context)
                }

                if (wishlists is wishlists) {
                    val popupMenu = PopupMenu(context, it)
                    for (wishlist in wishlists) {
                        popupMenu.menu.add((wishlist as WishlistDTO).wishlistName).setOnMenuItemClickListener {
                            cartViewModel.addProductToCart(productDTO.id, 1, context)
                            true
                        }
                    }
                    popupMenu.show()
                } else {
                    // Gestisci il caso in cui wishlists non sia una lista
                }
            }
        },
        shape = RoundedCornerShape(14.dp),
        // colors = OutlinedButtonColorScheme.outlinedButtonColors()
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = stringResource(id = R.string.add_to_wishlist)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.add_to_wishlist),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
