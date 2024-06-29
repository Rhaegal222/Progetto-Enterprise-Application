package com.android.frontend.view_models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.infrastructure.TokenManager
import com.android.frontend.model.GoogleAuthentication
import com.android.frontend.model.SecurePreferences
import com.android.frontend.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {

    var username by mutableStateOf("")
    var password by mutableStateOf("")
    private var accessToken by mutableStateOf("")
    private var refreshToken by mutableStateOf("")

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
                            val tokenMap = response.body()
                            val accessToken = tokenMap?.get("accessToken")
                            val refreshToken = tokenMap?.get("refreshToken")
                            if (accessToken != null && refreshToken != null) {
                                TokenManager.getInstance().saveTokens(context, accessToken, refreshToken)
                                SecurePreferences.saveProvider(context, "local")
                                onResult(true, null)
                            } else {
                                val errorMessage = response.errorBody()?.string() ?: "Errore sconosciuto"
                                onResult(false, errorMessage)
                            }
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

    fun signInWithGoogle(context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val googleAuth = GoogleAuthentication(context)
            googleAuth.signIn { success ->
                viewModelScope.launch(Dispatchers.Main) {
                    if (success != null) {
                        val accessToken = success["accessToken"]
                        val refreshToken = success["refreshToken"]
                        if (accessToken != null && refreshToken != null) {
                            TokenManager.getInstance().saveTokens(context, accessToken, refreshToken)
                            SecurePreferences.saveProvider(context, "google")
                            onResult(true)
                        } else {
                            onResult(false)
                        }
                    } else {
                        onResult(false)
                    }
                }
            }
        }
    }
}
