package com.android.frontend.view_models

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.PaymentMethodCreateDTO
import com.example.frontend.controller.models.PaymentMethodDTO
import com.example.frontend.service.PaymentService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class PaymentViewModel {

    private val paymentService: PaymentService = RetrofitInstance.paymentApi

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    val updated: MutableState<Boolean> = mutableStateOf(false)
    val localUpdated: MutableState<Boolean> = mutableStateOf(false)

    fun getPaymentMethods() {
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    paymentService.getPaymentMethods().awaitResponse()
                }
                if (response.isSuccessful) {
                    val paymentMethods = response.body()
                    Log.d("API_CALL", "Payment methods fetched successfully: $paymentMethods")
                    //MainRouter.changePage(Navigation.PaymentsPage)
                } else {
                    Log.d("API_CALL", "Failed to fetch payment methods: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("API_CALL", "Exception occurred: ${e.message}")
            }
        }
    }
    fun updatePayment(payment: PaymentMethodDTO) {
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    paymentService.updatePaymentMethod(payment.id, payment).awaitResponse()
                }
                updated.value = response.isSuccessful
                //MainRouter.changePage(Navigation.PaymentsPage)
            } catch (e: Exception) {
                updated.value = false
                e.printStackTrace()
            }
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
            localUpdated.value = true
        }
    }


    fun deletePayment(id: String) {
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    paymentService.deletePaymentMethod(id).awaitResponse()
                }
                updated.value = response.isSuccessful
                //MainRouter.changePage(Navigation.PaymentsPage)
            } catch (e: Exception) {
                updated.value = false
                e.printStackTrace()
            }
            localUpdated.value = true
        }
    }
}