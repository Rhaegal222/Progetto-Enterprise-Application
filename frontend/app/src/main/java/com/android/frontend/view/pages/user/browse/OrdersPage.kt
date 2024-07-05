package com.android.frontend.view.pages.user.browse

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.frontend.R
import com.android.frontend.dto.OrderDTO
import com.android.frontend.view.component.ErrorDialog
import com.android.frontend.view.component.OrderCard
import com.android.frontend.view_models.user.OrderViewModel
import org.threeten.bp.format.DateTimeFormatter

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OrdersPage(navController: NavController, orderViewModel: OrderViewModel = viewModel()) {
    val context = LocalContext.current

    val orders by orderViewModel.ordersLiveData.observeAsState(emptyList())
    val isLoading by orderViewModel.isLoading.observeAsState(false)
    val hasError by orderViewModel.hasError.observeAsState(false)

    LaunchedEffect(Unit) {
        Log.d("DEBUG", "Loading user orders")
        orderViewModel.getAllLoggedUserOrders(context)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (hasError) {
        ErrorDialog(
            title = stringResource(id = R.string.fetching_error),
            onDismiss = { navController.popBackStack() },
            onRetry = { orderViewModel.getAllLoggedUserOrders(context) },
            errorMessage = stringResource(id = R.string.orders_load_failed)
        )
    } else {
        OrdersContent(navController, orders)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersContent(
    navController: NavController,
    orders: List<OrderDTO>
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }

    Scaffold(
        topBar = {
            Row {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(id = R.string.orders),
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.navigateUp()
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    },
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (orders.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_orders),
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                for (order in orders) {
                    OrderCard(order, dateFormatter)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
