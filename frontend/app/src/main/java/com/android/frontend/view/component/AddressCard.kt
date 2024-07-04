package com.android.frontend.view.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.frontend.R
import com.android.frontend.dto.AddressDTO
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.ui.theme.colors.CardColorScheme
import com.android.frontend.ui.theme.colors.OutlinedButtonColorScheme
import com.android.frontend.ui.theme.colors.TextButtonColorScheme

@Composable
fun AddressCard(address: AddressDTO, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardColorScheme.cardColors()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            if (address.isDefault) {
                Text(
                    text = stringResource(R.string.default_address),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            } else {
                Text(
                    text = stringResource(R.string.set_as_default),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = address.fullName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            AddressRow(label = "Via Risorgimento, 80", value = "")
            Spacer(modifier = Modifier.height(4.dp))
            AddressRow(label = "Comerconi, Vibo Valentia 89844", value = "")
            Spacer(modifier = Modifier.height(4.dp))
            AddressRow(label = "Italia", value = "")
            Spacer(modifier = Modifier.height(4.dp))
            AddressRow(label = "Numero di telefono:", value = address.phoneNumber)

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { },
                colors = TextButtonColorScheme.textButtonColors()
            ) {
                Text(text = "Aggiungi istruzioni di consegna")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pulsanti Modifica e Rimuovi
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { },
                    colors = OutlinedButtonColorScheme.outlinedButtonColors()
                ) {
                    Text(text = "Modifica")
                }

                OutlinedButton(
                    onClick = onRemove,
                    colors = OutlinedButtonColorScheme.outlinedButtonColors()
                ) {
                    Text(text = "Rimuovi")
                }
            }
        }
    }
}

@Composable
fun AddressRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)

        Spacer(modifier = Modifier.width(8.dp))

        if (value.isNotEmpty()) {
            Text(text = value)
        }
    }
}
