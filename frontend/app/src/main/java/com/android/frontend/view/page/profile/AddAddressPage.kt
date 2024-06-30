package com.android.frontend.view.page.profile

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.android.frontend.R
import com.android.frontend.view_models.AddressViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddAddressPage(navController: NavHostController) {

    val context = LocalContext.current
    val addressViewModel = AddressViewModel()

    Scaffold {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ){
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = addressViewModel.header,
                        onValueChange = { addressViewModel.header = it},
                        label = { Text("Header") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = addressViewModel.street,
                        onValueChange = { addressViewModel.street = it},
                        label = { Text("Street") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    OutlinedTextField(
                        value = addressViewModel.city,
                        onValueChange = { addressViewModel.city = it},
                        label = { Text("City") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    OutlinedTextField(
                        value = addressViewModel.country,
                        onValueChange = { addressViewModel.country = it},
                        label = { Text("Country") },
                        modifier = Modifier.fillMaxWidth()
                    )


                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = addressViewModel.zipCode,
                        onValueChange = { addressViewModel.zipCode = it},
                        label = { Text("Zip Code") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = addressViewModel.isDefault,
                            onCheckedChange = {
                                addressViewModel.isDefault = it
                            })

                        Text(text = stringResource(id = R.string.set_as_default))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            try {
                                addressViewModel.addShippingAddress(
                                    context = context,
                                    header = addressViewModel.header,
                                    street = addressViewModel.street,
                                    city = addressViewModel.city,
                                    country = addressViewModel.country,
                                    zipCode = addressViewModel.zipCode,
                                    isDefault = addressViewModel.isDefault)
                                navController.popBackStack()
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }

                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(id = R.string.add_address))
                    }
                }
            }
        }
    }
}
