package com.android.frontend.view.page.profile

import android.annotation.SuppressLint
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
import androidx.compose.ui.graphics.Color
import com.android.frontend.model.SecurePreferences
import com.android.frontend.view_models.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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
                        value = creditCard?:"",
                        onValueChange = { },
                        label = { Text("Card Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


