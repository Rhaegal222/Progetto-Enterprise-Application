package com.android.frontend.view_models.user

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.R
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.TokenManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ChangePasswordViewModel : ViewModel() {
    var oldPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var repeatNewPassword by mutableStateOf("")
    var errorMessage by mutableStateOf(0)
    var successMessage by mutableStateOf(0)

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

    fun changePassword(context: Context, onComplete: (Boolean) -> Unit) {
        if (validateForm(context)) {
            viewModelScope.launch(Dispatchers.IO) {
                val success = changePasswordSuspend(context)
                withContext(Dispatchers.Main) {
                    onComplete(success)
                }
            }
        } else {
            onComplete(false)
        }
    }

    private suspend fun changePasswordSuspend(context: Context): Boolean = suspendCoroutine { continuation ->
        val accTok = TokenManager.getInstance().getAccessToken(context)
        if (accTok != null) {
            if (accTok.isNotEmpty()) {
                val token = if (accTok.startsWith("Bearer ")) accTok else "Bearer $accTok"
                val userService = RetrofitInstance.getUserApi(context)
                val call = userService.changePassword(token, oldPassword, newPassword)
                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            successMessage = R.string.passwordChangedSucc
                            errorMessage = 0
                            continuation.resume(true)
                        } else {
                            errorMessage = when (response.code()) {
                                500 -> R.string.invalid_old_password
                                else -> R.string.unknown_error
                            }
                            successMessage = 0
                            continuation.resume(false)
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        errorMessage = R.string.connection_error
                        successMessage = 0
                        continuation.resume(false)
                    }
                })
            } else {
                continuation.resume(false)
            }
        }
    }
}
