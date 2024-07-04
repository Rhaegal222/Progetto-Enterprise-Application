package com.android.frontend.view_models.admin

import android.content.Context
import android.content.Intent
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
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.lifecycle.MutableLiveData
import com.android.frontend.MainActivity
import com.android.frontend.dto.BrandDTO
import com.android.frontend.dto.CategoryDTO
import com.android.frontend.dto.creation.ProductCreateDTO
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.awaitResponse
import java.io.InputStream
import java.math.BigDecimal

class ProductViewModel : ViewModel() {

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

    private val _productsLiveData = MutableLiveData<List<ProductDTO>>()
    val productsLiveData: LiveData<List<ProductDTO>> = _productsLiveData

    private val _productImagesLiveData = MutableLiveData<Map<Long, Uri>>()
    val productImagesLiveData: LiveData<Map<Long, Uri>> = _productImagesLiveData

    val allBrands = MutableLiveData<List<BrandDTO>>()
    val allCategories = MutableLiveData<List<CategoryDTO>>()

    val isLoading = MutableLiveData<Boolean>()


    fun fetchAllData(context: Context) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                fetchAllBrands(context)
                fetchAllCategories(context)
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching all data", e)
            } finally {
                isLoading.value = false
            }
        }
    }

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

    fun fetchAllBrands(context: Context) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                return@launch
            }
            val brandService = RetrofitInstance.getBrandApi(context)
            val response = executeRequest(context) {
                brandService.getAllBrands("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                allBrands.postValue(response.body())
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching brands: ${response?.errorBody()?.string()}")
            }
        }
    }


    fun fetchAllProducts(context: Context) {
        viewModelScope.launch {
            try {
                val accessToken = TokenManager.getInstance().getAccessToken(context)
                val productService = RetrofitInstance.getProductApi(context)
                val response = productService.getAllProducts("Bearer $accessToken")
                if (response.isSuccessful) {
                    val products = response.body() ?: emptyList()
                    _productsLiveData.postValue(products)
                    products.forEach { product ->
                        fetchProductImage(context, product.id)
                    }
                } else {
                    Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch all products: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching all products", e)
            }
        }
    }

    fun addProduct(context: Context) {
        viewModelScope.launch {
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
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                return@launch
            }
            val productService = RetrofitInstance.getProductApi(context)
            val response = executeRequest(context) {
                productService.addProduct("Bearer $accessToken", productCreateDTO)
            }
            if (response?.isSuccessful == true) {
                val tokenMap = response.body()
                if (tokenMap != null) {
                    val message = tokenMap["message"]
                    val prodId = tokenMap["productId"]?.toLong()
                    productId.postValue(prodId)
                    Log.d("DEBUG", "${getCurrentStackTrace()} ${message}: ${productId.value}")
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error adding product: ${response?.errorBody()?.string()}")
            }
        }
    }

    fun deleteProduct(productId: Long, context: Context) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                return@launch
            }
            val productService = RetrofitInstance.getProductApi(context)
            val response = executeRequest(context) {
                productService.deleteProduct("Bearer $accessToken", productId)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Product deleted successfully with id: $productId")
                fetchAllProducts(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error deleting product: ${response?.errorBody()?.string()}")
            }
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

            try {
                val accessToken = TokenManager.getInstance().getAccessToken(context)
                val productImageService = RetrofitInstance.getProductImageApi(context)
                val call = productImageService.savePhotoProduct("Bearer $accessToken", body, productId, descriptionPart)
                val response = call.execute()

                if (response.isSuccessful) {
                    response.body()?.let { userImageDTO ->
                        Log.e("DEBUG", "${getCurrentStackTrace()}, Image upload success: ${userImageDTO}")
                    }
                } else {
                    Log.e("DEBUG", "${getCurrentStackTrace()}, Image upload failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Image upload error", e)
            }
        }
    }

    private fun fetchProductImage(context: Context, productId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val type = "product_photos"
                    val folderName = productId.toString()
                    val fileName = "photoProduct.png"
                    val responseBody = fetchImage(context, type, folderName, fileName)
                    responseBody?.let {
                        val tempFile = saveImageToFile(context, responseBody)
                        val imageUri = Uri.fromFile(tempFile)
                        val currentImages = _productImagesLiveData.value?.toMutableMap() ?: mutableMapOf()
                        currentImages[productId] = imageUri
                        _productImagesLiveData.postValue(currentImages)
                    } ?: run {
                        Log.e("DEBUG", "${getCurrentStackTrace()},Image retrieval failed")
                    }
                } catch (e: Exception) {
                    Log.e("DEBUG", "${getCurrentStackTrace()},Image retrieval error: ${e.message}")
                }
            }
        }
    }

    private suspend fun fetchImage(context: Context, type: String, folderName: String, fileName: String): ResponseBody? {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val productImageService = RetrofitInstance.getProductImageApi(context)
                val call = productImageService.getImage(type, folderName, fileName)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            continuation.resume(response.body())
                        } else {
                            continuation.resume(null)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        continuation.resume(null)
                    }
                })
            }
        }
    }

    private suspend fun saveImageToFile(context: Context, responseBody: ResponseBody): File {
        return withContext(Dispatchers.IO) {
            val tempFile = File.createTempFile("profile", "jpg", context.cacheDir)
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
            e.printStackTrace()
            return null
        }
        return tempFile
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