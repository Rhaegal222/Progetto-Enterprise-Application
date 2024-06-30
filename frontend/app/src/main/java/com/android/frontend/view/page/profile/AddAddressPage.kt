package com.android.frontend.view.page.profile

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.frontend.R
import com.android.frontend.view_models.AddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun AddAddressPage(navController: NavHostController, addressViewModel: AddressViewModel = viewModel()) {

    val context = LocalContext.current

    val focusManager = LocalFocusManager.current

    val firstNameFocusRequester = remember { FocusRequester() }
    val lastNameFocusRequester = remember { FocusRequester() }
    val streetFocusRequester = remember { FocusRequester() }
    val cityFocusRequester = remember { FocusRequester() }
    val countryFocusRequester = remember { FocusRequester() }
    val zipCodeFocusRequester = remember { FocusRequester() }

    val allFieldsValid by derivedStateOf {
        addressViewModel.firstname.isNotEmpty() &&
                addressViewModel.lastname.isNotEmpty() &&
                addressViewModel.street.isNotEmpty() &&
                addressViewModel.city.isNotEmpty() &&
                addressViewModel.country.isNotEmpty() &&
                addressViewModel.zipCode.isNotEmpty()
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.add_address).uppercase(),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        singleLine = true,
                        value = addressViewModel.firstname,
                        onValueChange = {
                            addressViewModel.firstname = it
                        },
                        label = { Text(stringResource(id = R.string.firstname)) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { lastNameFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(firstNameFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        singleLine = true,
                        value = addressViewModel.lastname,
                        onValueChange = {
                            addressViewModel.lastname = it
                        },
                        label = { Text(stringResource(id = R.string.lastname)) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { streetFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(lastNameFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        singleLine = true,
                        value = addressViewModel.street,
                        onValueChange = {
                            addressViewModel.street = it
                        },
                        label = { Text("Street") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { cityFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(streetFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        singleLine = true,
                        value = addressViewModel.city,
                        onValueChange = {
                            addressViewModel.city = it
                        },
                        label = { Text("City") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { countryFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(cityFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        singleLine = true,
                        value = addressViewModel.country,
                        onValueChange = {
                            addressViewModel.country = it
                        },
                        label = { Text("Country") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { zipCodeFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(countryFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        singleLine = true,
                        value = addressViewModel.zipCode,
                        onValueChange = {
                            addressViewModel.zipCode = it
                        },
                        label = { Text("Zip Code") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = if (allFieldsValid) ImeAction.Done else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(zipCodeFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = addressViewModel.isDefault,
                            onCheckedChange = {
                                addressViewModel.isDefault = it
                            })

                        Text(text = stringResource(id = R.string.set_as_default))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        enabled = allFieldsValid,
                        onClick = {
                            try {
                                addressViewModel.addShippingAddress(
                                    context = context,
                                    firstname = addressViewModel.firstname,
                                    lastname = addressViewModel.lastname,
                                    street = addressViewModel.street,
                                    city = addressViewModel.city,
                                    country = addressViewModel.country,
                                    zipCode = addressViewModel.zipCode,
                                    isDefault = addressViewModel.isDefault
                                )
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
