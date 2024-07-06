package com.android.frontend.view_models.authentication

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.TokenManager
import com.android.frontend.config.GoogleAuthentication
import com.android.frontend.config.Request
import com.android.frontend.persistence.SecurePreferences
import com.android.frontend.service.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var username by mutableStateOf("")
    var password by mutableStateOf("")

    private fun validateForm(): Boolean {
        return username.isNotEmpty() && password.isNotEmpty()
    }

    fun loginUser(context: Context, onResult: (Boolean, String?) -> Unit) {
        if (validateForm()) {
            viewModelScope.launch {
                val userService: UserService = RetrofitInstance.getUserApi(context)
                val response = Request().executeRequest(context) {
                    userService.login(username, password)
                }
                if (response?.isSuccessful == true) {
                    val tokenMap = response.body()
                    if (tokenMap != null) {
                        val accessToken = tokenMap["accessToken"]
                        val refreshToken = tokenMap["refreshToken"]
                        if (accessToken != null && refreshToken != null) {
                            TokenManager.getInstance().saveTokens(context, accessToken, refreshToken)
                            onResult(true, null)
                        } else {
                            onResult(false, "Errore sconosciuto")
                        }
                    } else {
                        onResult(false, "Errore sconosciuto")
                    }
                }
                else {
                    val errorMessage = response?.errorBody()?.string() ?: "Errore sconosciuto"
                    onResult(false, errorMessage)
                }
            }
        } else {
            onResult(false, "Compila tutti i campi")
        }
    }

    fun signInWithGoogle(context: Context, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val googleAuth = GoogleAuthentication(context)
            googleAuth.signIn { success, error ->
                viewModelScope.launch(Dispatchers.Main) {
                    if (success != null) {
                        val accessToken = success["accessToken"]
                        val refreshToken = success["refreshToken"]
                        if (accessToken != null && refreshToken != null) {
                            TokenManager.getInstance().saveTokens(context, accessToken, refreshToken)
                            SecurePreferences.saveProvider(context, "google")
                            onResult(true, null)
                        } else {
                            onResult(false, error)
                        }
                    } else {
                        onResult(false, error)
                    }
                }
            }
        }
    }
}