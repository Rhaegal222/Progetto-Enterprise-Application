package com.android.frontend.view.pages.profile

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.frontend.R
import com.android.frontend.dto.WishlistDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.ui.theme.colors.OutlinedTextFieldColorScheme
import com.android.frontend.view.pages.user.add.calculateNewCursorPosition
import com.android.frontend.view_models.user.WishlistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun AddWishlistPage(
    navController: NavHostController,
    wishlistViewModel: WishlistViewModel = viewModel()
) {
    var wishlistName by remember { mutableStateOf(TextFieldValue("")) }
    var expanded by remember { mutableStateOf(false) }
    var selectedVisibility by remember { mutableStateOf("Public") }
    val context = LocalContext.current
    val visibilityOptions = listOf("Public", "Private", "Shared")
    val allFieldsValid by derivedStateOf { wishlistName.text.isNotEmpty() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.add_wishlist).uppercase(),
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
                            label = { Text("Visibility") },
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

                    Button(
                        enabled = allFieldsValid,
                        onClick = {
                            try {
                                wishlistViewModel.createWishlist(
                                    context = context,
                                    wishlistName = wishlistName.text,
                                    WishlistDTO.WishlistVisibility.valueOf(selectedVisibility.uppercase())
                                )
                                navController.popBackStack()
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonColorScheme.buttonColors(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.add_wishlist).uppercase())
                    }
                }
            }
        }
    }
}
