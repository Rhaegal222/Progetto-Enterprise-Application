package com.android.frontend.view.page.profile

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.PaymentMethodDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.view.component.PaymentCard
import com.android.frontend.view_models.PaymentViewModel
import com.android.frontend.view.component.ErrorDialog

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PaymentMethodsPage(navController: NavHostController, paymentViewModel: PaymentViewModel = viewModel()) {
    val context = LocalContext.current
    val payments by paymentViewModel.paymentMethodsLiveData.observeAsState(emptyList())
    val isLoading by paymentViewModel.isLoading.observeAsState(false)
    val hasError by paymentViewModel.hasError.observeAsState(false)

    LaunchedEffect(Unit) {
        Log.d("DEBUG", "${getCurrentStackTrace()} Loading payment methods")
        paymentViewModel.getAllPaymentMethods(context)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (hasError) {
        ErrorDialog(
            title = stringResource(id = R.string.fetching_error),
            onDismiss = { navController.popBackStack() },
            onRetry = { paymentViewModel.getAllPaymentMethods(context) },
            errorMessage = stringResource(id = R.string.payment_methods_load_failed)
        )
    } else {
        PaymentMethodsContent(navController, payments, paymentViewModel, context)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsContent(
    navController: NavHostController,
    payments: List<PaymentMethodDTO>,
    paymentViewModel: PaymentViewModel,
    context: Context
) {
    val pagerState = rememberPagerState(pageCount = { payments.size })
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethodDTO?>(null) }
    var isDefaultPaymentMethod by remember { mutableStateOf(false) }

    LaunchedEffect(payments) {
        if (payments.isNotEmpty()) {
            selectedPaymentMethod = payments[0]
            isDefaultPaymentMethod = selectedPaymentMethod?.isDefault ?: false
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (page in payments.indices) {
                selectedPaymentMethod = payments[page]
                isDefaultPaymentMethod = selectedPaymentMethod?.isDefault ?: false
            }
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.payment_methods)
                    )
                }
            )
        },
        floatingActionButton = {
            Button(
                onClick = {
                    navController.navigate(Navigation.AddPaymentPage.route)
                },
                colors = ButtonColorScheme.buttonColors(),
                modifier = Modifier
                    .padding(0.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Payment Method",
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                        .padding(0.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.add_payment_method))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            if (payments.isNotEmpty()) {
                PaymentMethodsPagePreview(pagerState, payments, paymentViewModel, context, selectedPaymentMethod)
            } else {
                Text(
                    text = stringResource(id = R.string.no_payment_methods)
                )
            }
        }
    }
}

@Composable
fun PaymentMethodsPagePreview(
    pagerState: PagerState,
    payments: List<PaymentMethodDTO>,
    paymentViewModel: PaymentViewModel,
    context: Context,
    selectedPaymentMethod: PaymentMethodDTO?
) {
    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 50.dp),
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            PaymentCard(payment = payments[page], onRemove = {
                paymentViewModel.deletePayment(context, payments[page].id)
            })
        }
    }
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = selectedPaymentMethod?.isDefault ?: false,
            onCheckedChange = {
                selectedPaymentMethod?.let {
                    paymentViewModel.setDefaultPayment(context, it.id, pagerState)
                }
            }
        )
        Text(
            text = stringResource(id = R.string.set_as_default),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
