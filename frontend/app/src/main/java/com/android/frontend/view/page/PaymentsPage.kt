package com.example.frontend.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.model.CurrentDataUtils
import com.android.frontend.navigation.Navigation
import com.android.frontend.view.page.PaymentsMethod
import com.example.frontend.controller.models.PaymentMethodDTO
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.CreditCard

@Composable
fun PaymentsPage(navController: NavHostController) {
    val payments = CurrentDataUtils.PaymentsMethod

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
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
                    CurrentDataUtils.currentPaymentMethodDTO.value = null
                    navController.navigate(Navigation.AddPaymentPage.route)
                },
                modifier = Modifier.height(35.dp),
                buttonName = "Add new"
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            state = rememberLazyGridState(),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(30.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(payments) { item ->
                val payment: MutableState<PaymentMethodDTO?> = remember { mutableStateOf(item) }
                PaymentsMethod(navController = navController,payment = payment)
            }
        }
    }
}


@Composable
fun TransparentGreenButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonName: String
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp, 0.dp, 0.dp) ,
        shape = MaterialTheme.shapes.small.copy(
            topStart = CornerSize(8.dp),
            topEnd = CornerSize(8.dp),
            bottomStart = CornerSize(8.dp),
            bottomEnd = CornerSize(8.dp)
        ),
        contentPadding = ButtonDefaults.ContentPadding,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ){
        Text(text = buttonName)
    }
}