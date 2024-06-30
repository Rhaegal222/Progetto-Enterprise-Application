package com.android.frontend.view.page.main

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.view.component.Suggestion
import com.android.frontend.view_models.HomeViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(navController: NavHostController, homeViewModel: HomeViewModel = viewModel()) {

    var searchQuery by remember { mutableStateOf("") }
    var focusOnTextField by remember { mutableStateOf(false) }

    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(focusOnTextField) {
        if (!focusOnTextField) {
            focusManager.clearFocus()
        }
    }

    Scaffold (
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
                placeholder = { stringResource(id = R.string.search) },
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
        ) {
            // Se la search bar ha il focus
            if (focusOnTextField) {
                Suggestion()
            } else {
                HomePagePreview(rememberPagerState(initialPage = 0))
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomePagePreview(pagerState: com.google.accompanist.pager.PagerState) {

    var currentPage by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(10000)
            currentPage = (currentPage + 1) % 3
            tween<Float>(2000)
            pagerState.animateScrollToPage(page = currentPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        Text(text = "Offerte del giorno")

        HorizontalPager(
            state = pagerState,
            count = 3,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> {
                    Text(text = "Pagina 1")
                }
                1 -> {
                    Text(text = "Pagina 2")
                }
                2 -> {
                    Text(text = "Pagina 3")
                }
            }
        }
    }
}
