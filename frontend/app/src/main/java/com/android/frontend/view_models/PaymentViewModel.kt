package com.android.frontend.view_models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.PaymentMethodCreateDTO
import com.android.frontend.model.SecurePreferences
import com.android.frontend.controller.models.PaymentMethodDTO
import com.android.frontend.service.PaymentService
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class PaymentViewModel : ViewModel() {

    private val paymentMethods = MutableLiveData<List<PaymentMethodDTO>>()
    val paymentMethodsLiveData: LiveData<List<PaymentMethodDTO>> get() = paymentMethods

    var paymentMethodId by mutableStateOf("")
    var cardNumber by mutableStateOf("")
    var owner by mutableStateOf("")
    var expireMonth by mutableStateOf("")
    var expireYear by mutableStateOf("")
    var isDefault by mutableStateOf(false)

    fun addPaymentCard(context: Context, cardNumber: String, expireMonth: String, expireYear: String, owner: String, isDefault: Boolean) {
        viewModelScope.launch {
            val accessToken = SecurePreferences.getAccessToken(context)
            val paymentMethod = PaymentMethodCreateDTO(cardNumber, expireMonth, expireYear, owner, isDefault)
            val paymentService: PaymentService = RetrofitInstance.getPaymentApi(context)
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
                        Log.e("PaymentViewModel",
                            "Failed to add payment method: ${response.errorBody()?.string()}")
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
            val paymentService: PaymentService = RetrofitInstance.getPaymentApi(context)
            val call = paymentService.getAllPaymentMethods("Bearer $accessToken")
            call.enqueue(object : Callback<List<PaymentMethodDTO>> {
                override fun onResponse(
                    call: Call<List<PaymentMethodDTO>>,
                    response: Response<List<PaymentMethodDTO>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { it ->

                            if (it.isNotEmpty() && !it[0].isDefault) {
                                val defaultPaymentMethod = it.find { it.isDefault }
                                if (defaultPaymentMethod != null) {
                                    val defaultIndex =
                                        it.indexOf(defaultPaymentMethod)
                                    val firstPaymentMethod = it[0]

                                    val modifiedPaymentMethodsList =
                                        it.toMutableList()

                                    modifiedPaymentMethodsList[0] = defaultPaymentMethod
                                    modifiedPaymentMethodsList[defaultIndex] = firstPaymentMethod

                                    paymentMethods.value = modifiedPaymentMethodsList
                                } else {
                                    paymentMethods.value = it
                                }
                            } else {
                                paymentMethods.value = it
                            }
                        }
                    } else {
                        Log.e("PaymentViewModel",
                            "Failed to fetch payment methods: ${response.errorBody()?.string()}")
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

    @OptIn(ExperimentalPagerApi::class)
    fun setDefaultPayment(context: Context, id: String, pagerState: PagerState){
        viewModelScope.launch {
            val accessToken = SecurePreferences.getAccessToken(context)
            val paymentService: PaymentService = RetrofitInstance.getPaymentApi(context)
            val call = paymentService.setDefaultPaymentMethod("Bearer $accessToken", id)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("PaymentViewModel", "Set default payment method with id: $id")
                        viewModelScope.launch {
                            pagerState.scrollToPage(0)
                        }
                        getAllPaymentMethods(context)
                    } else {
                        Log.e("PaymentViewModel",
                            "Failed to set default payment method: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("PaymentViewModel", "Timeout error setting default payment method", t)
                    } else {
                        Log.e("PaymentViewModel", "Error setting default payment method", t)
                    }
                }
            })
        }
    }

    fun deletePayment(context: Context, id: String) {
        viewModelScope.launch {
            val accessToken = SecurePreferences.getAccessToken(context)
            val paymentService: PaymentService = RetrofitInstance.getPaymentApi(context)
            val call = paymentService.deletePaymentMethod("Bearer $accessToken", id)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("PaymentViewModel", "Deleted payment method with id: $id")
                        getAllPaymentMethods(context)
                    } else {
                        Log.e("PaymentViewModel",
                            "Failed to delete payment method: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("PaymentViewModel", "Timeout error deleting payment method", t)
                    } else {
                        Log.e("PaymentViewModel", "Error deleting payment method", t)
                    }
                }
            })
        }
    }

}