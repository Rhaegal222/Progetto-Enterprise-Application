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
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.ui.theme.colors.OutlinedTextFieldColorScheme
import com.android.frontend.view_models.user.AddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun AddAddressPage(navController: NavHostController, addressViewModel: AddressViewModel = viewModel()) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val fullNameFocusRequester = remember { FocusRequester() }
    val phoneNumFocusRequester = remember { FocusRequester() }
    val streetFocusRequester = remember { FocusRequester() }
    val additionalInfoFocusRequester = remember { FocusRequester() }
    val zipCodeFocusRequester = remember { FocusRequester() }
    val cityFocusRequester = remember { FocusRequester() }
    val provinceFocusRequester = remember { FocusRequester() }
    val countryFocusRequester = remember { FocusRequester() }

    var fullName by remember { mutableStateOf("") }
    var phoneNum by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var additionalInfo by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }

    val allFieldsValid by derivedStateOf {
        fullName.isNotBlank() && street.isNotBlank() &&
        zipCode.isNotBlank() && city.isNotBlank() &&
        province.isNotBlank() && country.isNotBlank()
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.add_address).uppercase())
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
                        colors = OutlinedTextFieldColorScheme.colors(),
                        singleLine = true,
                        value = addressViewModel.fullName,
                        onValueChange = {
                            addressViewModel.fullName = it
                        },
                        label = { Text(stringResource(id = R.string.firstname)) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { phoneNumFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(fullNameFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        colors = OutlinedTextFieldColorScheme.colors(),
                        singleLine = true,
                        value = addressViewModel.phoneNumber,
                        onValueChange = {
                            addressViewModel.phoneNumber = it
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
                            .focusRequester(phoneNumFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        colors = OutlinedTextFieldColorScheme.colors(),
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
                            onNext = { additionalInfoFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(streetFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        colors = OutlinedTextFieldColorScheme.colors(),
                        singleLine = true,
                        value = addressViewModel.additionalInfo,
                        onValueChange = {
                            addressViewModel.additionalInfo = it
                        },
                        label = { Text("Additional Info") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { zipCodeFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(additionalInfoFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        colors = OutlinedTextFieldColorScheme.colors(),
                        singleLine = true,
                        value = addressViewModel.zipCode,
                        onValueChange = {
                            addressViewModel.zipCode = it
                        },
                        label = { Text("Zip Code") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { cityFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(zipCodeFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        colors = OutlinedTextFieldColorScheme.colors(),
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
                            onNext = { provinceFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(cityFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        colors = OutlinedTextFieldColorScheme.colors(),
                        singleLine = true,
                        value = addressViewModel.province,
                        onValueChange = {
                            addressViewModel.province = it
                        },
                        label = { Text("Province") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { countryFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(provinceFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

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

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        enabled = allFieldsValid,
                        onClick = {
                            try {
                                addressViewModel.addShippingAddress(
                                    context = context,
                                    fullName = addressViewModel.fullName,
                                    phoneNumber = addressViewModel.phoneNumber,
                                    street = addressViewModel.street,
                                    additionalInfo = addressViewModel.additionalInfo,
                                    zipCode = addressViewModel.zipCode,
                                    city = addressViewModel.city,
                                    province = addressViewModel.province,
                                    country = addressViewModel.country,
                                    isDefault = addressViewModel.isDefault
                                )
                                navController.popBackStack()
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }

                        },
                        colors = ButtonColorScheme.buttonColors(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(id = R.string.add_address))
                    }
                }
            }
        }
    }
}
