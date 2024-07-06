package com.android.frontend.persistence

import android.net.Uri
import android.util.Log
import com.android.frontend.config.getCurrentStackTrace
import java.util.concurrent.atomic.AtomicInteger

object CurrentDataUtils {

    private var _baseUrl: String = "http://192.168.42.137:8080/"

    private var _currentProductId: Long = 0
    private var _currentProductImageUri: Uri? = null
    private var _currentWishlistId: String = ""
    private var _currentWishlistName: String = ""
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

    var currentWishlistId: String
        get() = _currentWishlistId
        set(newValue){
            _currentWishlistId = newValue
        }

    var CurrentWishlistName: String
        get() = _currentWishlistName
        set(newValue){
            _currentWishlistName = newValue
        }

    var baseUrl: String
        get() = _baseUrl
        set(newValue){
            Log.d("DEBUG", "${getCurrentStackTrace()} $baseUrl")
            _baseUrl = newValue
        }
}