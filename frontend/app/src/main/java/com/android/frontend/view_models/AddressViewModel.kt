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
import com.android.frontend.controller.infrastructure.TokenManager
import com.android.frontend.controller.models.AddressCreateDTO
import com.android.frontend.controller.models.AddressDTO
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class AddressViewModel : ViewModel() {

    private val shippingAddresses = MutableLiveData<List<AddressDTO>>()
    val shippingAddressesLiveData: LiveData<List<AddressDTO>> get() = shippingAddresses

    var header by mutableStateOf("")
    var country by mutableStateOf("")
    var city by mutableStateOf("")
    var street by mutableStateOf("")
    var zipCode by mutableStateOf("")
    var isDefault by mutableStateOf(false)

    fun addShippingAddress(context: Context, header: String, country: String, city: String, street: String, zipCode: String, isDefault: Boolean) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val shippingAddress = AddressCreateDTO(header, country, city, street, zipCode, isDefault)
            val addressService = RetrofitInstance.getAddressApi(context)
            val call = addressService.addShippingAddress("Bearer $accessToken", shippingAddress)
            call.enqueue(object : Callback<AddressDTO> {
                override fun onResponse(
                    call: Call<AddressDTO>,
                    response: Response<AddressDTO>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { shippingAddress ->
                            Log.d("DEBUG AddressViewModel", "Added shipping address: $shippingAddress")
                        }
                    } else {
                        Log.e("DEBUG AddressViewModel",
                            "Failed to add shipping address: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<AddressDTO>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("DEBUG AddressViewModel", "Timeout error adding shipping address", t)
                    } else {
                        Log.e("DEBUG AddressViewModel", "Error adding shipping address", t)
                    }
                }
            })
        }
    }

    fun getAllShippingAddresses(context: Context) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val addressService = RetrofitInstance.getAddressApi(context)
            val call = addressService.getAllShippingAddresses("Bearer $accessToken")
            call.enqueue(object : Callback<List<AddressDTO>> {
                override fun onResponse(
                    call: Call<List<AddressDTO>>,
                    response: Response<List<AddressDTO>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { it ->
                            shippingAddresses.value = it
                        }
                    } else {
                        Log.e("DEBUG AddressViewModel",
                            "Failed to fetch shipping addresses: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<AddressDTO>>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("DEBUG AddressViewModel", "Timeout error fetching shipping addresses", t)
                    } else {
                        Log.e("DEBUG AddressViewModel", "Error fetching shipping addresses", t)
                    }
                }
            })
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    fun setDefaultShippingAddress(context: Context, id: String, pagerState: PagerState){
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val addressService = RetrofitInstance.getAddressApi(context)
            val call = addressService.setDefaultShippingAddress("Bearer $accessToken", id)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("DEBUG AddressViewModel", "Set default shipping address with id: $id")
                        viewModelScope.launch {
                            pagerState.scrollToPage(0)
                        }
                        getAllShippingAddresses(context)
                    } else {
                        Log.e("DEBUG AddressViewModel",
                            "Failed to set default shipping address: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("DEBUG AddressViewModel", "Timeout error setting default shipping address", t)
                    } else {
                        Log.e("DEBUG AddressViewModel", "Error setting default shipping address", t)
                    }
                }
            })
        }
    }

    fun deleteShippingAddress(context: Context, id: String) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val addressService = RetrofitInstance.getAddressApi(context)
            val call = addressService.deleteShippingAddress("Bearer $accessToken", id)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("DEBUG AddressViewModel", "Deleted shipping address with id: $id")
                        getAllShippingAddresses(context)
                    } else {
                        Log.e("DEBUG AddressViewModel",
                            "Failed to delete shipping address: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("DEBUG AddressViewModel", "Timeout error deleting shipping address", t)
                    } else {
                        Log.e("DEBUG AddressViewModel", "Error deleting shipping address", t)
                    }
                }
            })
        }
    }

}
