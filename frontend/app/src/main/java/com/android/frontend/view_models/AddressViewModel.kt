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
import com.android.frontend.config.TokenManager
import com.android.frontend.dto.AddressCreateDTO
import com.android.frontend.dto.AddressDTO
import androidx.compose.foundation.pager.PagerState
import com.android.frontend.config.getCurrentStackTrace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.awaitResponse

class AddressViewModel : ViewModel() {

    private val _shippingAddresses = MutableLiveData<List<AddressDTO>>()
    val shippingAddressesLiveData: LiveData<List<AddressDTO>> get() = _shippingAddresses
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError

    var firstname by mutableStateOf("")
    var lastname by mutableStateOf("")
    var country by mutableStateOf("")
    var city by mutableStateOf("")
    var street by mutableStateOf("")
    var zipCode by mutableStateOf("")
    var isDefault by mutableStateOf(false)

    fun addShippingAddress(context: Context, firstname: String, lastname: String, country: String, city: String, street: String, zipCode: String, isDefault: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }
            val shippingAddress = AddressCreateDTO(firstname, lastname, country, city, street, zipCode, isDefault)
            val addressService = RetrofitInstance.getAddressApi(context)
            val response = executeRequest(context) {
                addressService.addShippingAddress("Bearer $accessToken", shippingAddress)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { shippingAddresses ->
                    Log.d("DEBUG", "${getCurrentStackTrace()}, Added shipping address: $shippingAddresses")
                    getAllShippingAddresses(context)
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Failed to add shipping address: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun getAllShippingAddresses(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }
            val addressService = RetrofitInstance.getAddressApi(context)
            val response = executeRequest(context) {
                addressService.getAllShippingAddresses("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    _shippingAddresses.value = it
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Failed to get shipping addresses: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun setDefaultShippingAddress(context: Context, id: String, pagerState: PagerState){
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }
            val addressService = RetrofitInstance.getAddressApi(context)
            val response = executeRequest(context) {
                addressService.setDefaultShippingAddress("Bearer $accessToken", id)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()}, Set default shipping address with id: $id")
                viewModelScope.launch {
                    pagerState.scrollToPage(0)
                }
                getAllShippingAddresses(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Failed to set default shipping address: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun deleteShippingAddress(context: Context, id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }
            val addressService = RetrofitInstance.getAddressApi(context)
            val response = executeRequest(context) {
                addressService.deleteShippingAddress("Bearer $accessToken", id)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()}, Deleted shipping address with id: $id")
                getAllShippingAddresses(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Failed to delete shipping address: ${response?.errorBody()?.string()}")
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
            Log.e("DEBUG", "${getCurrentStackTrace()}, Request failed", e)
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
