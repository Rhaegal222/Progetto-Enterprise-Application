package com.android.frontend.view_models.user

import android.content.Context
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
import kotlinx.coroutines.launch

class WishlistViewModel : ViewModel() {
    private val _wishlist = MutableLiveData<List<WishlistDTO>>()
    val wishlistLiveData: MutableLiveData<List<WishlistDTO>> get() = _wishlist

    private val _product = MutableLiveData<List<ProductDTO>>()
    val productsLiveData: LiveData<List<ProductDTO>> = _product

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError

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

    fun deleteWishlist(context: Context, wishlistId: Long) {
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

    fun getWishlistDetails(context: Context, wishlistId: Long) {
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
                response.body()?.let {product -> Log.d("DEBUG", "${getCurrentStackTrace()} Fetched products: $product")
                    _product.value = product
                }
            } else {
                Log.e("DEBUG",
                    "${getCurrentStackTrace()} Failed to fetch products: ${ response?.errorBody()?.string() ?: "Empty response"}")
                _hasError.value = true
            }
        }
    }

    fun addProductToWishlist(context: Context,wishlistId: Long, productId: Long) {
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
    fun removeProductFromWishlist(context: Context, productId: Long, wishlistId: Long) {
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
}


