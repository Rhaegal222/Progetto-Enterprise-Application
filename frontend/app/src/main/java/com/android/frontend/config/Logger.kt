package com.android.frontend.config

fun getCurrentStackTrace(): String {
    val stackTrace = Throwable().stackTrace
    val element = stackTrace[1]
    return "${element.fileName}:${element.lineNumber}"
}