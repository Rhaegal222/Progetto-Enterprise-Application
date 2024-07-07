package com.android.frontend.view.component

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.frontend.dto.WishlistDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.ui.theme.colors.CardColorScheme
import com.android.frontend.view_models.user.WishlistViewModel

@Composable
fun WishlistCard(
    wishlist: WishlistDTO,
    navController: NavController,
    wishlistViewModel: WishlistViewModel
) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = "Delete Wishlist") },
            text = { Text(text = "Are you sure you want to delete this wishlist?") },
            confirmButton = {
                Button(
                    onClick = {
                        if (wishlist.visibility.toString() == "SHARED")
                            wishlistViewModel.deleteSharedWishlistAccessByWishlistId(context, wishlist.id)
                        wishlistViewModel.deleteWishlist(context, wishlist.id)
                        navController.navigate(Navigation.WishlistsPage.route)
                        showDialog.value = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        border = BorderStroke(2.dp, Color.Gray),
        colors = CardColorScheme.cardColors(),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp, 8.dp)
            .clickable {
                CurrentDataUtils.CurrentWishlistVisibility = wishlist.visibility.toString()
                CurrentDataUtils.CurrentWishlistName = wishlist.wishlistName
                Log.d("DEBUG", "Wishlist Name: ${CurrentDataUtils.CurrentWishlistName}")
                navController.navigate("${Navigation.WishlistDetailsPage}/${wishlist.id}")
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = wishlist.wishlistName,
                )
                Text(
                    text = "${wishlist.products?.size ?: 0} items",
                )
            }
            IconButton(
                onClick = {
                    showDialog.value = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete wishlist"
                )
            }
        }
    }
}
