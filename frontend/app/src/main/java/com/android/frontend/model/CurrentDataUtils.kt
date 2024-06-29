package com.android.frontend.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object CurrentDataUtils {

    private var _currentProductId: String = ""
    private var _tokenExpired: Boolean = false

    var currentProductId: String
        get() = _currentProductId
        set(newValue){
            _currentProductId = newValue
        }

    var tokenExpired: Boolean
        get() = _tokenExpired
        set(newValue){
            _tokenExpired = newValue
        }
}