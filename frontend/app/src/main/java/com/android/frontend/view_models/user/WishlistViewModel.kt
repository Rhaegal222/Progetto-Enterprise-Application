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
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.ProductDTO
import com.android.frontend.dto.WishlistDTO
import com.android.frontend.dto.creation.WishlistCreateDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream

class WishlistViewModel : ViewModel() {
    private val _wishlist = MutableLiveData<List<WishlistDTO>>()
    val wishlistLiveData: MutableLiveData<List<WishlistDTO>> get() = _wishlist

    private val _productsLiveData = MutableLiveData<List<ProductDTO>>()
    val productsLiveData: LiveData<List<ProductDTO>> = _productsLiveData

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError

    private val _productImagesLiveData = MutableLiveData<Map<Long, Uri>>()
    val productImagesLiveData: LiveData<Map<Long, Uri>> = _productImagesLiveData

    private val wishlistDetails = MutableLiveData<WishlistDTO>()
    val wishlistDetailsLiveData: MutableLiveData<WishlistDTO> get() = wishlistDetails

    fun createWishlist(
        context: Context,
        wishlistName: String,
        visibility: WishlistDTO.WishlistVisibility
    ) {
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
            val wishlistCreateDTO = WishlistCreateDTO(wishlistName, visibility)
            val response = Request().executeRequest(context) {
                wishlistService.addWishlist("Bearer $accessToken", wishlistCreateDTO)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { wishlist ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Added wishlist: $wishlist")
                    getAllLoggedUserWishlists(context)
                }
            } else {
                Log.e(
                    "DEBUG",
                    "${getCurrentStackTrace()} Failed to add shipping address: ${
                        response?.errorBody()?.string() ?: "Empty response"
                    }"
                )
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun deleteWishlist(context: Context, wishlistId: String) {
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
                wishlistService.deleteWishlist("Bearer $accessToken", wishlistId)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Deleted wishlist with ID: $wishlistId")
                getAllLoggedUserWishlists(context)
            } else {
                Log.e(
                    "DEBUG",
                    "${getCurrentStackTrace()} Failed to delete wishlist: ${
                        response?.errorBody()?.string() ?: "Empty response"
                    }"
                )
                _hasError.value = true
                _isLoading.value = false
            }
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

    fun getWishlistDetails(context: Context, wishlistId: String) {
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
                wishlistService.getProductByWishlistId("Bearer $accessToken", wishlistId)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Fetched all products")
                val products = response.body() ?: emptyList()
                _productsLiveData.postValue(products)
                products.forEach { product ->
                    fetchProductImage(context, product.id)
                }
            } else {
                Log.e("DEBUG",
                    "${getCurrentStackTrace()} Failed to fetch products: ${ response?.errorBody()?.string() ?: "Empty response"}")
                _hasError.value = true
            }
        }
    }

    fun addProductToWishlist(context: Context,wishlistId: String, productId: Long) {
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
                wishlistService.addProductToWishlist("Bearer $accessToken", wishlistId, productId)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { wishlist ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Added product to wishlist: $wishlist")
                    getAllLoggedUserWishlists(context)
                }
            } else {
                Log.e(
                    "DEBUG",
                    "${getCurrentStackTrace()} Failed to add product to wishlist: ${
                        response?.errorBody()?.string() ?: "Empty response"
                    }"
                )
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun removeProductFromWishlist(context: Context, productId: Long, wishlistId: String) {
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
                wishlistService.removeProductsFromWishlist(
                    "Bearer $accessToken",
                    wishlistId,
                    productId
                )
            }
            if (response?.isSuccessful == true) {
                Log.d(
                    "DEBUG",
                    "${getCurrentStackTrace()} Removed product from wishlist: $productId"
                )
                getWishlistDetails(context, wishlistId)
            } else {
                Log.e(
                    "DEBUG",
                    "${getCurrentStackTrace()} Failed to remove product from wishlist: ${
                        response?.errorBody()?.string() ?: "Empty response"
                    }"
                )
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun updateWishlist(context: Context, wishlistId: String, wishlistDTO: WishlistDTO) {
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
                wishlistService.updateWishlist("Bearer $accessToken", wishlistId, wishlistDTO)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { wishlist ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Updated wishlist: $wishlist")
                    getWishlistDetails(context, wishlistId)
                    }
            } else {
                Log.e(
                    "DEBUG",
                    "${getCurrentStackTrace()} Failed to update wishlist: ${
                        response?.errorBody()?.string() ?: "Empty response"
                    }"
                )
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun getWishlistById(context: Context, wishlistId: String) {
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
                wishlistService.getWishlistById("Bearer $accessToken", wishlistId)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { wishlist ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Fetched   wishlist: $wishlist")
                    wishlistDetails.value = wishlist
                    }
            } else {
                Log.e(
                    "DEBUG", "${getCurrentStackTrace()} Failed to fetch wishlist: ${response?.errorBody()?.string() ?: "Empty response"}")
                _hasError.value = true
            }
            _isLoading.value = false
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


