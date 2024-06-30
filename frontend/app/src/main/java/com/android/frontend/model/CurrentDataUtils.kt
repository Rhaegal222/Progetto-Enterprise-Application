package com.android.frontend.model

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import java.util.concurrent.atomic.AtomicInteger

object CurrentDataUtils {

    private var _currentProductId: String = ""
    var refreshAttempts = AtomicInteger(0)

    var currentProductId: String
        get() = _currentProductId
        set(newValue){
            _currentProductId = newValue
        }
}