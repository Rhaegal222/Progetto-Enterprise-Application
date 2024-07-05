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
import com.android.frontend.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.math.BigDecimal

class EditProductViewModel : ViewModel() {

    val allBrands = MutableLiveData<List<BrandDTO>>()
    val allCategories = MutableLiveData<List<CategoryDTO>>()

    val brand = MutableLiveData<BrandDTO?>()
    val category = MutableLiveData<CategoryDTO?>()
    val availability = MutableLiveData<ProductDTO.Availability?>()

    val productDetails = MutableLiveData<ProductDTO>()

    private val prodImage = MutableLiveData<Uri?>(null)
    val prodImageLiveData : LiveData<Uri?> get() = prodImage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError

    fun getProductDetails(context: Context, id: Long) {
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
            val response = Request().executeRequest(context){
                productService.getProductById("Bearer $accessToken", id)
            }
            if (response?.isSuccessful == true){
                response.body()?.let { product ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Product details: $product")
                    productDetails.postValue(product)
                    fetchProductImage(context, id)
                }
            }
            else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch products: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }


    fun replacePhotoProduct(context: Context, productId: Long, imageUri: Uri) {
        viewModelScope.launch {
            uploadImage(context, productId, imageUri)
        }
    }

    fun updateProduct(context: Context, productId: Long, name: String, description: String, ingredients: String, nutritionalValues: String, weight: String, quantity: Int, price: BigDecimal, shippingCost: BigDecimal, onSale: Boolean, discountedPrice: BigDecimal) {
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

            val updateRequest = ProductUpdateRequest(
                name = name,
                description = description,
                ingredients = ingredients,
                nutritionalValues = nutritionalValues,
                weight = weight,
                quantity = quantity,
                price = price,
                shippingCost = shippingCost,
                availability = availability.value!!,
                brand = brand.value!!,
                category = category.value!!,
                onSale = onSale,
                discountedPrice = discountedPrice
            )

            val productService = RetrofitInstance.getProductApi(context)
            val response = Request().executeRequest(context) {
                productService.updateProduct("Bearer $accessToken", productId, updateRequest)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Product updated successfully: ${response.body()}")
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to update product: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
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

    private fun fetchProductImage(context: Context, productId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            try {
                val responseBody = getPhotoProductById(context, productId)
                responseBody?.let {
                    val tempFile = saveImageToFile(context, responseBody)
                    val imageUri = Uri.fromFile(tempFile)
                    prodImage.postValue(imageUri)
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

    fun fetchAllCategories(context: Context) {
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
                allCategories.postValue(response.body())
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching categories: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }


    private suspend fun uploadImage(context: Context, productId: Long, imageUri: Uri) {
        withContext(Dispatchers.IO) {
            val file = getFileFromUri(context, imageUri) ?: return@withContext

            // Prepare the file part
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            _isLoading.postValue(true)
            _hasError.postValue(false)

            try {
                val accessToken = TokenManager.getInstance().getAccessToken(context)
                if (accessToken == null) {
                    Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                    _isLoading.postValue(false)
                    _hasError.postValue(true)
                    return@withContext
                }

                val productImageService = RetrofitInstance.getProductImageApi(context)
                val response = Request().executeRequest(context) {
                    productImageService.replacePhotoProductById("Bearer $accessToken", productId, body)
                }

                if (response?.isSuccessful == true) {
                    response.body()?.let { userImageDTO ->
                        Log.d("DEBUG", "${getCurrentStackTrace()}, Image upload success: $userImageDTO")
                    }
                } else {
                    Log.e("DEBUG", "${getCurrentStackTrace()}, Image upload failed: ${response?.errorBody()?.string()}")
                    _hasError.postValue(true)
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Image upload error: ${e.message}", e)
                _hasError.postValue(true)
            } finally {
                _isLoading.postValue(false)
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

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val fileName = "photoProduct.png"
        val tempFile = File(context.cacheDir, fileName)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
        } catch (e: Exception) {
            Log.e("DEBUG", "${getCurrentStackTrace()}, File from URI error", e)
            return null
        }
        return tempFile
    }

}
