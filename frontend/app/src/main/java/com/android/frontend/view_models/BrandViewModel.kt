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
import com.android.frontend.config.Request
import com.android.frontend.config.TokenManager
import com.android.frontend.dto.BrandDTO
import com.android.frontend.dto.creation.BrandCreateDTO
import com.android.frontend.config.getCurrentStackTrace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.awaitResponse

class BrandViewModel : ViewModel() {

    private val _brands = MutableLiveData<List<BrandDTO>>()
    val brandsLiveData: LiveData<List<BrandDTO>> get() = _brands
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError

    var name by mutableStateOf("")
    var description by mutableStateOf("")

    fun addBrand(context: Context, name: String, description: String) {
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
            val brand = BrandCreateDTO(name, description)
            val brandService = RetrofitInstance.getBrandApi(context)
            val response = Request().executeRequest(context) {
                brandService.addBrand("Bearer $accessToken", brand)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { brands ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Added brand: $brands")
                    getAllBrands(context)
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to add brand: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun getAllBrands(context: Context) {
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
            val brandService = RetrofitInstance.getBrandApi(context)
            val response = Request().executeRequest(context) {
                brandService.getAllBrands("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    _brands.value = it
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to get brands: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun deleteBrand(context: Context, id: Int) {
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
            val brandService = RetrofitInstance.getBrandApi(context)
            val response = Request().executeRequest(context) {
                brandService.deleteBrand("Bearer $accessToken", id)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Deleted brand with id: $id")
                getAllBrands(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to delete brand: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }
}
