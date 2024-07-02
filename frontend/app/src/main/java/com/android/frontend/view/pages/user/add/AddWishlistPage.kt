package com.android.frontend.view.pages.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.frontend.R
import com.android.frontend.view_models.user.WishlistViewModel


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun AddWishlistPage(navController: NavHostController, WishlistViewModel: WishlistViewModel = viewModel()) {


    var wishlistName by remember { mutableStateOf(TextFieldValue("")) }
    var expanded by remember { mutableStateOf(false) }
    var selectedVisibility by remember { mutableStateOf("Public") } // Default visibility

    val visibilityOptions = listOf("Public", "Private", "Shared") // Available options

    Scaffold(
        // ... (rest of the Scaffold code is the same)
    ) { innerPadding ->
        LazyColumn(
            // ... (rest of the LazyColumn code is the same)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Wishlist Name Input
                    OutlinedTextField(

                        value = wishlistName,
                        onValueChange = {
                            wishlistName = it
                        },)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Visibility Dropdown Menu
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = selectedVisibility,
                            onValueChange = {},
                            label = { Text(stringResource(id = R.string.add_wishlist)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            visibilityOptions.forEach { visibilityOption ->
                                DropdownMenuItem(
                                    text = { Text(visibilityOption) },
                                    onClick = {
                                        selectedVisibility = visibilityOption
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}
