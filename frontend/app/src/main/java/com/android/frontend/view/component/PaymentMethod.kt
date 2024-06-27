package com.android.frontend.view.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.frontend.navigation.Navigation
import com.example.frontend.controller.models.PaymentMethodDTO
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Edit
import compose.icons.fontawesomeicons.solid.Trash

@Composable
fun PaymentMethod(navController: NavController, payment: PaymentMethodDTO ) {

    val creditCard = payment.creditCard
    val expiryDate = payment.expiryDate
    val owner = payment.owner
    val isDefault = payment.isDefault

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            hoveredElevation = 5.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = creditCard,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    )
                }
                Column() {
                    Row() {
                        IconButton(
                            onClick = {
                                // EditPayment(payment = payment)
                                navController.navigate(Navigation.AddPaymentPage.route)
                            },
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Edit,
                                contentDescription = "Edit ",
                                modifier = Modifier
                                    .height(16.dp),

                                )
                        }
                        IconButton(
                            onClick = {
                                navController.navigate(Navigation.PaymentsPage.route)
                            }

                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Trash,
                                contentDescription = "Remove",
                                modifier = Modifier
                                    .height(16.dp)
                            )

                        }
                    }

                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "expDate",
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = expiryDate)

            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "owner",
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(end = 8.dp)
                )

            }
        }
    }
}
