package com.android.frontend.view_models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.model.GoogleAuthentication
import com.android.frontend.model.SecurePreferences
import com.android.frontend.service.UserService
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

    private fun validateForm(): Boolean {
        return username.isNotEmpty() && password.isNotEmpty()
    }

    fun loginUser(context: Context, onResult: (Boolean, String?) -> Unit) {
        if (validateForm()) {
            viewModelScope.launch {
                val userService: UserService = RetrofitInstance.getUserApi(context)
                val call = userService.login(username, password)
                call.enqueue(object : Callback<Map<String, String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        if (response.isSuccessful) {
                            val tokenMap = response.body()!!
                            accessToken = tokenMap["accessToken"].toString()
                            refreshToken = tokenMap["refreshToken"].toString()
                            SecurePreferences.saveAccessToken(context, accessToken)
                            SecurePreferences.saveRefreshToken(context, refreshToken)
                            SecurePreferences.saveProvider(context, "local")
                            onResult(true, null)
                            refreshAccessToken(context)
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

    fun refreshAccessToken(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val storedRefreshToken = SecurePreferences.getRefreshToken(context)
            if (!storedRefreshToken.isNullOrEmpty()) {
                val token = if (storedRefreshToken.startsWith("Bearer ")) storedRefreshToken else "Bearer $storedRefreshToken"
                val userService: UserService = RetrofitInstance.getUserApi(context)
                val call = userService.refreshToken(token)
                call.enqueue(object : Callback<Map<String, String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        if (response.isSuccessful) {
                            val tokenMap = response.body()!!
                            accessToken = tokenMap["accessToken"].toString()
                            refreshToken = tokenMap["refreshToken"].toString()
                            SecurePreferences.saveAccessToken(context, accessToken)
                            SecurePreferences.saveRefreshToken(context, refreshToken)
                        } else {
                            Log.e("LoginViewModel", "Failed to refresh access token: ${response.code()}")
                        }
                    }
                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        Log.e("LoginViewModel", "Failed to refresh access token", t)
                    }
                })
            }
        }
    }

    fun signInWithGoogle(context: Context, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val googleAuth = GoogleAuthentication(context)
            googleAuth.signIn { success, error ->
                if (success != null) {
                    accessToken = success["accessToken"].toString()
                    refreshToken = success["refreshToken"].toString()
                    SecurePreferences.saveAccessToken(context, accessToken)
                    SecurePreferences.saveRefreshToken(context, refreshToken)
                    SecurePreferences.saveProvider(context, "google")
                    onResult(true, null)
                } else {
                    onResult(false, error)
                }
            }
        }
    }
}
