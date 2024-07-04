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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class WishlistViewModel : ViewModel() {
    private val wishlist = MutableLiveData<List<WishlistDTO>>()
    val wishlistLiveData: MutableLiveData<List<WishlistDTO>> get() = wishlist

    private val _productsLiveData = MutableLiveData<List<ProductDTO>>()
    val productsLiveData: LiveData<List<ProductDTO>> = _productsLiveData
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
                    fetchAllWishlists(context)
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

    fun fetchAllWishlists(context: Context) {
        val wishlistService = RetrofitInstance.getWishlistApi(context)
        val accessToken = TokenManager.getInstance().getAccessToken(context)
        val call = wishlistService.getAllLoggedUserWishlists("Bearer $accessToken")
        call.enqueue(object : retrofit2.Callback<List<WishlistDTO>> {
            override fun onResponse(
                call: retrofit2.Call<List<WishlistDTO>>,
                response: retrofit2.Response<List<WishlistDTO>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { wishlists ->
                        wishlist.postValue(wishlists)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<WishlistDTO>>, t: Throwable) {
                Log.e("DEBUG", "Error fetching wishlists", t)
            }
        })
    }

    fun getWishlistDetails(context: Context, wishlistId: Long,wishlistName: String) {
        viewModelScope.launch {
            try {
                val wishlistService = RetrofitInstance.getWishlistApi(context)
                val accessToken = TokenManager.getInstance().getAccessToken(context)
                val call = wishlistService.getProductByWishlistId("Bearer $accessToken", wishlistId)

                Log.d("DEBUG", "Starting API call for Wishlist ID: $wishlistId, $wishlistName with token: $accessToken")

                call.enqueue(object : Callback<List<ProductDTO>> {
                    override fun onResponse(
                        call: Call<List<ProductDTO>>,
                        response: Response<List<ProductDTO>>
                    ) {
                        Log.d("DEBUG", "API Response received for Wishlist ID: $wishlistId")
                        if (response.isSuccessful) {
                            val products = response.body() ?: emptyList()
                            _productsLiveData.postValue(products)
                            Log.d("DEBUG", "Successfully fetched products: $products")
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e("DEBUG", "Failed to fetch products: $errorBody")
                        }
                    }

                    override fun onFailure(call: Call<List<ProductDTO>>, t: Throwable) {
                        if (t is SocketTimeoutException) {
                            Log.e("DEBUG", "Timeout fetching products for Wishlist ID: $wishlistId", t)
                        } else {
                            Log.e("DEBUG", "Error fetching products for Wishlist ID: $wishlistId", t)
                        }
                    }
                })
            } catch (e: Exception) {
                Log.e("DEBUG", "Exception in getWishlistDetails: ${e.message}", e)
            }
        }
    }

    fun addProductToWishlist(context: Context, productId: Long, wishlistId: Long) {
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
                wishlistService.addProductToWishlist("Bearer $accessToken", productId, wishlistId)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { wishlist ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Added product to wishlist: $wishlist")
                    fetchAllWishlists(context)
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

}
