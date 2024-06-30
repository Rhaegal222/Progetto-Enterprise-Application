package com.android.frontend.persistence

import android.util.Log
import com.android.frontend.config.getCurrentStackTrace
import java.util.concurrent.atomic.AtomicInteger

object CurrentDataUtils {

    private var _baseUrl: String = "http://10.0.2.2:8080/"

    private var _currentProductId: String = ""
    var refreshAttempts = AtomicInteger(0)

    var currentProductId: String
        get() = _currentProductId
        set(newValue){
            _currentProductId = newValue
        }

    var baseUrl: String
        get() = _baseUrl
        set(newValue){
            // Log.d("DEBUG", "${getCurrentStackTrace()} $baseUrl")
            _baseUrl = newValue
        }
}