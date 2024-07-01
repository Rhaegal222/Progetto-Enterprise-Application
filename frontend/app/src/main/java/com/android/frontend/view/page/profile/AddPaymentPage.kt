package com.android.frontend.view.page.profile

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.frontend.R
import com.android.frontend.ui.theme.colors.ButtonColorScheme
import com.android.frontend.ui.theme.colors.OutlinedTextFieldColorScheme
import com.android.frontend.view_models.user.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun AddPaymentPage(navController: NavHostController, paymentViewModel: PaymentViewModel = viewModel()) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val expireMonthFocusRequester = remember { FocusRequester() }
    val expireYearFocusRequester = remember { FocusRequester() }
    val ownerFocusRequester = remember { FocusRequester() }

    var cardNumber by remember { mutableStateOf("") }
    var formattedCardNumber by remember { mutableStateOf(TextFieldValue("")) }

    var expireMonth by remember { mutableStateOf("") }
    var expireYear by remember { mutableStateOf("") }
    var owner by remember { mutableStateOf("") }

    val allFieldsValid by derivedStateOf {
        cardNumber.length == 16 && expireMonth.length == 2 &&
        expireYear.length == 2 && owner.isNotEmpty()
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.add_payment_card).uppercase(),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        }
    )  { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        colors = OutlinedTextFieldColorScheme.colors(),
                        singleLine = true,
                        value = formattedCardNumber,
                        onValueChange = { newTextFieldValue ->
                            val regex = Regex("^[0-9]{0,16}$")
                            val rawNumber = newTextFieldValue.text.replace(" ", "")
                            if (regex.matches(rawNumber) && rawNumber.length <= 16) {
                                cardNumber = rawNumber
                                val formattedText = rawNumber.chunked(4).joinToString(" ")
                                val cursorPosition = newTextFieldValue.selection.end
                                val newCursorPosition = calculateNewCursorPosition(
                                    oldText = formattedCardNumber.text,
                                    newText = formattedText,
                                    oldCursorPosition = cursorPosition
                                )
                                val formattedSelection = TextFieldValue(
                                    text = formattedText,
                                    selection = TextRange(newCursorPosition)
                                )
                                formattedCardNumber = formattedSelection
                                paymentViewModel.cardNumber = cardNumber
                            }
                            if (cardNumber.length == 16) {
                                expireMonthFocusRequester.requestFocus()
                            }
                        },
                        label = { Text("Card Number") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = if (allFieldsValid) ImeAction.Done else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { expireMonthFocusRequester.requestFocus() }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            colors = OutlinedTextFieldColorScheme.colors(),
                            singleLine = true,
                            value = expireMonth,
                            onValueChange = {
                                val regex = Regex("^[0-9]{0,2}$")
                                if (regex.matches(it)) {
                                    expireMonth = it
                                    paymentViewModel.expireMonth = it
                                }
                                if (expireMonth.length == 2) {
                                    if (expireMonth > "00" && expireMonth < "13")
                                        expireYearFocusRequester.requestFocus()
                                    else
                                        expireMonth = ""
                                }
                            },
                            label = { Text("Month") },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = if (allFieldsValid) ImeAction.Done else ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { expireYearFocusRequester.requestFocus() }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(expireMonthFocusRequester)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        OutlinedTextField(
                            colors = OutlinedTextFieldColorScheme.colors(),
                            singleLine = true,
                            value = expireYear,
                            onValueChange = {
                                val regex = Regex("^[0-9]{0,2}$")
                                if (regex.matches(it)) {
                                    expireYear = it
                                    paymentViewModel.expireYear = it
                                }
                                if (expireYear.length == 2) {
                                    ownerFocusRequester.requestFocus()
                                }
                            },
                            label = { Text("Year") },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = if (allFieldsValid) ImeAction.Done else ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { ownerFocusRequester.requestFocus() }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(expireYearFocusRequester)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        colors = OutlinedTextFieldColorScheme.colors(),
                        singleLine = true,
                        value = owner,
                        onValueChange = {
                            owner = it
                            paymentViewModel.owner = it
                        },
                        label = { Text("Owner") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = if (allFieldsValid) ImeAction.Done else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(ownerFocusRequester)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = paymentViewModel.isDefault,
                            onCheckedChange = {
                                paymentViewModel.isDefault = it
                            })

                        Text(text = stringResource(id = R.string.set_as_default))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        enabled = allFieldsValid,
                        onClick = {
                            try {
                                paymentViewModel.addPaymentCard(
                                    context = context,
                                    cardNumber = cardNumber,
                                    expireMonth = expireMonth,
                                    expireYear = expireYear,
                                    owner = owner,
                                    isDefault = paymentViewModel.isDefault
                                )
                                navController.popBackStack()
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonColorScheme.buttonColors(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(id = R.string.add_payment_card))
                    }
                }
            }
        }
    }
}

fun calculateNewCursorPosition(oldText: String, newText: String, oldCursorPosition: Int): Int {
    var newCursorPosition = oldCursorPosition
    val spaceAdded = newText.count { it == ' ' } - oldText.count { it == ' ' }
    newCursorPosition += spaceAdded
    return newCursorPosition.coerceAtMost(newText.length)
}
