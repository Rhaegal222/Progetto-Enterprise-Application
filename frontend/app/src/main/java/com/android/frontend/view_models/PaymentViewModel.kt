package com.example.frontend.view_models

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.android.frontend.model.CurrentDataUtils
import com.example.frontend.controller.models.PaymentMethodCreateDTO
import com.example.frontend.controller.models.PaymentMethodDTO
import com.example.frontend.service.PaymentService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.awaitResponse

class PaymentViewModel {

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val paymentService: PaymentService = retrofit.create(PaymentService::class.java)

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    val updated: MutableState<Boolean> = mutableStateOf(false)
    val localUpdated: MutableState<Boolean> = mutableStateOf(false)

    fun updatePayment(payment: PaymentMethodDTO) {
        coroutineScope.launch {
            try {
                if (payment.id != null) {
                    val response = withContext(Dispatchers.IO) {
                        paymentService.updatePaymentMethod(payment.id, payment).awaitResponse()
                    }
                    if (response.isSuccessful) {
                        updated.value = true
                    } else {
                        updated.value = false
                    }
                }
                //MainRouter.changePage(Navigation.PaymentsPage)
            } catch (e: Exception) {
                updated.value = false
                e.printStackTrace()
            }
            CurrentDataUtils.retrieveCurrentUser()
            CurrentDataUtils.retrievePaymentsMethod()
            localUpdated.value = true
        }
    }

    fun createPayment(payment: PaymentMethodCreateDTO) {
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    Log.d("API_CALL", "Sending request to create payment: $payment")
                    Log.d("PAYMENT", "expireDate.value: ${payment.expiryDate}")
                    paymentService.createPaymentMethod(payment).awaitResponse()
                }
                if (response.isSuccessful) {
                    updated.value = true
                    Log.d("API_CALL", "Payment method created successfully")
                } else {
                    updated.value = false
                    Log.d("API_CALL", "Failed to create payment method: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                updated.value = false
                e.printStackTrace()
                Log.d("API_CALL", "Exception occurred: ${e.message}")
            }
            CurrentDataUtils.retrieveCurrentUser()
            CurrentDataUtils.retrievePaymentsMethod()
            localUpdated.value = true
        }
    }


    fun deletePayment(id: String) {
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    paymentService.deletePaymentMethod(id).awaitResponse()
                }
                if (response.isSuccessful) {
                    updated.value = true
                } else {
                    updated.value = false
                }
                //MainRouter.changePage(Navigation.PaymentsPage)
            } catch (e: Exception) {
                updated.value = false
                e.printStackTrace()
            }
            CurrentDataUtils.retrieveCurrentUser()
            CurrentDataUtils.retrievePaymentsMethod()
            localUpdated.value = true
        }
    }
}