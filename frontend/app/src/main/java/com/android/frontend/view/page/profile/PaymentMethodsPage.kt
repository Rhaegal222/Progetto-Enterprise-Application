package com.android.frontend.view.page.profile

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.navigation.Navigation
import com.android.frontend.ui.theme.TransparentGreenButton
import com.android.frontend.view.component.PaymentMethod
import com.android.frontend.view_models.PaymentViewModel
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.CreditCard

@Composable
fun PaymentMethodsPage(navController: NavHostController, paymentViewModel: PaymentViewModel = viewModel()) {

    val context = LocalContext.current

    val payments = paymentViewModel.paymentMethodsLiveData.observeAsState().value
    paymentViewModel.getAllPaymentMethods(context)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.CreditCard,
                contentDescription = "Payments Methods",
                modifier = Modifier.height(18.dp)
            )
            Text(
                text = stringResource(id = R.string.payment_methods),
                style = TextStyle(fontSize = 20.sp),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )

            TransparentGreenButton(
                onClick = {
                    navController.navigate(Navigation.AddPaymentPage.route)
                },
                modifier = Modifier.height(35.dp),
                buttonName = "Add new"
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(8.dp)
            ) {
                for (payment in payments ?: emptyList()) {
                    item {
                        Log.d("Payment", payment.toString())
                        Text(text = payment.creditCard, style = TextStyle(fontSize = 18.sp))
                        Text(text = payment.owner, style = TextStyle(fontSize = 18.sp))
                        Text(text = payment.expiryDate, style = TextStyle(fontSize = 18.sp))
                    }
                }
            }
        }
    }
}