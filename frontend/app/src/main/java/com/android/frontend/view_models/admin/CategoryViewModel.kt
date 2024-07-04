package com.android.frontend.view_models.admin

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.MainActivity
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.Request
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.CategoryDTO
import com.android.frontend.dto.creation.CategoryCreateDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import retrofit2.awaitResponse

class CategoryViewModel : ViewModel() {

    val allCategories = MutableLiveData<List<CategoryDTO>>()
    val name = MutableLiveData<String>()


    fun fetchAllCategories(context: Context) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                return@launch
            }
            val productCategoryService = RetrofitInstance.getProductCategoryApi(context)
            val response = executeRequest(context) {
                productCategoryService.getAllCategories("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                allCategories.postValue(response.body())
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching categories: ${response?.errorBody()?.string()}")
            }
        }
    }

    fun deleteCategory(id: Long, context: Context) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                return@launch
            }
            val categoryService = RetrofitInstance.getProductCategoryApi(context)
            val response = executeRequest(context) {
                categoryService.deleteCategory("Bearer $accessToken", id)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Category deleted successfully with id: $id")
                fetchAllCategories(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error deleting category: ${response?.errorBody()?.string()}")
            }
        }
    }

    fun addCategory(categoryCreateDTO: CategoryCreateDTO, context: Context) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                return@launch
            }
            val productCategoryService = RetrofitInstance.getProductCategoryApi(context)
            val response = executeRequest(context) {
                productCategoryService.addCategory("Bearer $accessToken", categoryCreateDTO)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { categories ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Category added successfully: $categories")
                    fetchAllCategories(context)
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error adding category: ${response?.errorBody()?.string()}")
            }
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