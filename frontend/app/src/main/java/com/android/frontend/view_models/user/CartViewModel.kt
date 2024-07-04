package com.android.frontend.view_models.user

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.CartDTO
import com.android.frontend.dto.ProductDTO
import com.android.frontend.dto.creation.CartItemCreateDTO
import com.android.frontend.persistence.SecurePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.util.UUID

class CartViewModel : ViewModel() {
    private val _cart = MutableStateFlow<CartDTO?>(null)
    val cart: StateFlow<CartDTO?> = _cart

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount

    private val productDetailsCache = mutableMapOf<Long, MutableStateFlow<ProductDTO?>>()

    fun getProductDetails(context: Context, id: Long): Flow<ProductDTO?> {
        if (!productDetailsCache.containsKey(id)) {
            productDetailsCache[id] = MutableStateFlow(null)
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
                                Log.d("DEBUG", "${getCurrentStackTrace()} Product details: $product")
                                productDetailsCache[id]?.value = product
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
                })
            }
        }
        return productDetailsCache[id]!!.asStateFlow()
    }

    fun loadCart(context: Context) {
        val cartService = RetrofitInstance.getCartApi(context)
        val accessToken = TokenManager.getInstance().getAccessToken(context)
        viewModelScope.launch(Dispatchers.IO) {
            val response = cartService.getCartForLoggedUser("Bearer ${accessToken}").execute()
            if (response.isSuccessful) {
                response.body()?.let {
                    _cart.value = it
                    _cartItemCount.value = it.items.size
                }
            }
        }
    }

    fun updateCartItem(cartItemId: UUID, quantity: Int, context: Context) {
        val cartService = RetrofitInstance.getCartApi(context)
        val accessToken = TokenManager.getInstance().getAccessToken(context)
        viewModelScope.launch(Dispatchers.IO) {
            val response = cartService.editItemInCart("Bearer ${accessToken}",cartItemId, quantity).execute()
            if (response.isSuccessful) {
                loadCart(context)
            }
        }
    }

    fun addProductToCart(productId: Long , quantity: Int, context: Context) {
        val cartService = RetrofitInstance.getCartApi(context)
        val cartItemCreateDTO = CartItemCreateDTO(productId, quantity)
        val accessToken = TokenManager.getInstance().getAccessToken(context)

        viewModelScope.launch(Dispatchers.IO) {
            val response = cartService.addItemToCart("Bearer ${accessToken}",cartItemCreateDTO).execute()
            if (response.isSuccessful) {
                loadCart(context)
            }
        }
    }

    fun removeCartItem(cartItemId: UUID, context: Context) {
        val cartService = RetrofitInstance.getCartApi(context)
        val accessToken = TokenManager.getInstance().getAccessToken(context)

        viewModelScope.launch(Dispatchers.IO) {
            val response = cartService.removeItemFromCart("Bearer ${accessToken}", cartItemId).execute()
            if (response.isSuccessful) {
                loadCart(context)
            }
        }
    }

    fun clearCart(context: Context) {
        val cartService = RetrofitInstance.getCartApi(context)
        val accessToken = TokenManager.getInstance().getAccessToken(context)

        viewModelScope.launch(Dispatchers.IO) {
            val response = cartService.clearCart("Bearer ${accessToken}").execute()
            if (response.isSuccessful) {
                loadCart(context)
            }
        }
    }
}
