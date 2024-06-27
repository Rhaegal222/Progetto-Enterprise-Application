package com.android.frontend.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.AddressDTO
import com.android.frontend.controller.models.UserBasicDTO
import com.android.frontend.controller.models.UserDTO
import com.android.frontend.service.UserService
import com.example.frontend.controller.models.PaymentMethodDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

object CurrentDataUtils {

    private val userService: UserService = RetrofitInstance.api

    private var _accessToken: MutableState<String> = mutableStateOf("")
    private var _refreshToken: MutableState<String> = mutableStateOf("")
    private var _userId: MutableState<String> = mutableStateOf("")
    private var _currentUser: MutableState<UserDTO?> = mutableStateOf(null)
    private var _currentProductId: MutableState<String> = mutableStateOf("")
    private var _visitedUser: MutableState<UserBasicDTO?> = mutableStateOf(null)
    private var _currentAddress: MutableState<AddressDTO?> = mutableStateOf(null)
    private var _Addresses = mutableStateListOf<AddressDTO>()
    private var _PaymentsMethod = mutableStateListOf<PaymentMethodDTO>()
    private var _currentPaymentMethod: MutableState<PaymentMethodDTO?> = mutableStateOf(null)
    private var _defaultAddress: MutableState<AddressDTO?> = mutableStateOf(null)
    private var _currentAddresses = mutableStateListOf<AddressDTO>()
    private var _defaultPaymentMethod: MutableState<PaymentMethodDTO?> = mutableStateOf(null)
    private var _showLoadingScreen: MutableState<Boolean> = mutableStateOf(true)
    private var _goToHome: MutableState<Boolean> = mutableStateOf(false)


    val currentUser: UserDTO?
        get() = _currentUser.value

    val showLoadingScreen: MutableState<Boolean>
        get() = _showLoadingScreen

    val goToHome: MutableState<Boolean>
        get() = _goToHome

    val PaymentsMethod: SnapshotStateList<PaymentMethodDTO>
        get() = _PaymentsMethod

    var currentPaymentMethodDTO: MutableState<PaymentMethodDTO?>
        get() = _currentPaymentMethod
        set(newValue){ _currentPaymentMethod = newValue}

    fun retrieveAddresses() {
        _Addresses.clear()
        _currentUser.value?.addresses?.let { _Addresses.addAll(it.toList()) }
        _Addresses.forEach { a ->
            if (a.isDefault)
                _defaultAddress.value = a
        }
    }

    fun retrievePaymentsMethod() {
        _PaymentsMethod.clear()
        _currentUser.value?.paymentMethods?.let { _PaymentsMethod.addAll(it.toList()) }
        _PaymentsMethod.forEach { p ->
            if (p.isDefault)
                _defaultPaymentMethod.value = p
        }
    }

    fun retrieveCurrentUser() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    userService.me("Bearer ${_accessToken.value}").awaitResponse()
                }
                if (response.isSuccessful) {
                    _currentUser.value = response.body()
                    retrieveAddresses()
                    retrievePaymentsMethod()
                } else {
                    // Handle error response if needed
                    println("Failed to retrieve current user: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("Exception in retrieveCurrentUser: $e")
            }
        }
    }
}