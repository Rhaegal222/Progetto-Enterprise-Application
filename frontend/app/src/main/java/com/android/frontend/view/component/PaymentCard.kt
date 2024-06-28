package com.android.frontend.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import com.android.frontend.R
import com.android.frontend.controller.models.PaymentMethodDTO

@Composable
fun PaymentCard(payment: PaymentMethodDTO) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(200.dp)
    ) {
        Box(modifier = Modifier.background(Color(0xFF00A35D))) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                // Banner per il logo della carta di credito
                Box(
                    // Colora il banner di nero
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color.Black)
                )

                val cardNumber = payment.cardNumber
                Text(
                    text = "**** **** **** ${cardNumber.takeLast(4)}",
                    style = TextStyle(color = Color.White, fontSize = 22.sp, letterSpacing = 3.sp, textAlign = TextAlign.Center),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(

                        ) {
                            Text(
                                text = "VALID THRU",
                                style = TextStyle(color = Color.White, fontSize = 10.sp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "00/00",
                                style = TextStyle(color = Color.White, fontSize = 16.sp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "MAX MUSTERMANN",
                            style = TextStyle(color = Color.White, fontSize = 16.sp)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Spacer(modifier = Modifier.height(40.dp).fillMaxWidth())
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.mastercard_logo),
                                contentDescription = "Bank Logo",
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(50.dp)
                                    .padding(0.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        }
    }
}
