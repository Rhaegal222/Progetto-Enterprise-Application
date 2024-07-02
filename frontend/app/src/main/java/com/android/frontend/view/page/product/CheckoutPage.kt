package com.android.frontend.view.page.product

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.frontend.R
import com.android.frontend.navigation.Navigation
import com.android.frontend.view_models.user.PaymentViewModel
import com.android.frontend.view_models.user.AddressViewModel
import com.android.frontend.view_models.user.CartViewModel

@Composable
fun CheckoutPage(
    cartViewModel: CartViewModel,
    paymentViewModel: PaymentViewModel = viewModel(),
    addressViewModel: AddressViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    var paymentMethod by remember { mutableStateOf<String?>(null) }
    var shippingAddress by remember { mutableStateOf<String?>(null) }

    val payments by paymentViewModel.paymentMethodsLiveData.observeAsState(emptyList())
    val addresses by addressViewModel.shippingAddressesLiveData.observeAsState(emptyList())
    val isLoadingPayments by paymentViewModel.isLoading.observeAsState(false)
    val isLoadingAddresses by addressViewModel.isLoading.observeAsState(false)

    val cart by cartViewModel.cart.collectAsState()

    LaunchedEffect(Unit) {
        paymentViewModel.getAllPaymentMethods(context)
        addressViewModel.getAllShippingAddresses(context)
        cartViewModel.loadCart(context)  // Load the current cart
    }

    LaunchedEffect(payments, addresses) {
        paymentMethod = payments.find { it.isDefault }?.cardNumber
        shippingAddress = addresses.find { it.isDefault }?.street
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = stringResource(id = R.string.checkout),
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = stringResource(id = R.string.payment_method),
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (isLoadingPayments) {
            CircularProgressIndicator(modifier = Modifier.padding(bottom = 16.dp))
        } else if (paymentMethod == null) {
            Button(
                onClick = {
                    navController.navigate(Navigation.AddPaymentPage.route)
                },
                colors = ButtonDefaults.buttonColors(Color.Black),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.add_payment_method), color = Color.White)
            }
        } else {
            OutlinedTextField(
                value = paymentMethod!!,
                onValueChange = {},
                readOnly = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Payment, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        Text(
            text = stringResource(id = R.string.shipping_address),
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (isLoadingAddresses) {
            CircularProgressIndicator(modifier = Modifier.padding(bottom = 16.dp))
        } else if (shippingAddress == null) {
            Button(
                onClick = {
                    navController.navigate(Navigation.AddAddressPage.route)
                },
                colors = ButtonDefaults.buttonColors(Color.Black),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.add_shipping_address), color = Color.White)
            }
        } else {
            OutlinedTextField(
                value = shippingAddress!!,
                onValueChange = {},
                readOnly = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Home, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                cart?.cartItems?.forEach { cartItem ->
                    cartViewModel.removeCartItem(cartItem.id, context)
                }
                navController.navigate(Navigation.CartPage.route)
            },
            colors = ButtonDefaults.buttonColors(Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(stringResource(id = R.string.purchase), color = Color.White)
        }
    }
}