package com.android.frontend.controller.infrastructure

fun getCurrentStackTrace(): String {
    val stackTrace = Throwable().stackTrace
    val element = stackTrace[1]
    return "${element.fileName}:${element.lineNumber}"
}