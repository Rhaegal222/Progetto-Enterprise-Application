package com.android.frontend.view_models.admin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.MainActivity
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.math.BigDecimal
import java.net.SocketTimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EditProductViewModel : ViewModel() {

    val productDetails = MutableLiveData<ProductDTO>()
    val allBrands = MutableLiveData<List<BrandDTO>>()
    val allCategories = MutableLiveData<List<CategoryDTO>>()

    val isLoading = MutableLiveData<Boolean>()

    val brand = MutableLiveData<BrandDTO?>()
    val category = MutableLiveData<CategoryDTO?>()
    val availability = MutableLiveData<ProductDTO.Availability?>()


    private val prodImag = MutableLiveData<Uri?>(null)
    val prodImagLiveData : LiveData<Uri?> get() = prodImag

    fun getProductDetails(context: Context, id: Long) {
        viewModelScope.launch {
            val productService = RetrofitInstance.getProductApi(context)
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val call = productService.getProductById("Bearer $accessToken", id)
            call.enqueue(object : retrofit2.Callback<ProductDTO> {
                override fun onResponse(
                    call: retrofit2.Call<ProductDTO>,
                    response: retrofit2.Response<ProductDTO>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { product ->
                            Log.d("DEBUG", "Product details: $product")
                            productDetails.value = product
                        }
                    } else {
                        Log.e("DEBUG", "Failed to fetch product: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<ProductDTO>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("DEBUG", "Timeout error fetching product details", t)
                    } else {
                        Log.e("DEBUG", "Error fetching product details", t)
                    }
                }
            })
        }
    }


    fun uploadProductImage(context: Context, productId: Long, imageUri: Uri) {
        viewModelScope.launch {
            deletePhotoProduct(context, productId)
            uploadImage(context, productId, imageUri)

        }
    }

    fun updateProduct(context: Context, productId: Long, name: String, description: String, ingredients: String, nutritionalValues: String, weight: String, quantity: Int, price: BigDecimal, shippingCost: BigDecimal, onSale: Boolean, discountedPrice: BigDecimal) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)

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
            val call = productService.updateProduct("Bearer $accessToken", productId, updateRequest)
            call.enqueue(object : Callback<ProductDTO> {
                override fun onResponse(call: Call<ProductDTO>, response: Response<ProductDTO>) {
                    if (response.isSuccessful) {
                        Log.e("DEBUG", "${getCurrentStackTrace()}, Product updated successfully: ${response.body()}")
                    } else {
                        Log.e("DEBUG", "${getCurrentStackTrace()}, Failed to update product: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ProductDTO>, t: Throwable) {
                    Log.e("DEBUG", "${getCurrentStackTrace()}, Error updating product", t)
                }
            })
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

    fun getProductImage(context: Context, productId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val type = "product_photos"
                    val folderName = productId.toString()
                    val fileName = "photoProduct.png"
                    val responseBody = fetchImage(context, type, folderName, fileName)
                    responseBody?.let {
                        val tempFile = saveImageToFile(context, responseBody)
                        prodImag.postValue(Uri.fromFile(tempFile))
                    } ?: run {
                        Log.e("DEBUG", "${getCurrentStackTrace()},Image retrieval failed")
                    }
                } catch (e: Exception) {
                    Log.e("DEBUG", "${getCurrentStackTrace()},Image retrieval error: ${e.message}")
                }
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

    suspend fun uploadImage(context: Context, productId: Long, imageUri: Uri) {
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

    suspend fun deletePhotoProduct(context: Context, productId: Long) {
        val accessToken = TokenManager.getInstance().getAccessToken(context)
        val productImageService = RetrofitInstance.getProductImageApi(context)
        val call = productImageService.deletePhotoProduct("Bearer $accessToken", productId)
        try {
            val response = withContext(Dispatchers.IO) { call.awaitResponse() }
            if (!response.isSuccessful) {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Image delete failed: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("DEBUG", "${getCurrentStackTrace()}, Image delete error: ${e.message}")
        }
    }

    private suspend fun saveImageToFile(context: Context, responseBody: ResponseBody): File {
        return withContext(Dispatchers.IO) {
            val tempFile = File.createTempFile("product", "jpg", context.cacheDir)
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