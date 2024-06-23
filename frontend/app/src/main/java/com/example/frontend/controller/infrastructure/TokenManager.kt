package com.example.frontend.controller.infrastructure

object TokenManager {
    var accessToken: String? = null
    var refreshToken: String? = null

    fun setTokens(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }
}