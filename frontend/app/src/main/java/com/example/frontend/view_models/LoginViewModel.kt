package com.example.frontend.view_models

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class LoginViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isPasswordVisible by mutableStateOf(false)

    fun login() {
        // Implement your login logic here
    }

    fun googleSignIn() {
        // Implement your Google Sign-In logic here
    }
}