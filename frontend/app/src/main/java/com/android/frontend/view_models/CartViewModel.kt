package com.android.frontend.view_models

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.dto.CartCreateDTO
import com.android.frontend.dto.CartDTO
import com.android.frontend.persistence.SecurePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val _cart = MutableStateFlow<CartDTO?>(null)
    val cart: StateFlow<CartDTO?> = _cart

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount

    fun loadCart(context: Context) {
        val cartService = RetrofitInstance.getCartApi(context)
        val userId = SecurePreferences.getUser(context)?.id ?: ""
        viewModelScope.launch(Dispatchers.IO) {
            val response = cartService.getCartByUserId(userId).execute()
            if (response.isSuccessful) {
                response.body()?.let {
                    _cart.value = it
                    _cartItemCount.value = it.cartItems.size
                }
            }
        }
    }

    fun updateCartItem(cartItemId: String, quantity: Int, context: Context) {
        val cartService = RetrofitInstance.getCartApi(context)
        viewModelScope.launch(Dispatchers.IO) {
            val response = cartService.updateCartItem(cartItemId, quantity).execute()
            if (response.isSuccessful) {
                loadCart(context)
            }
        }
    }

    fun addProductToCart(userId: String, productId: String, quantity: Int, context: Context) {
        val cartService = RetrofitInstance.getCartApi(context)
        val cartCreateDTO = CartCreateDTO(userId, productId, quantity)
        viewModelScope.launch(Dispatchers.IO) {
            val response = cartService.addProductToCart(cartCreateDTO).execute()
            if (response.isSuccessful) {
                loadCart(context)  // Aggiorna il carrello dopo aver aggiunto l'elemento
            }
        }
    }


    fun removeCartItem(cartItemId: String, context: Context) {
        val cartService = RetrofitInstance.getCartApi(context)
        viewModelScope.launch(Dispatchers.IO) {
            val response = cartService.removeProductFromCart(cartItemId).execute()
            if (response.isSuccessful) {
                loadCart(context)
            }
        }
    }
}
