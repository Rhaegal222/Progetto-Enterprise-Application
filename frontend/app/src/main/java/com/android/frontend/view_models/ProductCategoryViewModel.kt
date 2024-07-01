package com.android.frontend.view_models

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.MainActivity
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.ProductCategoryDTO
import com.android.frontend.dto.creation.ProductCategoryCreateDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.awaitResponse

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
            val response = executeRequest(context) {
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
            val response = executeRequest(context) {
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
            Log.e("DEBUG", "${getCurrentStackTrace()} Request failed", e)
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