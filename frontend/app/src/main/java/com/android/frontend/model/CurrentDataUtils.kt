package com.android.frontend.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object CurrentDataUtils {

    private var _currentProductId: String = ""

    var currentProductId: String
        get() = _currentProductId
        set(newValue){
            _currentProductId = newValue
        }
}