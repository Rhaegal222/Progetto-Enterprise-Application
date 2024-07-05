package com.android.frontend.view.pages.user.browse

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.frontend.R
import com.android.frontend.dto.WishlistDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.view_models.user.WishlistViewModel

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistsPage(
    navController: NavController,
    wishlistViewModel: WishlistViewModel = viewModel()
) {
    val context = LocalContext.current
    val wishlists by wishlistViewModel.wishlistLiveData.observeAsState(emptyList())
    var isLoading by remember { mutableStateOf(true) }

    // Fetch wishlists when the composable enters composition
    LaunchedEffect(Unit) {
        wishlistViewModel.getAllLoggedUserWishlists(context)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text= stringResource(id = R.string.all_wishlists)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                })
        },
        floatingActionButton = {
            Button(
                onClick = {
                    navController.navigate(Navigation.AddWishlistPage.route)
                },
                colors = ButtonColorScheme.buttonColors(),
                modifier = Modifier
                    .padding(0.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Wishlist",
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                        .padding(0.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.add_wishlist))
            }
        }
    ) { innerPadding ->
        Box( // Add Box for center alignment
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator() // Show loading indicator
            } else if (wishlists.isEmpty()) {
                Text("You have no wishlists yet.") // Handle empty state
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(wishlists) { wishlist -> // Safe access to wishlists
                        WishlistCard(wishlist = wishlist, navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun WishlistCard(wishlist: WishlistDTO, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                CurrentDataUtils.currentWishlistId = wishlist.id
                CurrentDataUtils.CurrentWishlistName = wishlist.wishlistName
                navController.navigate(Navigation.WishlistDetailsPage.route)
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
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
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${wishlist.products?.size ?: 0} items",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
