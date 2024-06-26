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
    var _application: Application? = null

    val currentUser: UserDTO?
        get() = _currentUser.value

    val showLoadingScreen: MutableState<Boolean>
        get() = _showLoadingScreen

    val goToHome: MutableState<Boolean>
        get() = _goToHome

    var accessToken: String
        get() = SecurePreferences.getAccessToken(_application!!.applicationContext) ?: ""
        set(newValue) {
            SecurePreferences.saveAccessToken(_application!!.applicationContext, newValue)
        }

    var refreshToken: String
        get() = SecurePreferences.getRefreshToken(_application!!.applicationContext) ?: ""
        set(newValue) {
            SecurePreferences.saveRefreshToken(_application!!.applicationContext, newValue)
        }

    fun retrieveCurrentUser() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userService.me("Bearer $accessToken").execute()
                if (response.isSuccessful) {
                    val user = response.body()
                    _currentUser.value = user
                    user?.let { SecurePreferences.saveUser(_application!!.applicationContext, it) }
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
            SecurePreferences.clearAll(_application!!.applicationContext)
            _goToHome.value = false
        }
    }

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

    fun setRefresh(refresh_token: String) {
        refreshToken = refresh_token
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
