package com.android.frontend.view_models

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.MainActivity
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.PaymentMethodCreateDTO
import com.android.frontend.controller.models.PaymentMethodDTO
import com.android.frontend.controller.infrastructure.TokenManager
import com.android.frontend.service.PaymentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.awaitResponse
import androidx.compose.foundation.pager.PagerState

class PaymentViewModel : ViewModel() {

    private val _paymentMethods = MutableLiveData<List<PaymentMethodDTO>>()
    val paymentMethodsLiveData: LiveData<List<PaymentMethodDTO>> get() = _paymentMethods
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError

    var paymentMethodId by mutableStateOf("")
    var cardNumber by mutableStateOf("")
    var owner by mutableStateOf("")
    var expireMonth by mutableStateOf("")
    var expireYear by mutableStateOf("")
    var isDefault by mutableStateOf(false)

    fun addPaymentCard(context: Context, cardNumber: String, expireMonth: String, expireYear: String, owner: String, isDefault: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG PaymentViewModel", "Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }
            val paymentMethod = PaymentMethodCreateDTO(cardNumber, expireMonth, expireYear, owner, isDefault)
            val paymentService: PaymentService = RetrofitInstance.getPaymentApi(context)
            val response = executeRequest(context) {
                paymentService.addPaymentMethod("Bearer $accessToken", paymentMethod)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { paymentMethod ->
                    Log.d("DEBUG PaymentViewModel", "Added payment method: $paymentMethod")
                }
            } else {
                Log.e("DEBUG PaymentViewModel", "Failed to add payment method: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun getAllPaymentMethods(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG PaymentViewModel", "Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }
            val paymentService: PaymentService = RetrofitInstance.getPaymentApi(context)
            val response = executeRequest(context) {
                paymentService.getAllPaymentMethods("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    _paymentMethods.value = it
                }
            } else {
                Log.e("DEBUG PaymentViewModel", "Failed to fetch payment methods: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun setDefaultPayment(context: Context, id: String, pagerState: PagerState) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG PaymentViewModel", "Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }
            val paymentService: PaymentService = RetrofitInstance.getPaymentApi(context)
            val response = executeRequest<Void>(context) {
                paymentService.setDefaultPaymentMethod("Bearer $accessToken", id)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG PaymentViewModel", "Set default payment method with id: $id")
                viewModelScope.launch {
                    pagerState.scrollToPage(0)
                }
                getAllPaymentMethods(context)
            } else {
                Log.e("DEBUG PaymentViewModel", "Failed to set default payment method: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun deletePayment(context: Context, id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG PaymentViewModel", "Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }
            val paymentService: PaymentService = RetrofitInstance.getPaymentApi(context)
            val response = executeRequest<Void>(context) {
                paymentService.deletePaymentMethod("Bearer $accessToken", id)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG PaymentViewModel", "Deleted payment method with id: $id")
                getAllPaymentMethods(context)
            } else {
                Log.e("DEBUG PaymentViewModel", "Failed to delete payment method: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    private suspend fun <T> executeRequest(context: Context, request: () -> Call<T>): Response<T>? {
        return try {
            val response = withContext(Dispatchers.IO) { request().awaitResponse() }
            when (response.code()) {
                401, 403 -> {
                    if (TokenManager.getInstance().tryRefreshToken(context)) {
                        withContext(Dispatchers.IO) { request().awaitResponse() } // Retry the request
                    } else {
                        handleLogout(context)
                        null
                    }
                }
                else -> response
            }
        } catch (e: Exception) {
            Log.e("DEBUG PaymentViewModel", "Request failed", e)
            _hasError.value = true
            null
        }
    }

    private fun handleLogout(context: Context) {
        TokenManager.getInstance().clearTokens(context)
        context.startActivity(Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}

