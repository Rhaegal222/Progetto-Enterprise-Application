package com.android.frontend.view_models.user

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.Request
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.CartDTO
import com.android.frontend.dto.CartItemDTO
import com.android.frontend.dto.ProductDTO
import com.android.frontend.dto.creation.CartItemCreateDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CartViewModel : ViewModel() {
    private val _cart = MutableLiveData<CartDTO>()
    val cart: LiveData<CartDTO?> get() = _cart

    val cartItems: LiveData<List<CartItemDTO>> get() = cart.map { it?.items ?: emptyList() }

    private val _cartItemCount = MutableLiveData(0)
    val cartItemCount: LiveData<Int> get() = _cartItemCount

    private val productDetailsCache = mutableMapOf<Long, MutableStateFlow<ProductDTO?>>()

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError

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
            val response = Request().executeRequest(context){
                Log.d("DEBUG", "${getCurrentStackTrace()} Getting cart for logged user")
                cartService.getCartForLoggedUser("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    _cart.value = it
                    _cartItemCount.value = it.items.size
                }
            }
            else {
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
                val response = Request().executeRequest(context){
                    productService.getProductById("Bearer $accessToken", id)
                }
                if (response?.isSuccessful == true){
                    response.body()?.let { product ->
                        Log.d("DEBUG", "${getCurrentStackTrace()} Product details: $product")
                        productDetailsCache[id]?.value = product
                    }
                }
                else {
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
                cartService.addItemToCart("Bearer $accessToken",cartItemCreateDTO)
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
            _hasError.value = true
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
}
