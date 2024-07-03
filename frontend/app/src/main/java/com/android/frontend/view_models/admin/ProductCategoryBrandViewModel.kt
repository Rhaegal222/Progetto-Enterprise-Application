package com.android.frontend.view_models.admin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.MainActivity
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.BrandDTO
import com.android.frontend.dto.ProductCategoryDTO
import com.android.frontend.dto.ProductDTO
import com.android.frontend.dto.creation.BrandCreateDTO
import com.android.frontend.dto.creation.ProductCategoryCreateDTO
import com.android.frontend.dto.creation.ProductCreateDTO
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
import androidx.lifecycle.MutableLiveData
import com.android.frontend.dto.UserDTO
import com.android.frontend.persistence.SecurePreferences

class ProductCategoryBrandViewModel() : ViewModel() {

    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val ingredients = MutableLiveData<String>()
    val nutritionalValues = MutableLiveData<String>()
    val productPrice = MutableLiveData<BigDecimal>()
    val deliveryPrice = MutableLiveData<BigDecimal>()
    val productWeight = MutableLiveData<String>()
    val availability = MutableLiveData<ProductDTO.Availability>()
    val quantity = MutableLiveData<Int>()
    val brand = MutableLiveData<BrandDTO>()
    val productCategory = MutableLiveData<ProductCategoryDTO>()
    val onSale = MutableLiveData<Boolean>()
    val discountedPrice = MutableLiveData<BigDecimal>()

    val allBrands = MutableLiveData<List<BrandDTO>>()
    val allCategories = MutableLiveData<List<ProductCategoryDTO>>()

    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val successMessage = MutableLiveData<String>()

    private val _productsLiveData = MutableLiveData<List<ProductDTO>>()
    val productsLiveData: LiveData<List<ProductDTO>> = _productsLiveData

    private val _productImagesLiveData = MutableLiveData<Map<String, Uri>>()
    val productImagesLiveData: LiveData<Map<String, Uri>> = _productImagesLiveData

    val productId = MutableLiveData<String>()

    fun fetchAllData(context: Context) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                fetchAllBrands(context)
                fetchAllCategories(context)
            } catch (e: Exception) {
                errorMessage.value = "Failed to load data"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun fetchAllProducts(context: Context) {

        viewModelScope.launch {
            val productService = RetrofitInstance.getProductApi(context)
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val call = productService.getAllProducts("Bearer $accessToken")
            call.enqueue(object : retrofit2.Callback<List<ProductDTO>> {
                override fun onResponse(
                    call: retrofit2.Call<List<ProductDTO>>,
                    response: retrofit2.Response<List<ProductDTO>>
                ) {
                    if (response.isSuccessful) {
                        val products = response.body() ?: emptyList()
                        _productsLiveData.postValue(products)
                        products.forEach { product ->
                            fetchProductImage(context, product.id)
                        }
                    } else {
                        Log.e("DEBUG", " ${getCurrentStackTrace()} Failed to fetch products: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<ProductDTO>>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Timeout error fetching products", t)
                    } else {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching products", t)
                    }
                }
            }
            )
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


    fun addBrand(brandCreateDTO: BrandCreateDTO, context: Context) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                return@launch
            }
            val brandService = RetrofitInstance.getBrandApi(context)
            val response = executeRequest(context) {
                brandService.addBrand("Bearer $accessToken", brandCreateDTO)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { brands ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Brand added successfully: $brands")
                    fetchAllBrands(context)
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error adding brand: ${response?.errorBody()?.string()}")
            }
        }
    }


    fun deleteBrand(id: Int, context: Context) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                return@launch
            }
            val brandService = RetrofitInstance.getBrandApi(context)
            val response = executeRequest(context) {
                brandService.deleteBrand("Bearer $accessToken", id)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Brand deleted successfully with id: $id")
                fetchAllBrands(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error deleting brand: ${response?.errorBody()?.string()}")
            }
        }
    }

    fun addCategory(categoryCreateDTO: ProductCategoryCreateDTO, context: Context) {
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

    fun deleteCategory(id: Int, context: Context) {
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

    fun addProduct(context: Context) {
        viewModelScope.launch {
            val productCreateDTO = ProductCreateDTO(
                title = title.value ?: "",
                description = description.value,
                ingredients = ingredients.value,
                nutritionalValues = nutritionalValues.value,
                productPrice = productPrice.value ?: BigDecimal.ZERO,
                deliveryPrice = deliveryPrice.value ?: BigDecimal.ZERO,
                productWeight = productWeight.value,
                availability = availability.value ?: ProductDTO.Availability.UNAVAILABLE,
                quantity = quantity.value ?: 0,
                brand = brand.value ?: BrandDTO(0, "", ""),
                productCategory = productCategory.value ?: ProductCategoryDTO(0, ""),
                onSale = onSale.value ?: false,
                discountedPrice = discountedPrice.value
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
                    productId.postValue(tokenMap["productId"])
                    Log.d("DEBUG", "${getCurrentStackTrace()} ${message}: ${productId.value}")
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error adding product: ${response?.errorBody()?.string()}")
            }
        }
    }


    fun deleteProduct(productId: String, context: Context) {
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
                successMessage.postValue("Product deleted successfully")
                fetchAllProducts(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error deleting product: ${response?.errorBody()?.string()}")
                errorMessage.postValue("Error deleting product")
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

    fun uploadProductImage(context: Context, productId: String, imageUri: Uri) {
        viewModelScope.launch {
            //stampa nel log productId e imageUri
            Log.d("DEBUG", "${getCurrentStackTrace()} productId: $productId, imageUri: $imageUri")
            uploadImage(context, productId, imageUri)

        }
    }

    private suspend fun uploadImage(context: Context, productId: String, imageUri: Uri) {
        withContext(Dispatchers.IO) {
            val file = getFileFromUri(context, imageUri) ?: return@withContext

            // Prepare the file part
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            // Prepare other parts
            val descriptionPart = "Product Image".toRequestBody("text/plain".toMediaTypeOrNull())
            val productIdPart = productId.toRequestBody("text/plain".toMediaTypeOrNull())

            try {
                val accessToken = TokenManager.getInstance().getAccessToken(context)
                val productImageService = RetrofitInstance.getProductImageApi(context)
                val call = productImageService.savePhotoProduct("Bearer $accessToken", body, productIdPart, descriptionPart)
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



    private fun deletePhotoProduct(context: Context, productId: String) {
        val accessToken = TokenManager.getInstance().getAccessToken(context)

        val productImageService = RetrofitInstance.getProductImageApi(context)
        val call = productImageService.deletePhotoProduct("Bearer $accessToken", productId)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    Log.e("DEBUG", "${getCurrentStackTrace()},Image delete failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("DEBUG", "${getCurrentStackTrace()},Image delete error: ${t.message}")
            }
        })
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
            val tempFile = File.createTempFile("product", "jpg", context.cacheDir)
            val inputStream = responseBody.byteStream()
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            tempFile
        }
    }

    private fun fetchProductImage(context: Context, productId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val type = "product_photos"
                    val folderName = productId
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