package com.android.frontend.view.pages.user.details

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.android.frontend.persistence.CurrentDataUtils
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.frontend.R
import com.android.frontend.dto.WishlistDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.ui.theme.colors.OutlinedButtonColorScheme
import com.android.frontend.ui.theme.colors.OutlinedTextFieldColorScheme
import com.android.frontend.view_models.user.WishlistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WishlistUpdatePage(
    navController: NavController,
    wishlistViewModel: WishlistViewModel = viewModel()
) {
    val context = LocalContext.current
    val wishlistId = CurrentDataUtils.currentWishlistId //
    Log.d("DEBUG11111", "Wishlist ID: $wishlistId")// Retrieve the wishlist ID
    val wishlistDetails by wishlistViewModel.wishlistDetailsLiveData.observeAsState()

    // States to hold the updated values
    var wishlistName by remember { mutableStateOf(TextFieldValue("")) }
    var selectedVisibility by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val visibilityOptions = WishlistDTO.WishlistVisibility.entries.map { it.name } // Automatically fetch all visibility options

    // Load the wishlist details when the page is opened
    LaunchedEffect(wishlistId) {
        Log.d("DEBUG", "Fetching wishlist with ID: $wishlistId")
        wishlistViewModel.getWishlistById(context, wishlistId)
    }

    // Update states when wishlist details are loaded
    LaunchedEffect(wishlistDetails) {
        wishlistDetails?.let {
            Log.d("DEBUG", "Wishlist details loaded: $it")
            wishlistName = TextFieldValue(it.wishlistName)
            selectedVisibility = it.visibility.name
        }
    }
    Log.d("DEBUG", "Wishlist Name: $wishlistName")
    Log.d("DEBUG", "Selected Visibility: $selectedVisibility")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.edit_wishlist),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        colors = OutlinedTextFieldColorScheme.colors(),
                        singleLine = true,
                        value = wishlistName,
                        onValueChange = { wishlistName = it },
                        label = { Text("Wishlist Name") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box {
                        OutlinedTextField(
                            colors = OutlinedTextFieldColorScheme.colors(),
                            value = selectedVisibility,
                            onValueChange = { },
                            label = { Text(selectedVisibility) },
                            trailingIcon = {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Visibility")
                                }
                            },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            visibilityOptions.forEach { visibility ->
                                DropdownMenuItem(
                                    text = { Text(visibility) },
                                    onClick = {
                                        selectedVisibility = visibility
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        shape = RoundedCornerShape(12.dp),
                        enabled = wishlistName.text.isNotEmpty() && selectedVisibility.isNotEmpty(),
                        onClick = {
                            try {
                                val wishlistDTO = WishlistDTO(
                                    id = wishlistId,
                                    wishlistName = wishlistName.text,
                                    visibility = WishlistDTO.WishlistVisibility.valueOf(selectedVisibility.uppercase())
                                )
                                wishlistViewModel.updateWishlist(context, wishlistId, wishlistDTO)
                                CurrentDataUtils.CurrentWishlistName = wishlistName.text
                                navController.navigate("${Navigation.WishlistDetailsPage}/${wishlistId}")} catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = OutlinedButtonColorScheme.outlinedButtonColors(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.confirm))
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        shape = RoundedCornerShape(12.dp),
                        onClick = {
                            wishlistViewModel.deleteWishlist(context, wishlistId)
                        },
                        colors = ButtonColorScheme.buttonColors(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.delete))
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
