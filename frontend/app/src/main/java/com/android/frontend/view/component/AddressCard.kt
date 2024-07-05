package com.android.frontend.view.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.frontend.R
import com.android.frontend.dto.AddressDTO
import com.android.frontend.ui.theme.colors.CardColorScheme
import com.android.frontend.ui.theme.colors.OutlinedButtonColorScheme
import com.android.frontend.ui.theme.colors.TextButtonColorScheme

@Composable
fun AddressCard(address: AddressDTO, onRemove: () -> Unit) {
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
                if (address.isDefault) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.default_address),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = address.fullName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                AddressRow(value = address.street)

                Spacer(modifier = Modifier.height(4.dp))

                AddressRow(value = address.additionalInfo)

                Spacer(modifier = Modifier.height(4.dp))

                Row() {

                    if (address.city.isNotEmpty())
                        AddressRow(value = address.city + ", ")


                    if (address.postalCode.isNotEmpty())
                        AddressRow(value = address.postalCode + " ")

                    if (address.province.isNotEmpty())
                        AddressRow(value = address.province)

                }

                Spacer(modifier = Modifier.height(4.dp))

                AddressRow(value = address.country)

                Spacer(modifier = Modifier.height(8.dp))

                if (address.additionalInfo.isEmpty())
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { },
                        colors = TextButtonColorScheme.textButtonColors()
                    ) {
                        Text(text = stringResource(id = R.string.add_additional_info))
                    }

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedButtonColorScheme.outlinedButtonColors()
                    ) {
                        Text(text = stringResource(id = R.string.edit))
                    }

                    OutlinedButton(
                        onClick = onRemove,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedButtonColorScheme.outlinedButtonColors()
                    ) {
                        Text(text = stringResource(id = R.string.remove))
                    }
                }
            }
        }
    }
}

@Composable
fun AddressRow(value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (value.isNotEmpty()) {
            Text(text = value)
        }
    }
}
