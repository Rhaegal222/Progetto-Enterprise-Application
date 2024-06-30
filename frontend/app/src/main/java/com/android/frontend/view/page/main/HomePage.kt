package com.android.frontend.view.page.main

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.view.component.Suggestion
import com.android.frontend.view_models.HomeViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(navController: NavHostController, homeViewModel: HomeViewModel = viewModel()) {

    val viewModel: HomeViewModel = viewModel()

    var searchQuery by remember { mutableStateOf("") }
    var focusOnTextField by remember { mutableStateOf(false) }

    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(focusOnTextField) {
        if (!focusOnTextField) {
            focusManager.clearFocus()
        }
    }

    Scaffold(
        topBar = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = {
                    if (focusOnTextField)
                        IconButton(
                            onClick = {
                                // Clear search query
                                searchQuery = ""
                                focusOnTextField = false
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.cancel),
                            )
                        }
                    else
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(id = R.string.search),
                        )
                },
                trailingIcon = {
                    if (!focusOnTextField)
                        IconButton(onClick = {
                            // Handle QR code scanner click
                        }) {
                            Icon(
                                imageVector = Icons.Filled.QrCodeScanner,
                                contentDescription = stringResource(id = R.string.search),
                            )
                        }
                    else
                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(id = R.string.cancel),
                            )
                        }
                },
                singleLine = true,
                placeholder = { Text(stringResource(id = R.string.search)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        focusOnTextField = focusState.isFocused
                    },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        // Handle search action
                    }
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            // Se la search bar ha il focus
            if (focusOnTextField) {
                Suggestion()
            } else {
                HomePagePreview()
            }
        }
    }
}

@Composable
fun HomePagePreview() {
    HorizontalPager(state = rememberPagerState(pageCount = { 2 })) {

    }
}
