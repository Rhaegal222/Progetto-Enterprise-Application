package com.android.frontend.config

fun getCurrentStackTrace(): String {
    return Thread.currentThread().stackTrace.joinToString("\n") { it.toString() }
}