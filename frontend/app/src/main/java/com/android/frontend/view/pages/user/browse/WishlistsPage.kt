package com.android.frontend.view.pages.user.browse

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.frontend.R
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.WishlistDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.ui.theme.colors.TextButtonColorScheme
import com.android.frontend.view.component.ErrorDialog
import com.android.frontend.view_models.user.WishlistViewModel

@SuppressLint("SuspiciousIndentation")
@Composable
fun WishlistsPage(
    navController: NavController,
    wishlistViewModel: WishlistViewModel = viewModel()
) {
    val context = LocalContext.current

    val wishlists by wishlistViewModel.wishlistLiveData.observeAsState(emptyList())
    val isLoading by wishlistViewModel.isLoading.observeAsState(false)
    val hasError by wishlistViewModel.hasError.observeAsState(false)

    LaunchedEffect(Unit) {
        Log.d("DEBUG", "${getCurrentStackTrace()} Loading wishlists")
        wishlistViewModel.getAllLoggedUserWishlists(context)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (hasError) {
        ErrorDialog(
            title = stringResource(id = R.string.fetching_error),
            onDismiss = { navController.popBackStack() },
            onRetry = { wishlistViewModel.getAllLoggedUserWishlists(context) },
            errorMessage = stringResource(id = R.string.wishlist_failed_to_load)
        )
    } else {
        WishlistsContent(navController, wishlists)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WishlistsContent(
    navController: NavController,
    wishlists: List<WishlistDTO>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.all_wishlists),
                        )
                        TextButton(
                            onClick = { navController.navigate(Navigation.AddWishlistPage.route) },
                            colors = TextButtonColorScheme.textButtonColors(),
                        ) {
                            Text(
                                text = stringResource(id = R.string.add_wishlist),
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                })
        })
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (wishlists.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.wishlist_empty),
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                for (wishlist in wishlists) {
                    WishlistCard(wishlist = wishlist, navController = navController)
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
                )
                Text(
                    text = "${wishlist.products?.size ?: 0} items",
                )
            }
        }
    }
}
