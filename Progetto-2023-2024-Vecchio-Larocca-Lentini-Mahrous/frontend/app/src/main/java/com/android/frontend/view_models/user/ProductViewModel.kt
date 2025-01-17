package com.android.frontend.view_models.user

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
import com.android.frontend.dto.ProductDTO
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.BrandDTO
import com.android.frontend.dto.CategoryDTO
import com.android.frontend.dto.WishlistDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class ProductViewModel : ViewModel() {

    private val _productsLiveData = MutableLiveData<List<ProductDTO>>()
    val productsLiveData: LiveData<List<ProductDTO>> = _productsLiveData

    private val _productImagesLiveData = MutableLiveData<Map<Long, Uri>>()
    val productImagesLiveData: LiveData<Map<Long, Uri>> = _productImagesLiveData

    private val _categoriesLiveData = MutableLiveData<List<CategoryDTO>>()
    val categoriesLiveData: LiveData<List<CategoryDTO>> = _categoriesLiveData

    private val _brandsLiveData = MutableLiveData<List<BrandDTO>>()
    val brandsLiveData: LiveData<List<BrandDTO>> = _brandsLiveData

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError

    private val _wishlist = MutableLiveData<List<WishlistDTO>>()
    val wishlistLiveData: MutableLiveData<List<WishlistDTO>> get() = _wishlist

    private val imageDownloadSemaphore = Semaphore(5)

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
                Log.e(
                    "DEBUG",
                    "${getCurrentStackTrace()} Failed to fetch all products: ${
                        response?.errorBody()?.string()
                    }"
                )
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    private suspend fun fetchProductImage(context: Context, productId: Long) {
        viewModelScope.launch {
            try {
                imageDownloadSemaphore.withPermit {  // Usa il permesso del semaforo
                    val responseBody = getPhotoProductById(context, productId)
                    responseBody?.let {
                        val tempFile = saveImageToFile(context, it)
                        val imageUri = Uri.fromFile(tempFile)
                        val currentImages =
                            _productImagesLiveData.value?.toMutableMap() ?: mutableMapOf()
                        currentImages[productId] = imageUri
                        _productImagesLiveData.postValue(currentImages)
                    } ?: run {
                        Log.e("DEBUG", "${getCurrentStackTrace()}, Image retrieval failed")
                        _hasError.postValue(true)
                    }
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Image retrieval error: ${e.message}")
                _hasError.postValue(true)
            }
        }
    }

    private suspend fun getPhotoProductById(context: Context, productId: Long): ResponseBody? {
        return withContext(Dispatchers.IO) {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
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
                Log.e(
                    "DEBUG",
                    "${getCurrentStackTrace()} Error fetching image: ${
                        response?.errorBody()?.string()
                    }"
                )
                _hasError.postValue(true)
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

    fun fetchSalesProducts(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
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
                    Log.e(
                        "DEBUG",
                        "${getCurrentStackTrace()} Failed to fetch sales products: ${
                            response.errorBody()?.string()
                        }"
                    )
                    _hasError.value = true
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching sales products", e)
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun fetchProductsByCategory(context: Context, categoryName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val productService = RetrofitInstance.getProductApi(context)
            val response = Request().executeRequest(context) {
                productService.getProductsByCategory("Bearer $accessToken", categoryName)
            }
            if (response?.isSuccessful == true) {
                val products = response.body() ?: emptyList()
                _productsLiveData.postValue(products)
                products.forEach { product ->
                    fetchProductImage(context, product.id)
                }
            } else {
                Log.e(
                    "DEBUG",
                    "${getCurrentStackTrace()} Failed to fetch products by category: ${
                        response?.errorBody()?.string()
                    }"
                )
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun fetchProductsByBrand(context: Context, brandName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val productService = RetrofitInstance.getProductApi(context)
            val response = Request().executeRequest(context) {
                productService.getProductsByBrand("Bearer $accessToken", brandName)
            }
            if (response?.isSuccessful == true) {
                val products = response.body() ?: emptyList()
                _productsLiveData.postValue(products)
                products.forEach { product ->
                    fetchProductImage(context, product.id)
                }
            } else {
                Log.e(
                    "DEBUG",
                    "${getCurrentStackTrace()} Failed to fetch products by brand: ${
                        response?.errorBody()?.string()
                    }"
                )
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun fetchProductsByPriceRange(context: Context, min: Double, max: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val productService = RetrofitInstance.getProductApi(context)
            val response = Request().executeRequest(context) {
                productService.getProductsByPriceRange("Bearer $accessToken", min, max)
            }
            if (response?.isSuccessful == true) {
                val products = response.body() ?: emptyList()
                _productsLiveData.postValue(products)
                products.forEach { product ->
                    fetchProductImage(context, product.id)
                }
            } else {
                Log.e(
                    "DEBUG",
                    "${getCurrentStackTrace()} Failed to fetch products by price range: ${
                        response?.errorBody()?.string()
                    }"
                )
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun fetchAllCategories(context: Context) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val categoryService = RetrofitInstance.getProductCategoryApi(context)
            val call = categoryService.getAllCategories("Bearer $accessToken")
            call.enqueue(object : Callback<List<CategoryDTO>> {
                override fun onResponse(call: Call<List<CategoryDTO>>, response: Response<List<CategoryDTO>>) {
                    if (response.isSuccessful) {
                        _categoriesLiveData.postValue(response.body() ?: emptyList())
                    } else {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch categories: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<CategoryDTO>>, t: Throwable) {
                    Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching categories", t)
                }
            })
        }
    }

    fun fetchAllBrands(context: Context) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val brandService = RetrofitInstance.getBrandApi(context)
            val call = brandService.getAllBrands("Bearer $accessToken")
            call.enqueue(object : Callback<List<BrandDTO>> {
                override fun onResponse(call: Call<List<BrandDTO>>, response: Response<List<BrandDTO>>) {
                    if (response.isSuccessful) {
                        _brandsLiveData.postValue(response.body() ?: emptyList())
                    } else {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch brands: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<BrandDTO>>, t: Throwable) {
                    Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching brands", t)
                }
            })
        }
    }
    fun getAllLoggedUserWishlists(context: Context) {
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
            val wishlistService = RetrofitInstance.getWishlistApi(context)
            val response = Request().executeRequest(context) {
                wishlistService.getAllLoggedUserWishlists("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { wishlists ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Fetched wishlists: $wishlists")
                    _wishlist.value = wishlists
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch wishlists: ${response?.errorBody()?.string() ?: "Empty response"}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun searchProducts(context: Context, query: String) {
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
                Log.d("DEBUG", "${getCurrentStackTrace()} Fetched all products for search")
                val products = response.body()?.filter { it.name.contains(query, ignoreCase = true) } ?: emptyList()
                _productsLiveData.postValue(products)
                products.forEach { product ->
                    fetchProductImage(context, product.id)
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch products for search: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }
}
