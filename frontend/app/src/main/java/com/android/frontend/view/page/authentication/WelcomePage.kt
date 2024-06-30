package com.android.frontend.view.page.authentication

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.navigation.Navigation
import com.android.frontend.persistence.CurrentDataUtils

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WelcomePage(navController: NavController) {

    var baseUrl = remember { CurrentDataUtils.baseUrl }
    val checkedState = remember { mutableStateOf(baseUrl == "http://http://10.0.2.2:8080/") }

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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = checkedState.value,
                    onCheckedChange = {
                        if (checkedState.value) {
                            checkedState.value = false
                            baseUrl = "https://192.168.160.200:8080/"
                            CurrentDataUtils.baseUrl = baseUrl
                        } else {
                            checkedState.value = true
                            baseUrl = "http://10.0.2.2:8080/"
                            CurrentDataUtils.baseUrl = baseUrl
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (checkedState.value) {
                    Text(
                        text = "Gaetano",
                        style = typography.bodyMedium,
                        color = colors.onBackground
                    )
                } else {
                    Text(
                        text = "Localhost",
                        style = typography.bodyMedium,
                        color = colors.onBackground
                    )
                }
            }

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
                color = colors.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.welcome_description),
                style = typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = colors.onBackground,
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
                        contentColor = colors.primary
                    ),
                    border = BorderStroke(1.dp, colors.primary)
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
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
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
