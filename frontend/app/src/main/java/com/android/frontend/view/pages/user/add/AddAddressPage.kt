package com.android.frontend.view.pages.user.add

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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.android.frontend.navigation.Navigation
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.ui.theme.colors.OutlinedTextFieldColorScheme
import com.android.frontend.view_models.user.AddressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun AddAddressPage(
    navController: NavHostController,
    addressViewModel: AddressViewModel = viewModel()) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val fullNameFocusRequester = remember { FocusRequester() }
    val phoneNumFocusRequester = remember { FocusRequester() }
    val streetFocusRequester = remember { FocusRequester() }
    val additionalInfoFocusRequester = remember { FocusRequester() }
    val postalCodeFocusRequester = remember { FocusRequester() }
    val cityFocusRequester = remember { FocusRequester() }
    val provinceFocusRequester = remember { FocusRequester() }
    val countryFocusRequester = remember { FocusRequester() }

    val fullName by addressViewModel::fullName
    val phoneNum by addressViewModel::phoneNumber
    val street by addressViewModel::street
    val additionalInfo by addressViewModel::additionalInfo
    val postalCode by addressViewModel::postalCode
    val city by addressViewModel::city
    val province by addressViewModel::province
    val country by addressViewModel::country

    val allFieldsValid by derivedStateOf {
        fullName.isNotBlank() && phoneNum.isNotBlank() && street.isNotBlank() &&
                postalCode.isNotBlank() && city.isNotBlank() &&
                province.isNotBlank() && country.isNotBlank()
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.add_address))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Navigation.AddressesPage.route) }) {
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
                        value = fullName,
                        onValueChange = {
                            addressViewModel.fullName = it
                        },
                        label = { Text(stringResource(id = R.string.fullname)) },
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

                    // Aggiungere un menu a discesa con i paesi di tutto il mondo e il prefisso

                    OutlinedTextField(
                        colors = OutlinedTextFieldColorScheme.colors(),
                        singleLine = true,
                        value = phoneNum,
                        onValueChange = {
                            addressViewModel.phoneNumber = it
                        },
                        label = { Text(stringResource(id = R.string.phone_number)) },
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
                        value = street,
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
                        value = additionalInfo,
                        onValueChange = {
                            addressViewModel.additionalInfo = it
                        },
                        label = { Text("Additional Info") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { postalCodeFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(additionalInfoFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        colors = OutlinedTextFieldColorScheme.colors(),
                        singleLine = true,
                        value = postalCode,
                        onValueChange = {
                            addressViewModel.postalCode = it
                        },
                        label = { Text(stringResource(id = R.string.postal_code)) },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { cityFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(postalCodeFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        colors = OutlinedTextFieldColorScheme.colors(),
                        singleLine = true,
                        value = city,
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
                        value = province,
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

                    OutlinedTextField(
                        colors = OutlinedTextFieldColorScheme.colors(),
                        singleLine = true,
                        value = country,
                        onValueChange = {
                            addressViewModel.country = it
                        },
                        label = { Text("Country") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(countryFocusRequester)
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
                        shape = RoundedCornerShape(12.dp),
                        enabled = allFieldsValid,
                        onClick = {
                            try {
                                addressViewModel.addShippingAddress(
                                    context = context,
                                    fullName = fullName,
                                    phoneNumber = phoneNum,
                                    street = street,
                                    additionalInfo = additionalInfo,
                                    postalCode = postalCode,
                                    city = city,
                                    province = province,
                                    country = country,
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
