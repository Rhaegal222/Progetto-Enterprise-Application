// ShippingCard.kt
package com.android.frontend.view.component

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import com.android.frontend.dto.AddressDTO
import com.android.frontend.ui.theme.cardHeightAddress
import com.android.frontend.ui.theme.cardWidth

@Composable
fun AddressCard(shippingAddress: AddressDTO, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeightAddress)
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(
                            onClick = onRemove,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text(
                            text = shippingAddress.header,
                            style = TextStyle(fontSize = 18.sp),
                            maxLines = 1
                        )
                        Text(
                            text = shippingAddress.street,
                            style = TextStyle(fontSize = 16.sp),
                            maxLines = 1
                        )
                        Text(
                            text = "${shippingAddress.city} ${shippingAddress.country}",
                            style = TextStyle(fontSize = 14.sp),
                            maxLines = 1
                        )
                        Text(
                            text = shippingAddress.zipCode,
                            style = TextStyle(fontSize = 14.sp),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
