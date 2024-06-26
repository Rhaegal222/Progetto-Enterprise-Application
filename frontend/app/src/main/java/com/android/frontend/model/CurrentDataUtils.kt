package com.android.frontend.model

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.AddressDTO
import com.android.frontend.controller.models.PaymentMethodDTO
import com.android.frontend.controller.models.UserBasicDTO
import com.android.frontend.controller.models.UserDTO
import com.android.frontend.model.persistence.AppDatabase
import com.android.frontend.service.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CurrentDataUtils {

    private val userService: UserService = RetrofitInstance.api

    private var _accessToken: MutableState<String> = mutableStateOf("")
    private var _refreshToken: MutableState<String> = mutableStateOf("")
    private var _userId: MutableState<String> = mutableStateOf("")
    private var _currentUser: MutableState<UserDTO?> = mutableStateOf(null)
    //val _currentUser = MutableLiveData<UserDTO>()
    private var _currentProductId: MutableState<String> = mutableStateOf("")
    private var _visitedUser: MutableState<UserBasicDTO?> = mutableStateOf(null)
    private var _currentAddress: MutableState<AddressDTO?> = mutableStateOf(null)
    private var _Addresses = mutableStateListOf<AddressDTO>()
    private var _PaymentsMethod = mutableStateListOf<PaymentMethodDTO>()
    private var _currentPaymentMethod: MutableState<PaymentMethodDTO?>  = mutableStateOf(null)
    private var _defaultAddress: MutableState<AddressDTO?> = mutableStateOf(null)
    private var _currentAddresses = mutableStateListOf<AddressDTO>()
    private var _defaultPaymentMethod: MutableState<PaymentMethodDTO?> = mutableStateOf(null)


    private var _showLoadingScreen: MutableState<Boolean> = mutableStateOf(true)
    private var _goToHome: MutableState<Boolean> = mutableStateOf(false)

    var _application: Application? = null

    var accessToken: String
        get() = _accessToken.value
        set(newValue) { _accessToken.value = newValue }

    var userId: String
        get() = _userId.value
        set(newValue) { _userId.value = newValue }

    val currentUser: UserDTO?
        get() = _currentUser.value

    val showLoadingScreen: MutableState<Boolean>
        get() = _showLoadingScreen

    val goToHome: MutableState<Boolean>
        get() = _goToHome
    var refreshToken: String
        get() = _refreshToken.value
        set(newValue) { _refreshToken.value = newValue }

    fun retrieveCurrentUser() {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userService.me("Bearer $accessToken").execute()
                if (response.isSuccessful) {
                    _currentUser.value = response.body()
                    retrieveAddresses()
                    retrievePaymentsMethod()
                } else {
                    println("Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println(e)
            }
        }
    }

    fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            _currentUser.value = null
            _refreshToken.value = ""
            _accessToken.value = ""
            _goToHome.value = false
        }
    }

    fun retrieveAddresses() {
        _Addresses.clear()
        _currentUser.value?.addresses?.let { _Addresses.addAll(it.toList()) }
        _currentAddresses.forEach { a ->
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

    fun setRefresh(refresh_token: String) {
        _refreshToken.value = refresh_token
        CoroutineScope(Dispatchers.IO).launch {
            val user = com.android.frontend.model.persistence.User(null, refresh_token)
            val refreshToken2 = AppDatabase.getInstance(_application?.applicationContext!!).userDao().getRefreshToken()
            if (refreshToken2 == null) {
                AppDatabase.getInstance(_application?.applicationContext!!).userDao().insert(user)
            } else {
                AppDatabase.getInstance(_application?.applicationContext!!).userDao().update(refresh_token)
            }
        }
    }
}
