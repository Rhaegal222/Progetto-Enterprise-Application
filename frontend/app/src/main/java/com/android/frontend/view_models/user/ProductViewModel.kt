package com.android.frontend.view_models.user


import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.TokenManager
import com.android.frontend.dto.CartDTO
import com.android.frontend.dto.ProductDTO
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.creation.CartItemCreateDTO
import com.android.frontend.dto.creation.ProductCreateDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.net.SocketTimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProductViewModel : ViewModel() {

    private val _productsLiveData = MutableLiveData<List<ProductDTO>>()
    val productsLiveData: LiveData<List<ProductDTO>> = _productsLiveData


    private val productDetails = MutableLiveData<ProductDTO>()
    val productDetailsLiveData: LiveData<ProductDTO> get() = productDetails


    private val _productImagesLiveData = MutableLiveData<Map<Long, Uri>>()
    val productImagesLiveData: LiveData<Map<Long, Uri>> = _productImagesLiveData

    fun getProductDetails(context: Context, id: Long) {
        viewModelScope.launch {
            val productService = RetrofitInstance.getProductApi(context)
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val call = productService.getProductById("Bearer $accessToken",id)
            call.enqueue(object : retrofit2.Callback<ProductDTO> {
                override fun onResponse(
                    call: retrofit2.Call<ProductDTO>,
                    response: retrofit2.Response<ProductDTO>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { product ->
                            Log.d("DEBUG", "${getCurrentStackTrace()} Product details: $product")
                            productDetails.value = product
                        }
                    } else {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch products: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<ProductDTO>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Timeout error fetching product details", t)
                    } else {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching product details", t)
                    }
                }
            }
            )
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

    fun fetchSalesProducts(context: Context) {
        viewModelScope.launch {
            try {
                val accessToken = TokenManager.getInstance().getAccessToken(context)
                val productService = RetrofitInstance.getProductApi(context)
                val response = productService.getSalesProducts("Bearer $accessToken")
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

    fun fetchProductsByCategory(context: Context, categoryName: String) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val productService = RetrofitInstance.getProductApi(context)
            val call = productService.getProductsByCategory("Bearer $accessToken", categoryName)
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
                        Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch products by category: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<ProductDTO>>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Timeout error fetching products by category", t)
                    } else {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching products by category", t)
                    }
                }
            })
        }
    }

    fun fetchProductsByBrand(context: Context, brandName: String) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val productService = RetrofitInstance.getProductApi(context)
            val call = productService.getProductsByBrand("Bearer $accessToken", brandName)
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
                        Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch products by brand: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<ProductDTO>>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Timeout error fetching products by brand", t)
                    } else {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching products by brand", t)
                    }
                }
            })
        }
    }

    fun fetchProductsByPriceRange(context: Context, min: Double, max: Double) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val productService = RetrofitInstance.getProductApi(context)
            val call = productService.getProductsByPriceRange("Bearer $accessToken", min, max)
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
                        Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch products by price range: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<ProductDTO>>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Timeout error fetching products by price range", t)
                    } else {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching products by price range", t)
                    }
                }
            })
        }
    }

}

