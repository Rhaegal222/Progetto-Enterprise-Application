package com.android.frontend.view_models.user

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.Request
import com.android.frontend.config.TokenManager
import com.android.frontend.dto.CartItemDTO
import com.android.frontend.dto.creation.OrderCreateDTO
import com.android.frontend.service.OrderService
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.UUID

class OrderViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _hasError = MutableLiveData<Boolean>()
    val hasError: LiveData<Boolean> get() = _hasError

    private val _orderCreated = MutableLiveData<Boolean>()
    val orderCreated: LiveData<Boolean> get() = _orderCreated

    fun addOrder(context: Context, items: List<CartItemDTO>, addressId: UUID, paymentMethodId: UUID, userId: UUID) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            _orderCreated.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }

            val orderCreateDTO = OrderCreateDTO(
                items,
                addressId,
                paymentMethodId,
                userId
            )
            val orderService: OrderService = RetrofitInstance.getOrderApi(context)
            val response = Request().executeRequest(context) {
                orderService.addOrder("Bearer $accessToken", orderCreateDTO)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "Order created successfully")
                _orderCreated.value = true
            } else {
                Log.e("DEBUG", "Failed to create order: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }
}
