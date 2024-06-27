package com.android.frontend.view.page.profile

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.android.frontend.model.SecurePreferences
import com.android.frontend.view_models.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddPaymentPage(navController: NavHostController) {

    val context = LocalContext.current

    val paymentViewModel = PaymentViewModel()

    val mText = "paymentUpdated"
    val notUpdated = "paymentNotUpdated"

    /*
    val updated = paymentViewModel.updated
    val localUpdate = paymentViewModel.localUpdated

    if (localUpdate.value) {
        localUpdate.value = false
        if (updated.value) {
            Toast.makeText(context, mText, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, notUpdated, Toast.LENGTH_LONG).show()
        }
    }
     */

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        }
    ) {
        LazyColumn {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                ) {

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = paymentViewModel.creditCard,
                        onValueChange = { paymentViewModel.creditCard = it},
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

                    //Checkbox to set as default payment method
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = paymentViewModel.isDefault,
                            onCheckedChange = {
                                paymentViewModel.isDefault = it
                            })

                        Text("Set as default payment method")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            paymentViewModel.addPaymentCard(
                                context = context,
                                creditCard = paymentViewModel.creditCard,
                                expiryDate = paymentViewModel.expireMonth + "/" + paymentViewModel.expireYear,
                                owner = paymentViewModel.owner,
                                isDefault = paymentViewModel.isDefault
                            )
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Payment Card")
                    }
                }
            }
        }
    }
}


