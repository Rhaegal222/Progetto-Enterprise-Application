package com.android.frontend.view_models.admin

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.Request
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.ProductDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream

class ProductViewModel : ViewModel() {

    private val _productsLiveData = MutableLiveData<List<ProductDTO>>()
    val productsLiveData: LiveData<List<ProductDTO>> = _productsLiveData

    private val _productImagesLiveData = MutableLiveData<Map<Long, Uri>>()
    val productImagesLiveData: LiveData<Map<Long, Uri>> = _productImagesLiveData

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError

    fun fetchAllProducts(context: Context) {
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
            val productService = RetrofitInstance.getProductApi(context)
            val response = Request().executeRequest(context) {
                productService.getAllProducts("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Fetched all products")
                val products = response.body() ?: emptyList()
                _productsLiveData.postValue(products)
                products.forEach { product ->
                    fetchProductImage(context, product.id)
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch all products: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun deleteProduct(productId: Long, context: Context) {
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
            val productService = RetrofitInstance.getProductApi(context)
            val response = Request().executeRequest(context) {
                productService.deleteProduct("Bearer $accessToken", productId)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Product deleted successfully with id: $productId")
                fetchAllProducts(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error deleting product: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    private fun fetchProductImage(context: Context, productId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            try {
                val responseBody = getPhotoProductById(context, productId)
                responseBody?.let {
                    val tempFile = saveImageToFile(context, responseBody)
                    val imageUri = Uri.fromFile(tempFile)
                    val currentImages = _productImagesLiveData.value?.toMutableMap() ?: mutableMapOf()
                    currentImages[productId] = imageUri
                    _productImagesLiveData.postValue(currentImages)
                } ?: run {
                    Log.e("DEBUG", "${getCurrentStackTrace()}, Image retrieval failed")
                    _hasError.value = true
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Image retrieval error: ${e.message}")
                _hasError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getPhotoProductById(context: Context, productId: Long): ResponseBody? {
        return withContext(Dispatchers.IO) {
            _isLoading.postValue(true)
            _hasError.postValue(false)
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                _isLoading.postValue(false)
                _hasError.postValue(true)
                return@withContext null
            }
            val productImageService = RetrofitInstance.getProductImageApi(context)
            val response = Request().executeRequest(context) {
                productImageService.getPhotoProductById("Bearer $accessToken", productId)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Fetched image")
                response.body()
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching image: ${response?.errorBody()?.string()}")
                null
            }
        }
    }

    private suspend fun saveImageToFile(context: Context, responseBody: ResponseBody): File {
        return withContext(Dispatchers.IO) {
            val tempFile = File.createTempFile("product", "png", context.cacheDir)
            val inputStream = responseBody.byteStream()
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            tempFile
        }
    }
}
