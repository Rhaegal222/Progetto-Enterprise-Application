package com.android.frontend.view.page.profile

import ShippingAddressCard
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.android.frontend.R
import com.android.frontend.dto.AddressDTO
import com.android.frontend.view_models.AddressViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.navigation.Navigation
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.view.component.ErrorDialog

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ShippingAddressesPage(navController: NavController, addressViewModel: AddressViewModel = viewModel()) {

    val context = LocalContext.current

    val addresses by addressViewModel.shippingAddressesLiveData.observeAsState(emptyList())
    val isLoading by addressViewModel.isLoading.observeAsState(false)
    val hasError by addressViewModel.hasError.observeAsState(false)

    LaunchedEffect(Unit) {
        Log.d("DEBUG", "${getCurrentStackTrace()} Loading shipping addresses")
        addressViewModel.getAllShippingAddresses(context)
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (hasError) {
        ErrorDialog(
            title = stringResource(id = R.string.fetching_error),
            onDismiss = { navController.popBackStack() },
            onRetry = { addressViewModel.getAllShippingAddresses(context) },
            errorMessage = stringResource(id = R.string.shipping_addresses_load_failed)
        )
    } else {
        ShippingAddressesContent(navController, addresses, addressViewModel, context)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingAddressesContent(
    navController: NavController,
    addresses: List<AddressDTO>,
    addressViewModel: AddressViewModel,
    context: Context
) {
    var selectedAddress by remember { mutableStateOf<AddressDTO?>(null) }
    var isDefaultAddress by remember { mutableStateOf(false) }

    LaunchedEffect(addresses) {
        if (addresses.isNotEmpty()) {
            selectedAddress = addresses[0]
            isDefaultAddress = selectedAddress?.isDefault ?: false
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.shipping_addresses),
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (address in addresses) {
                ShippingAddressCard(
                    shippingAddress = address,
                    onRemove = { addressViewModel.deleteShippingAddress(context, address.id) })
            }
        }
    }
}
