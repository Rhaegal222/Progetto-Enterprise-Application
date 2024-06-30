package com.android.frontend.view.page.authentication

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.frontend.R
import com.android.frontend.navigation.Navigation

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WelcomePage(navController: NavController) {

    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val size = with(LocalDensity.current) {
        DpSize(
            width = LocalConfiguration.current.screenWidthDp.dp,
            height = LocalConfiguration.current.screenHeightDp.dp
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.background // Colore di sfondo della schermata
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ServerSwitch()

            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "ChatBot Image",
                modifier = Modifier
                    .height(size.height * 0.4f)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.welcome),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground, // Usa un colore scuro per il testo "Welcome"
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.welcome_description),
                style = typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = colors.onBackground, // Colore del testo della descrizione
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                OutlinedButton(
                    onClick = {
                        navController.navigate(Navigation.LoginPage.route)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = colors.background, // Sfondo bianco o nero a seconda del tema
                        contentColor = colors.onBackground // Testo nero o bianco a seconda del tema
                    ),
                    border = BorderStroke(1.dp, colors.onBackground) // Contorno nero o bianco a seconda del tema
                ) {
                    Text(
                        text = stringResource(id = R.string.login).uppercase(),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        navController.navigate(Navigation.SignupPage.route)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.onBackground, // Sfondo nero o bianco a seconda del tema
                        contentColor = colors.background // Testo bianco o nero a seconda del tema
                    ),
                    border = BorderStroke(1.dp, colors.onBackground) // Bordo nero o bianco a seconda del tema
                ) {
                    Text(
                        text = stringResource(id = R.string.sign_up).uppercase(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}