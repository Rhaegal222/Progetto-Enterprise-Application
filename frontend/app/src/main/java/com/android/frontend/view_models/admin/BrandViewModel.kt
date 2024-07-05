package com.android.frontend.view_models.admin

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.Request
import com.android.frontend.config.TokenManager
import com.android.frontend.dto.BrandDTO
import com.android.frontend.dto.creation.BrandCreateDTO
import com.android.frontend.config.getCurrentStackTrace
import kotlinx.coroutines.launch


class BrandViewModel : ViewModel() {

    val allBrands = MutableLiveData<List<BrandDTO>>()
    val name = MutableLiveData<String>()
    val description = MutableLiveData<String>()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError

    fun fetchAllBrands(context: Context) {
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
                allBrands.postValue(response.body())
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching brands: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun deleteBrand(id: Long, context: Context) {
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
                Log.d("DEBUG", "${getCurrentStackTrace()} Brand deleted successfully with id: $id")
                fetchAllBrands(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error deleting brand: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun addBrand(brandCreateDTO: BrandCreateDTO, context: Context) {
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
                brandService.addBrand("Bearer $accessToken", brandCreateDTO)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { brands ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Brand added successfully: $brands")
                    fetchAllBrands(context)
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error adding brand: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }
}
