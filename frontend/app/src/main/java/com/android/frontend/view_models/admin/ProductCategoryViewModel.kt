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
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.creation.ProductCategoryCreateDTO
import kotlinx.coroutines.launch

class ProductCategoryViewModel : ViewModel() {

    private val _categories = MutableLiveData<List<ProductCategoryDTO>>()
    val categories: LiveData<List<ProductCategoryDTO>> get() = _categories

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _hasError = MutableLiveData(false)

    fun addProductCategory(context: Context, categoryName: String) {
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
            val productCategory = ProductCategoryCreateDTO(categoryName)
            val productCategoryService = RetrofitInstance.getProductCategoryApi(context)
            val response = Request().executeRequest(context) {
                productCategoryService.addCategory("Bearer $accessToken", productCategory)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { categories ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Added product category: $categories")
                    getAllProductCategories(context)
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to add product category: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun getAllProductCategories(context: Context) {
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
            val productCategoryService = RetrofitInstance.getProductCategoryApi(context)
            val response = Request().executeRequest(context) {
                productCategoryService.getAllCategories("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    _categories.value = it
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to get product categories: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }
}