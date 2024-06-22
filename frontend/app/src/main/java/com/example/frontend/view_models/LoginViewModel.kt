package com.example.frontend.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.RetrofitInstance
import com.example.frontend.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var accessToken by mutableStateOf("")
    var refreshToken by mutableStateOf("")

    private val userService: UserService = RetrofitInstance.api

    fun validateForm(): Boolean {
        return username.isNotEmpty() && password.isNotEmpty()
    }

    fun loginUser(onResult: (Boolean, String?) -> Unit) {
        if (validateForm()) {
            viewModelScope.launch {
                val call = userService.login(username, password)
                call.enqueue(object : Callback<Map<String, String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        if (response.isSuccessful) {
                            val tokenMap = response.body()!!
                            accessToken = tokenMap["accessToken"].toString()
                            refreshToken = tokenMap["refreshToken"].toString()
                            onResult(true, null)
                            refreshAccessToken()
                        } else {
                            val errorMessage = response.errorBody()?.string() ?: "Errore sconosciuto"
                            onResult(false, errorMessage)
                        }
                    }

                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        val failureMessage = t.message ?: "Errore sconosciuto"
                        onResult(false, failureMessage)
                    }
                })
            }
        } else {
            onResult(false, "Validazione del modulo fallita")
        }
    }

    fun refreshAccessToken() {
        viewModelScope.launch(Dispatchers.IO) {
            if (refreshToken.isNotEmpty()) {
                val call = userService.refreshToken("Bearer $refreshToken")
                call.enqueue(object : Callback<Map<String, String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        if (response.isSuccessful) {
                            val tokenMap = response.body()!!
                            accessToken = tokenMap["accessToken"].toString()
                            refreshToken = tokenMap["refreshToken"].toString()
                        } else {
                            // Gestisci la risposta di errore se necessario
                        }
                    }

                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        // Gestisci il fallimento se necessario
                    }
                })
            }
        }
    }
}