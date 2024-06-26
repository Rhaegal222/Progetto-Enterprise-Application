package com.android.frontend.view_models

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.R
import com.android.frontend.RetrofitInstance
import com.android.frontend.model.CurrentDataUtils
import com.android.frontend.service.UserService
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordViewModel : ViewModel() {
    var oldPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var repeatNewPassword by mutableStateOf("")
    var errorMessage by mutableStateOf(0)

    private val userService: UserService = RetrofitInstance.api

    private fun validateForm(context: Context): Boolean {
        return when {
            oldPassword.isEmpty() || newPassword.isEmpty() || repeatNewPassword.isEmpty() -> {
                errorMessage = R.string.miss_fields
                false
            }
            newPassword != repeatNewPassword -> {
                errorMessage = R.string.passwordMissmatch
                false
            }
            newPassword.length < 8 -> {
                errorMessage = R.string.passwordTooShort
                false
            }
            !newPassword.any { it.isUpperCase() } -> {
                errorMessage = R.string.password1UpChar
                false
            }
            !newPassword.any { it.isDigit() } -> {
                errorMessage = R.string.password1Numb
                false
            }
            !newPassword.any { it in "!@#\$%^&*()_+-=<>?" } -> {
                errorMessage = R.string.password1SpecChar
                false
            }
            else -> {
                errorMessage = 0
                true
            }
        }
    }

    fun changePassword(context: Context): Boolean {
        return if (validateForm(context)) {
            viewModelScope.launch(Dispatchers.IO) {
                val accTok = CurrentDataUtils.accessToken
                if (accTok.isNotEmpty()) {
                    val token = if (accTok.startsWith("Bearer ")) accTok else "Bearer $accTok"
                    println("$token $oldPassword $newPassword")
                    val call = userService.changePassword(token, oldPassword, newPassword)
                    call.enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                errorMessage = R.string.passwordChangedSucc
                            } else {
                                errorMessage = R.string.unknown_error
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            errorMessage = R.string.connection_error
                        }
                    })
                }
            }
            true
        } else {
            false
        }
    }
}
