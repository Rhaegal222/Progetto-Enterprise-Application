package com.android.frontend.view_models.user

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
import com.android.frontend.config.TokenManager
import com.android.frontend.dto.creation.AddressCreateDTO
import com.android.frontend.dto.AddressDTO
import androidx.compose.foundation.pager.PagerState
import com.android.frontend.config.Request
import com.android.frontend.config.getCurrentStackTrace
import kotlinx.coroutines.launch

class AddressViewModel : ViewModel() {

    private val _shippingAddresses = MutableLiveData<List<AddressDTO>>()
    val shippingAddressesLiveData: LiveData<List<AddressDTO>> get() = _shippingAddresses
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError

    // var addressId by mutableStateOf("")
    var fullName by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var street by mutableStateOf("")
    var additionalInfo by mutableStateOf("")
    var postalCode by mutableStateOf("")
    var city by mutableStateOf("")
    var province by mutableStateOf("")
    var country by mutableStateOf("")
    var isDefault by mutableStateOf(false)

    fun addShippingAddress(
        context: Context,
        fullName: String,
        phoneNumber: String,
        street: String,
        additionalInfo: String,
        postalCode: String, city:
        String, province: String,
        country: String,
        isDefault: Boolean)
    {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }
            val shippingAddress = AddressCreateDTO(fullName, phoneNumber, street, additionalInfo, postalCode, city, province, country, isDefault)
            val addressService = RetrofitInstance.getAddressApi(context)
            val response = Request().executeRequest(context) {
                addressService.addAddress("Bearer $accessToken", shippingAddress)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { shippingAddresses ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Added shipping address: $shippingAddresses")
                    getAllLoggedUserShippingAddresses(context)
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to add shipping address: ${response?.errorBody()?.string() ?: "Empty response"}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun getAllLoggedUserShippingAddresses(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }
            val addressService = RetrofitInstance.getAddressApi(context)
            val response = Request().executeRequest(context) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Getting shipping addresses for logged user")
                addressService.getAllLoggedUserAddresses("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Got shipping addresses")
                response.body()?.let {
                    _shippingAddresses.value = it
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to get shipping addresses: ${response?.errorBody()?.string()}")
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
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }
            val addressService = RetrofitInstance.getAddressApi(context)
            val response = Request().executeRequest(context) {
                addressService.setAddress("Bearer $accessToken", id)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Set default shipping address with id: $id")
                viewModelScope.launch {
                    pagerState.scrollToPage(0)
                }
                getAllLoggedUserShippingAddresses(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to set default shipping address: ${response?.errorBody()?.string()}")
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
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }
            val addressService = RetrofitInstance.getAddressApi(context)
            val response = Request().executeRequest(context) {
                addressService.deleteAddress("Bearer $accessToken", id)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Deleted shipping address with id: $id")
                getAllLoggedUserShippingAddresses(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to delete shipping address: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }
}
