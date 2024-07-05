package com.android.frontend.view.component

import android.view.View
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.PopupMenu
import androidx.activity.ComponentActivity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.input.pointer.pointerInput
import com.android.frontend.R
import com.android.frontend.view_models.user.WishlistViewModel

@Composable
fun AddToWishlistButton(
    wishlistViewModel: WishlistViewModel = WishlistViewModel()
) {
    val context = LocalContext.current
    val wishlists by wishlistViewModel.wishlistLiveData.observeAsState(emptyList())
    val activity = context as? ComponentActivity ?: return

    OutlinedButton(
        onClick = {
            activity.lifecycleScope.launch {
                if (wishlists is List<*>) {
                    // Usa il context di LocalContext per ottenere la View
                    val it = View(context)
                    val popupMenu = PopupMenu(context, it)
                    for (wishlist in wishlists) {
                        popupMenu.menu.add(wishlist.wishlistName).setOnMenuItemClickListener {
                        // Logica per aggiungere il prodotto al carrello
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
        modifier = Modifier.pointerInput(Unit) {

        }
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = stringResource(id = R.string.add_to_wishlist)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(id = R.string.add_to_wishlist),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

