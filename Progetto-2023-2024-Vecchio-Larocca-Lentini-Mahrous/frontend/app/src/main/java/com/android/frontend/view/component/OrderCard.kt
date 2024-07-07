package com.android.frontend.view.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.frontend.R
import com.android.frontend.dto.OrderDTO
import com.android.frontend.ui.theme.colors.CardColorScheme
import org.threeten.bp.format.DateTimeFormatter

@Composable
fun OrderCard(
    order: OrderDTO,
    dateFormatter: DateTimeFormatter
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, Color.Gray),
        colors = CardColorScheme.cardColors()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp, 0.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.total_cost),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = order.totalCost.toString(),
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(id = R.string.created_at),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = order.createdAt.format(dateFormatter),
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(id = R.string.address),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${order.address.city}, ${order.address.street}",
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(id = R.string.payment_method),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = order.paymentMethod.cardNumber,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(id = R.string.order_items),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                for (item in order.items) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Row {
                        Text(
                            text = stringResource(id = R.string.product_name_),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 32.dp)
                        )
                    }
                    Row {
                        Text(
                            text = item.productName,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 32.dp)
                        )
                    }

                    Row {
                        Text(
                            text = stringResource(id = R.string.quantity),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 32.dp)
                        )
                    }
                    Row {
                        Text(
                            text = item.quantity.toString(),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 32.dp)
                        )
                    }
                }
            }
        }
    }
}
