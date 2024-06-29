package com.android.frontend.view.page.profile

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.controller.models.PaymentMethodDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.view.component.PaymentCard
import com.android.frontend.view_models.PaymentViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodsPage(navController: NavHostController, paymentViewModel: PaymentViewModel = viewModel()) {

    val context = LocalContext.current
    val payments by paymentViewModel.paymentMethodsLiveData.observeAsState()

    // Chiamare getAllPaymentMethods solo una volta quando la composable viene inizializzata
    LaunchedEffect(Unit) {
        paymentViewModel.getAllPaymentMethods(context)
    }

    val pagerState = rememberPagerState()
    var selectedPaymentMethod by remember { mutableStateOf<PaymentMethodDTO?>(null) }
    var isDefaultPaymentMethod by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(payments) {
        if (!payments.isNullOrEmpty()) {
            selectedPaymentMethod = payments!![0]
            isDefaultPaymentMethod = selectedPaymentMethod?.isDefault ?: false
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (payments != null && page in payments!!.indices) {
                selectedPaymentMethod = payments!![page]
                isDefaultPaymentMethod = selectedPaymentMethod?.isDefault ?: false
            }
        }
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.payment_methods),
                        color = colors.onBackground
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
            )
        },
        floatingActionButton = {
            Button(
                onClick = {
                    navController.navigate(Navigation.AddPaymentPage.route)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    contentColor = colors.onPrimary
                ),
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
            if (!payments.isNullOrEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    count = payments!!.size,
                    contentPadding = PaddingValues(horizontal = 50.dp),
                    modifier = Modifier.fillMaxWidth()
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                    ) {
                        PaymentCard(payment = payments!![page], onRemove = {
                            paymentViewModel.deletePayment(context, payments!![page].id)
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
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = colors.primary,
                            uncheckedColor = colors.onSurface
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.set_as_default),
                        color = colors.onBackground,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

