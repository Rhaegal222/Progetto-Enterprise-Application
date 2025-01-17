package com.android.frontend.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.android.frontend.R
import com.android.frontend.dto.PaymentMethodDTO
import com.android.frontend.ui.theme.cardHeight
import com.android.frontend.ui.theme.cardWidth
import com.android.frontend.ui.theme.colors.IconButtonColorScheme

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
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp))

                    val cardNumber = payment.cardNumber
                    Text(
                        text = "**** **** **** ${cardNumber.takeLast(4)}",
                        style = TextStyle(fontSize = 22.sp, letterSpacing = 3.sp, textAlign = TextAlign.Center),
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
                                    style = TextStyle(fontSize = 10.sp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${payment.expireMonth}/${payment.expireYear}",
                                    style = TextStyle(fontSize = 16.sp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = payment.owner,
                                style = TextStyle(fontSize = 16.sp)
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
                .offset(x = 10.dp, y = (-10).dp)
                .clip(CircleShape)
                .size(30.dp)
                .padding(4.dp),
            colors = IconButtonColorScheme.iconButtonColors(
                containerColor = Color.Red,
                contentColor = Color.Black,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
