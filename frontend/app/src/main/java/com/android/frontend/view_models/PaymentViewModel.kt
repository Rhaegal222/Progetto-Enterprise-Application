package com.android.frontend.view_models

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.translation.ViewTranslationResponse
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.PaymentMethodCreateDTO
import com.android.frontend.controller.models.ProductDTO
import com.android.frontend.model.SecurePreferences
import com.example.frontend.controller.models.PaymentMethodDTO
import com.android.frontend.service.PaymentService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class PaymentViewModel : ViewModel() {

    private val paymentMethods = MutableLiveData<List<PaymentMethodDTO>>()
    val paymentMethodsLiveData: LiveData<List<PaymentMethodDTO>> get() = paymentMethods

    var paymentMethodId by mutableStateOf("")
    var creditCard by mutableStateOf("")
    var owner by mutableStateOf("")
    var expireMonth by mutableStateOf("")
    var expireYear by mutableStateOf("")
    var isDefault by mutableStateOf(false) // True or False

    private val paymentService: PaymentService = RetrofitInstance.paymentApi

    fun addPaymentCard(context: Context, creditCard: String, expiryDate: String, owner: String, isDefault: Boolean) {
        viewModelScope.launch {
            val accessToken = SecurePreferences.getAccessToken(context)
            Log.d("PaymentViewModel", "Access Token: $accessToken")
            val paymentMethod = PaymentMethodCreateDTO(creditCard, expiryDate, owner, isDefault)
            val call = paymentService.addPaymentMethod("Bearer $accessToken", paymentMethod)
            call.enqueue(object : Callback<PaymentMethodDTO> {
                override fun onResponse(
                    call: Call<PaymentMethodDTO>,
                    response: Response<PaymentMethodDTO>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { paymentMethod ->
                            Log.d("PaymentViewModel", "Added payment method: $paymentMethod")
                        }
                    } else {
                        Log.e(
                            "PaymentViewModel",
                            "Failed to add payment method: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: Call<PaymentMethodDTO>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("PaymentViewModel", "Timeout error adding payment method", t)
                    } else {
                        Log.e("PaymentViewModel", "Error adding payment method", t)
                    }
                }
            })
        }
    }

    fun getAllPaymentMethods(context: Context) {
        viewModelScope.launch {
            val accessToken = SecurePreferences.getAccessToken(context)
            val call = paymentService.getAllPaymentMethods("Bearer $accessToken")
            call.enqueue(object : Callback<List<PaymentMethodDTO>> {
                override fun onResponse(
                    call: Call<List<PaymentMethodDTO>>,
                    response: Response<List<PaymentMethodDTO>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { paymentMethodsList ->
                            paymentMethods.value = paymentMethodsList
                        }
                    } else {
                        Log.e(
                            "PaymentViewModel",
                            "Failed to fetch payment methods: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: Call<List<PaymentMethodDTO>>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("PaymentViewModel", "Timeout error fetching payment methods", t)
                    } else {
                        Log.e("PaymentViewModel", "Error fetching payment methods", t)
                    }
                }
            })
        }
    }

    /*
    fun updatePayment(payment: PaymentMethodDTO) {
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    paymentService.updatePaymentMethod(payment.id, payment).awaitResponse()
                }
                updated.value = response.isSuccessful
                //MainRouter.changePage(Navigation.PaymentMethodsPage)
            } catch (e: Exception) {
                updated.value = false
                e.printStackTrace()
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
                //MainRouter.changePage(Navigation.PaymentMethodsPage)
            } catch (e: Exception) {
                updated.value = false
                e.printStackTrace()
            }
            localUpdated.value = true
        }
    }
     */
}