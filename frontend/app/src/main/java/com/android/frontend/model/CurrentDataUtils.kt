package com.android.frontend.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.UserBasicDTO
import com.android.frontend.service.UserService
import com.android.frontend.controller.models.PaymentMethodDTO

object CurrentDataUtils {

    private val userService: UserService = RetrofitInstance.api

    private var _currentProductId: String = ""
    private var _visitedUser: MutableState<UserBasicDTO?> = mutableStateOf(null)
    private var _defaultPaymentMethod: MutableState<PaymentMethodDTO?> = mutableStateOf(null)
    private var _showLoadingScreen: MutableState<Boolean> = mutableStateOf(true)
    private var _goToHome: MutableState<Boolean> = mutableStateOf(false)

    val showLoadingScreen: MutableState<Boolean>
        get() = _showLoadingScreen

    val goToHome: MutableState<Boolean>
        get() = _goToHome

    var currentProductId: String
        get() = _currentProductId
        set(newValue){
            _currentProductId = newValue
        }
}