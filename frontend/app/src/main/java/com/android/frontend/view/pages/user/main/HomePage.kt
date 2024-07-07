package com.android.frontend.view.pages.user.main

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.dto.ProductDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.ui.theme.colors.OutlinedTextFieldColorScheme
import com.android.frontend.view.component.ProductCard
import com.android.frontend.view.component.Suggestion
import com.android.frontend.view.pages.user.browse.ProductsPage
import com.android.frontend.view_models.user.CartViewModel
import com.android.frontend.view_models.user.ProductViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.delay

@OptIn(ExperimentalPagerApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomePage(
    navController: NavHostController,
    productViewModel: ProductViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val productImages by productViewModel.productImagesLiveData.observeAsState(emptyMap())
    val products by productViewModel.productsLiveData.observeAsState(emptyList())

    val isLoading by productViewModel.isLoading.observeAsState(false)
    val hasError by productViewModel.hasError.observeAsState(false)

    var searchQuery by remember { mutableStateOf("") }
    var focusOnTextField by remember { mutableStateOf(false) }
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        productViewModel.fetchSalesProducts(context)
    }

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
                            modifier = Modifier.padding(0.dp),
                            onClick = {
                                searchQuery = ""
                                focusOnTextField = false
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                modifier = Modifier.padding(0.dp),
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.cancel),
                            )
                        }
                    else
                        Icon(
                            modifier = Modifier.padding(0.dp),
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(id = R.string.search),
                        )
                },
                trailingIcon = {
                    if (!focusOnTextField)
                        IconButton(
                            modifier = Modifier.padding(0.dp),
                            onClick = {
                            }
                        ) {
                            Icon(
                                modifier = Modifier.padding(0.dp),
                                imageVector = Icons.Filled.QrCodeScanner,
                                contentDescription = stringResource(id = R.string.search),
                            )
                        }
                    else
                        IconButton(
                            modifier = Modifier.padding(0.dp),
                            onClick = {
                                CurrentDataUtils.searchQuery = searchQuery
                                navController.navigate(Navigation.ProductsPage.route)
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                modifier = Modifier.padding(0.dp),
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(id = R.string.cancel),
                            )
                        }
                },
                singleLine = true,
                colors = OutlinedTextFieldColorScheme.colors(),
                placeholder = { Text(text = stringResource(id = R.string.search)) },
                modifier = Modifier
                    .padding(5.dp)
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
                        productViewModel.searchProducts(context, searchQuery)
                        focusManager.clearFocus()
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
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (hasError) {
                Text(text = stringResource(id = R.string.error_dialog_title))
            } else if (focusOnTextField) {
                Suggestion()
            } else if (searchQuery.isNotEmpty()) {
                ProductList(
                    navController = navController,
                    cartViewModel = cartViewModel,
                    productViewModel = productViewModel
                )
            } else {
                OnOfferProducts(
                    navController = navController,
                    pagerState = rememberPagerState(0),
                    products = products,
                    productImages = productImages,
                    cartViewModel = cartViewModel
                )
            }
        }
    }
}

@Composable
fun ProductList(
    navController: NavHostController,
    cartViewModel: CartViewModel,
    productViewModel: ProductViewModel
) {
    val products by productViewModel.productsLiveData.observeAsState(emptyList())
    val productImages by productViewModel.productImagesLiveData.observeAsState(emptyMap())

    LazyColumn {
        items(products) { product ->
            ProductCard(
                productDTO = product,
                navController = navController,
                cartViewModel = cartViewModel,
                imageUri = productImages[product.id]
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnOfferProducts(
    navController: NavHostController,
    pagerState: PagerState,
    products: List<ProductDTO>,
    productImages: Map<Long, Uri>,
    cartViewModel: CartViewModel,
) {

    var currentPage by remember { mutableIntStateOf(0) }

    val totalPages = products.size

    LaunchedEffect(totalPages) {
        if (totalPages > 0) {
            while (true) {
                delay(5000)
                currentPage = (currentPage + 1) % totalPages
                tween<Float>(2000)
                pagerState.animateScrollToPage(page = currentPage)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.on_offer),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(4.dp)
            )
        }

        HorizontalPager(
            state = pagerState,
            count = totalPages,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            ProductCard(
                productDTO = products[page],
                navController = navController,
                cartViewModel = cartViewModel,
                imageUri = productImages[products[page].id]
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (i in 0 until totalPages) {
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .size(10.dp)
                        .background(
                            color = if (i == currentPage) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.5f
                            ),
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
    }
}