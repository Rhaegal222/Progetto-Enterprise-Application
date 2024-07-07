package com.android.frontend.view_models.user

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.Request
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.CartDTO
import com.android.frontend.dto.CartItemDTO
import com.android.frontend.dto.ProductDTO
import com.android.frontend.dto.creation.CartItemCreateDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class CartViewModel : ViewModel() {

    private val _cartStateFlow = MutableStateFlow<CartDTO?>(null)
    val cart: LiveData<CartDTO?> get() = _cartStateFlow.asLiveData()

    val cartItems: LiveData<List<CartItemDTO>> get() = cart.map { it?.items ?: emptyList() }

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: LiveData<Int> get() = _cartItemCount.asLiveData()

    private val productDetailsCache = mutableMapOf<Long, MutableStateFlow<ProductDTO?>>()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError

    private val _productImagesLiveData = MutableLiveData<Map<Long, Uri>>()
    val productImagesLiveData: LiveData<Map<Long, Uri>> = _productImagesLiveData

    fun <T> StateFlow<T>.asLiveData(): LiveData<T> = asLiveData(viewModelScope.coroutineContext)

    fun getCartForLoggedUser(context: Context) {
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

            val cartService = RetrofitInstance.getCartApi(context)
            val response = Request().executeRequest(context) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Getting cart for logged user")
                cartService.getCartForLoggedUser("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    _cartStateFlow.value = it
                    _cartItemCount.value = it.items.size
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch cart: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun getProductDetails(context: Context, id: Long): Flow<ProductDTO?> {
        if (!productDetailsCache.containsKey(id)) {
            productDetailsCache[id] = MutableStateFlow(null)
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
                    productService.getProductById("Bearer $accessToken", id)
                }
                if (response?.isSuccessful == true) {
                    response.body()?.let { product ->
                        Log.d("DEBUG", "${getCurrentStackTrace()} Product details: $product")
                        productDetailsCache[id]?.value = product
                        fetchProductImage(context, id)
                    }
                } else {
                    Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch products: ${response?.errorBody()?.string()}")
                    _hasError.value = true
                }
                _isLoading.value = false
            }
        }
        return productDetailsCache[id]!!.asStateFlow()
    }

    fun updateCartItem(cartItemId: UUID, quantity: Int, context: Context) {
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

            val cartService = RetrofitInstance.getCartApi(context)
            val response = Request().executeRequest(context) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Updating cart item")
                cartService.editItemInCart("Bearer $accessToken", cartItemId, quantity)
            }
            if (response?.isSuccessful == true) {
                getCartForLoggedUser(context)
            }
            else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to update cart item: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun addProductToCart(productId: Long , quantity: Int, context: Context) {
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

            val cartService = RetrofitInstance.getCartApi(context)
            val cartItemCreateDTO = CartItemCreateDTO(productId, quantity)
            val response = Request().executeRequest(context) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Adding product to cart")
                cartService.addItemToCart("Bearer $accessToken", cartItemCreateDTO)
            }

            if (response?.isSuccessful == true) {
                getCartForLoggedUser(context)
            }
            else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to add product to cart: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun removeCartItem(cartItemId: UUID, context: Context) {
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

            val cartService = RetrofitInstance.getCartApi(context)
            val response = Request().executeRequest(context) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Removing item from cart")
                cartService.removeItemFromCart("Bearer $accessToken", cartItemId)
            }
            if (response?.isSuccessful == true) {
                getCartForLoggedUser(context)
            }
            else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to remove item from cart: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun clearCart(context: Context) {
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

            val cartService = RetrofitInstance.getCartApi(context)
            val response = Request().executeRequest(context) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Clearing cart")
                cartService.clearCart("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                getCartForLoggedUser(context)
            }
            else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to clear cart: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
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
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching image: ${response?.errorBody()?.string()}")
                _hasError.postValue(true)
                null
            }
        }
    }

    private fun fetchProductImage(context: Context, productId: Long) {
        viewModelScope.launch {
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
