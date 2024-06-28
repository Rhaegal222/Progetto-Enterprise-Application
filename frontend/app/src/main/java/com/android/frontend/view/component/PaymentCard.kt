package com.android.frontend.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.android.frontend.R
import com.android.frontend.controller.models.PaymentMethodDTO


val cardWidth = 320.dp
val cardHeight = cardWidth / 1.586f

@Composable
fun PaymentCard(payment: PaymentMethodDTO, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Card(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight)
        ) {
            Box(modifier = Modifier.background(Color(0xFF00A35D))) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
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
                            Row {

                                Text(
                                    text = "VALID THRU",
                                    style = TextStyle(color = Color.White, fontSize = 10.sp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${payment.expireMonth}/${payment.expireYear}",
                                    style = TextStyle(color = Color.White, fontSize = 16.sp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = payment.owner,
                                style = TextStyle(color = Color.White, fontSize = 16.sp)
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Spacer(modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth())
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
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 10.dp, y = -10.dp) // Sposta il bottone fuori dall'angolo
                .clip(CircleShape)
                .background(Color.Red)
                .size(30.dp) // Aumenta la dimensione del cerchio
                .padding(4.dp) // Aggiunge padding intorno all'icona
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp) // Dimensione dell'icona
            )
        }
    }
}
