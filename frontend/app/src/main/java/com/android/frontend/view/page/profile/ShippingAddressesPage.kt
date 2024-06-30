package com.android.frontend.view.page.profile

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
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
import androidx.navigation.NavController
import com.android.frontend.R
import com.android.frontend.dto.AddressDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.view.component.AddressCard
import com.android.frontend.view_models.AddressViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.view.component.ErrorDialog

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ShippingAddressesPage(navController: NavController, addressViewModel: AddressViewModel = viewModel()) {

    val context = LocalContext.current

    val isLoading by addressViewModel.isLoading.observeAsState(false)
    val hasError by addressViewModel.hasError.observeAsState(false)
    val addresses by addressViewModel.shippingAddressesLiveData.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        Log.d("DEBUG", "${getCurrentStackTrace()}, Loading shipping addresses")
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun ShippingAddressesContent(
    navController: NavController,
    addresses: List<AddressDTO>,
    addressViewModel: AddressViewModel,
    context: Context
) {
    val pagerState = rememberPagerState(pageCount = { addresses.size })
    var selectedAddress by remember { mutableStateOf<AddressDTO?>(null) }
    var isDefaultAddress by remember { mutableStateOf(false) }


    LaunchedEffect(addresses) {
        if (addresses.isNotEmpty()) {
            selectedAddress = addresses[0]
            isDefaultAddress = selectedAddress?.isDefault ?: false
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (page in addresses.indices) {
                selectedAddress = addresses[page]
                isDefaultAddress = selectedAddress?.isDefault ?: false
            }
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
        },
        floatingActionButton = {
            Button(
                onClick = {
                    navController.navigate(Navigation.AddAddressPage.route)
                },
                colors = ButtonDefaults.buttonColors(),
                modifier = Modifier
                    .padding(0.dp)

            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Address",
                    modifier = Modifier.width(40.dp).height(40.dp).padding(0.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = stringResource(id = R.string.add_address))
            }
        }
    )  { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            if (addresses.isNotEmpty()) {
                AddressPagePreview(pagerState, addresses, addressViewModel, context, selectedAddress)
            } else {
                Text(
                    text = stringResource(id = R.string.no_shipping_addresses),
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun AddressPagePreview(
    pagerState: PagerState,
    addresses: List<AddressDTO>,
    addressViewModel: AddressViewModel,
    context: Context,
    selectedAddress: AddressDTO?
) {
    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 50.dp),
        modifier = Modifier.fillMaxWidth()
    ) { page ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp)
        ) {
            AddressCard(shippingAddress = addresses[page], onRemove = {
                addressViewModel.deleteShippingAddress(context, addresses[page].id)
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
        Checkbox(checked = selectedAddress?.isDefault ?: false,
            onCheckedChange = {
                selectedAddress?.let {
                    addressViewModel.setDefaultShippingAddress(context, it.id, pagerState)
                }
            })

        Text(
            text = stringResource(id = R.string.set_as_default),
            color = Color.Black,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
