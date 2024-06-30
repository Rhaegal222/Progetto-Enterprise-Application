package com.android.frontend.persistence

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