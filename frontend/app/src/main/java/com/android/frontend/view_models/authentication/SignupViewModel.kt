package com.android.frontend.view_models.authentication

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.Request
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {
    var lastname by mutableStateOf("")
    var firstname by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")


    private fun validateForm(): Boolean {
        return lastname.isNotEmpty() && firstname.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
    }

    fun registerUser(context: Context, onResult: (Boolean, String) -> Unit) {
        if (validateForm()) {
            viewModelScope.launch {
                val userService = RetrofitInstance.getUserApi(context)
                val response = Request().executeRequest(context) {
                    userService.register(lastname, firstname, email, password)
                }
                if (response?.isSuccessful == true) {
                    onResult(true, "Registrazione avvenuta con successo")
                } else {
                    val errorMessage = response?.errorBody()?.string() ?: "Errore sconosciuto"
                    onResult(false, errorMessage)
                    }
            }
        } else {
            onResult(false, "Compila tutti i campi")
        }
    }
}
