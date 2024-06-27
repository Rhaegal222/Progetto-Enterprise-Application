package com.android.frontend.view.page.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.frontend.model.SecurePreferences
import com.android.frontend.view_models.PaymentViewModel

@Composable
fun AddPaymentPage(navController: NavHostController) {

    val context = LocalContext.current

    val paymentViewModel = PaymentViewModel()

    val paymentMethod = SecurePreferences.getCurrentPaymentMethod(context)

    val paymentMethodId = paymentMethod?.id
    val creditCard = paymentMethod?.creditCard
    val owner = paymentMethod?.owner
    val expiryDate = paymentMethod?.expiryDate
    val isDefault = paymentMethod?.isDefault

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

    Column {
        Text(
            text = "Add Payment Method",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.padding(16.dp)) {
        }
    }
}


