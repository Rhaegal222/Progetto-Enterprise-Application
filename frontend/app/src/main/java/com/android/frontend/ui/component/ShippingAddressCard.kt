import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.frontend.dto.AddressDTO
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.ui.theme.colors.TextButtonColorScheme

@Composable
fun ShippingAddressCard(shippingAddress: AddressDTO, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp)
        ) {
            // Intestazione "Predefinita"
            Text(
                text = "Predefinita: amazon",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Nome in grassetto
            Text(
                text = shippingAddress.fullName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Indirizzo
            AddressRow(label = "Via Risorgimento, 80", value = "")
            Spacer(modifier = Modifier.height(4.dp))
            AddressRow(label = "Comerconi, Vibo Valentia 89844", value = "")
            Spacer(modifier = Modifier.height(4.dp))
            AddressRow(label = "Italia", value = "")
            Spacer(modifier = Modifier.height(4.dp))
            AddressRow(label = "Numero di telefono:", value = shippingAddress.phoneNumber)

            Spacer(modifier = Modifier.height(8.dp))

            // Link per aggiungere le istruzioni di consegna
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
                Button(
                    onClick = { },
                    colors = ButtonColorScheme.buttonColors()
                ) {
                    Text(text = "Modifica")
                }

                Button(
                    onClick = onRemove,
                    colors = ButtonColorScheme.buttonColors()
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
