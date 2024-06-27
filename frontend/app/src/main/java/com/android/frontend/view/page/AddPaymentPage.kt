package com.android.frontend.view.page

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.frontend.model.CurrentDataUtils
import com.android.frontend.navigation.Navigation
import com.example.frontend.controller.models.PaymentMethodCreateDTO
import com.example.frontend.view_models.PaymentViewModel

@Composable
fun AddPaymentPage(navController: NavHostController) {
    val payment = CurrentDataUtils.currentPaymentMethodDTO
    val paymentViewModel = PaymentViewModel()
    val creditCardText = remember { mutableStateOf(payment.value?.creditCard ?: "") }
    val expireDate = remember { mutableStateOf(payment.value?.expiryDate ?: "") }
    val ownerText = remember { mutableStateOf(payment.value?.owner ?: "") }
    val isDefaultBoolean = remember { mutableStateOf(payment.value?.isDefault ?: false) }

    val context = LocalContext.current
    val mText = "paymentUpdated"
    val notUpdated = "paymentNotUpdated"

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

    // Log per verificare il valore di expireDate.value
    Log.d("AddPaymentPage", "expireDate.value: ${expireDate.value}")

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())) {

        val inputBorderColor = Color.Gray
        val textColor = Color.Black
        val iconColor = Color.Black

        OutlinedTextField(
            value = creditCardText.value,
            onValueChange = { creditCardText.value = it },
            label = {
                Text(
                    text = "Credit Card Number",
                    color = textColor
                )
            },
            leadingIcon = {
                Icon(Icons.Default.CreditCard, contentDescription = null, tint = iconColor)
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = textColor,
                focusedBorderColor = inputBorderColor,
                unfocusedBorderColor = inputBorderColor,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = ownerText.value,
            onValueChange = { ownerText.value = it },
            label = {
                Text(
                    text = "Owner",
                    color = textColor
                )
            },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = iconColor)
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = textColor,
                focusedBorderColor = inputBorderColor,
                unfocusedBorderColor = inputBorderColor,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = expireDate.value,
            onValueChange = { expireDate.value = it },
            label = {
                Text(
                    text = "Exp Date (yyyy-MM-dd)",
                    color = textColor
                )
            },
            leadingIcon = {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = iconColor)
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = textColor,
                focusedBorderColor = inputBorderColor,
                unfocusedBorderColor = inputBorderColor,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "setAsDefault")
            RadioButton(selected = isDefaultBoolean.value, onClick = { isDefaultBoolean.value = !isDefaultBoolean.value })
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (payment.value?.id != null) {
                    paymentViewModel.updatePayment(payment.value?.copy(
                        creditCard = creditCardText.value,
                        expiryDate = expireDate.value,
                        owner = ownerText.value,
                        isDefault = isDefaultBoolean.value
                    )!!)
                } else {
                    paymentViewModel.createPayment(payment = PaymentMethodCreateDTO(
                        creditCard = creditCardText.value,
                        expiryDate = expireDate.value,
                        owner = ownerText.value,
                        isDefault = isDefaultBoolean.value
                    ))
                }
                navController.navigate(Navigation.PaymentsPage.route)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Text(
                text = if(payment.value?.id != null) "Edit" else "Create",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
