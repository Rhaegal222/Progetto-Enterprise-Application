package com.android.frontend.view_models.admin

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.ProductDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import androidx.lifecycle.MutableLiveData
import com.android.frontend.config.Request
import com.android.frontend.dto.BrandDTO
import com.android.frontend.dto.CategoryDTO
import com.android.frontend.dto.creation.ProductCreateDTO
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import java.math.BigDecimal

class AddProductViewModel : ViewModel() {

    val productId = MutableLiveData<Long?>()

    val name = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val ingredients = MutableLiveData<String>()
    val nutritionalValues = MutableLiveData<String>()
    val weight = MutableLiveData<String>()
    val quantity = MutableLiveData<Int>()
    val price = MutableLiveData<BigDecimal>()
    val shippingCost = MutableLiveData<BigDecimal>()
    val availability = MutableLiveData<ProductDTO.Availability>()
    val brand = MutableLiveData<BrandDTO>()
    val category = MutableLiveData<CategoryDTO>()
    val onSale = MutableLiveData<Boolean>()
    val salePrice = MutableLiveData<BigDecimal>()

    val allBrands = MutableLiveData<List<BrandDTO>>()
    val allCategories = MutableLiveData<List<CategoryDTO>>()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError


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
                Log.d("DEBUG", "${getCurrentStackTrace()} Fetched all categories")
                allCategories.postValue(response.body())
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching categories: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
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
                Log.d("DEBUG", "${getCurrentStackTrace()} Fetched all brands")
                allBrands.postValue(response.body())
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching brands: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }


    fun addProduct(context: Context) {
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

            val productCreateDTO = ProductCreateDTO(
                name = name.value ?: "",
                description = description.value?: "",
                ingredients = ingredients.value ?: "",
                nutritionalValues = nutritionalValues.value ?: "",
                weight = weight.value ?: "",
                quantity = quantity.value ?: 0,
                price = price.value ?: BigDecimal.ZERO,
                shippingCost = shippingCost.value ?: BigDecimal.ZERO,
                availability = availability.value ?: ProductDTO.Availability.IN_STOCK,
                brand = brand.value ?: BrandDTO(1,"",""),
                category = category.value ?: CategoryDTO(1,""),
                onSale = onSale.value ?: false,
                salePrice = salePrice.value
            )

            val productService = RetrofitInstance.getProductApi(context)
            val response = Request().executeRequest(context) {
                productService.addProduct("Bearer $accessToken", productCreateDTO)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Product added successfully")
                val tokenMap = response.body()
                if (tokenMap != null) {
                    val message = tokenMap["message"]
                    val prodId = tokenMap["productId"]?.toLong()
                    productId.postValue(prodId)
                    Log.d("DEBUG", "${getCurrentStackTrace()} ${message}: ${productId.value}")
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error adding product: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }


    fun uploadProductImage(context: Context, productId: Long, imageUri: Uri) {
        viewModelScope.launch {
            //stampa nel log productId e imageUri
            Log.d("DEBUG", "${getCurrentStackTrace()} productId: $productId, imageUri: $imageUri")
            uploadImage(context, productId, imageUri)

        }
    }

    private suspend fun uploadImage(context: Context, productId: Long, imageUri: Uri) {
        withContext(Dispatchers.IO) {
            val file = getFileFromUri(context, imageUri) ?: return@withContext

            // Prepare the file part
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            // Prepare other parts
            val descriptionPart = "Product Image".toRequestBody("text/plain".toMediaTypeOrNull())

            _isLoading.value = true
            _hasError.value = false

            try {
                val accessToken = TokenManager.getInstance().getAccessToken(context)
                if (accessToken == null) {
                    Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                    _isLoading.value = false
                    _hasError.value = true
                    return@withContext
                }

                val productImageService = RetrofitInstance.getProductImageApi(context)
                val response = Request().executeRequest(context) {
                    productImageService.savePhotoProduct("Bearer $accessToken", body, productId, descriptionPart)
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
            e.printStackTrace()
            return null
        }
        return tempFile
    }

}