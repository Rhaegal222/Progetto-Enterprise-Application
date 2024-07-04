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
import com.android.frontend.dto.OrderDTO
import com.android.frontend.dto.creation.OrderCreateDTO
import com.android.frontend.dto.OrderItemDTO
import it.unical.inf.ea.backend.data.services.interfaces.OrderService
import kotlinx.coroutines.launch
import java.util.UUID

class OrderViewModel : ViewModel() {

    private val _orders = MutableLiveData<List<OrderDTO>>()
    val ordersLiveData: LiveData<List<OrderDTO>> get() = _orders
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError

    var orderId by mutableStateOf("")
    var orderItems by mutableStateOf(listOf<OrderItemDTO>())

    fun getAllOrders(context: Context) {
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
            val orderService: OrderService = RetrofitInstance.getOrderApi(context)
            val response = Request().executeRequest(context) {
                orderService.getAllOrders("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    _orders.value = it
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch orders: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun addOrder(context: Context, orderCreateDTO: OrderCreateDTO) {
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
            val orderService: OrderService = RetrofitInstance.getOrderApi(context)
            val response = Request().executeRequest(context) {
                orderService.addOrder("Bearer $accessToken", orderCreateDTO)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { order ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Added order: $order")
                    getAllOrders(context)
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to add order: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun updateOrder(context: Context, id: UUID, orderItemDTO: OrderItemDTO) {
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
            val orderService: OrderService = RetrofitInstance.getOrderApi(context)
            val response = Request().executeRequest(context) {
                orderService.updateOrder("Bearer $accessToken", id.toString(), orderItemDTO)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { order ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Updated order: $order")
                    getAllOrders(context)
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to update order: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun deleteOrder(context: Context, id: UUID) {
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
            val orderService: OrderService = RetrofitInstance.getOrderApi(context)
            val response = Request().executeRequest<Void>(context) {
                orderService.deleteOrder("Bearer $accessToken", id.toString())
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Deleted order with id: $id")
                getAllOrders(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to delete order: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun getOrderById(context: Context, id: UUID) {
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
            val orderService: OrderService = RetrofitInstance.getOrderApi(context)
            val response = Request().executeRequest(context) {
                orderService.getOrder("Bearer $accessToken", id.toString())
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { order ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} Fetched order: $order")
                    // Handle the fetched order as needed
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch order: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }

    fun getAllLoggedUserOrders(context: Context) {
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
            val orderService: OrderService = RetrofitInstance.getOrderApi(context)
            val response = Request().executeRequest(context) {
                orderService.getAllLoggedUserOrders("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                response.body()?.let {
                    _orders.value = it
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch logged user orders: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }
}
