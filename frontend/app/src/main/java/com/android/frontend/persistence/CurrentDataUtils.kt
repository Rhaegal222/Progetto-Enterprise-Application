package com.android.frontend.persistence

import android.net.Uri
import android.util.Log
import com.android.frontend.config.getCurrentStackTrace
import java.util.concurrent.atomic.AtomicInteger

object CurrentDataUtils {

    private var _baseUrl: String = "http://10.0.2.2:8080/"

    private var _currentProductId: Long = 0
    private var _currentProductImageUri: Uri? = null
    private var _currentWishlistId: Long = 0
    var refreshAttempts = AtomicInteger(0)

    var currentProductId: Long
        get() = _currentProductId
        set(newValue){
            _currentProductId = newValue
        }

    var currentProductImageUri: Uri?
        get() = _currentProductImageUri
        set(newValue){
            _currentProductImageUri = newValue
        }

    var currentWishlistId: Long
        get() = _currentWishlistId
        set(newValue){
            _currentWishlistId = newValue
        }

    var baseUrl: String
        get() = _baseUrl
        set(newValue){
            Log.d("DEBUG", "${getCurrentStackTrace()} $baseUrl")
            _baseUrl = newValue
        }
}