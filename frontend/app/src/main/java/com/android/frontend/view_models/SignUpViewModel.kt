package com.android.frontend.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel : ViewModel() {
    var lastname by mutableStateOf("")
    var firstname by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")


    private fun validateForm(): Boolean {
        return lastname.isNotEmpty() && firstname.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
    }

    fun registerUser(onResult: (Boolean, String) -> Unit) {
        if (validateForm()) {
            viewModelScope.launch {
                val call = RetrofitInstance.api.register(firstname, lastname, email, password)
                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            onResult(true, "")
                        } else {
                            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                            println("Error: $errorMessage")
                            onResult(false, errorMessage)
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        val failureMessage = t.message ?: "Unknown failure"
                        println("Failure: $failureMessage")
                        onResult(false, failureMessage)
                    }
                })
            }
        } else {
            onResult(false, "Form validation failed")
        }
    }
}