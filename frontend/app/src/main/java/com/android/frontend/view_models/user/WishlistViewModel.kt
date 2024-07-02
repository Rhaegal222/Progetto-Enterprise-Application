package com.android.frontend.view_models.user

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.Request
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.WishlistDTO
import com.android.frontend.dto.creation.WishlistCreateDTO
import kotlinx.coroutines.launch

class WishlistViewModel : ViewModel() {
    private val wishlist = MutableLiveData<List<WishlistDTO>>()
    val wishlistLiveData: MutableLiveData<List<WishlistDTO>> get() = wishlist
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError
    private val wishlistDetails = MutableLiveData<WishlistDTO>()
    val wishlistDetailsLiveData: MutableLiveData<WishlistDTO> get() = wishlistDetails

    var wishlistname by mutableStateOf("")
    var visibility by mutableStateOf("")

    fun createWishlist(
        context: Context,
        wishlistname: String,
        visibility: WishlistDTO.Visibility
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
            val wishlistCreateDTO = WishlistCreateDTO(wishlistname, visibility)
            val response = Request().executeRequest(context) {
                wishlistService.createWishlist("Bearer $accessToken", wishlistCreateDTO)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { shippingAddresses ->
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
}
