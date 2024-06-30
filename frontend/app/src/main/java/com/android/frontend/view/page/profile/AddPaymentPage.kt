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
import com.android.frontend.view_models.PaymentViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddPaymentPage(navController: NavHostController) {

    val context = LocalContext.current

    val paymentViewModel = PaymentViewModel()

    Scaffold {
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = paymentViewModel.cardNumber,
                        onValueChange = { paymentViewModel.cardNumber = it},
                        label = { Text("Card Number") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = paymentViewModel.expireMonth,
                            onValueChange = { paymentViewModel.expireMonth = it},
                            label = { Text("Month") },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedTextField(
                            value = paymentViewModel.expireYear,
                            onValueChange = { paymentViewModel.expireYear = it},
                            label = { Text("Year") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    //Owner
                    OutlinedTextField(
                        value = paymentViewModel.owner,
                        onValueChange = { paymentViewModel.owner = it},
                        label = { Text("Owner") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = paymentViewModel.isDefault,
                            onCheckedChange = {
                                paymentViewModel.isDefault = it
                            })

                        Text(text = stringResource(id = R.string.set_as_default))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            try {
                                paymentViewModel.addPaymentCard(
                                    context = context,
                                    cardNumber = paymentViewModel.cardNumber,
                                    expireMonth = paymentViewModel.expireMonth,
                                    expireYear = paymentViewModel.expireYear,
                                    owner = paymentViewModel.owner,
                                    isDefault = paymentViewModel.isDefault)
                                navController.popBackStack()
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }

                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(id = R.string.add_payment_card))
                    }
                }
            }
        }
    }
}


