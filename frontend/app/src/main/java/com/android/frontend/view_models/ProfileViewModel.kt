package com.android.frontend.view_models

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.UserDTO
import com.android.frontend.model.SecurePreferences
import com.android.frontend.service.UserService
import com.android.frontend.controller.models.UserUpdateRequest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private var user = mutableStateOf(SecurePreferences.getUser(application))

    var isLoading = mutableStateOf(true)

    private var userId = user.value?.id ?: -1
    var firstName = mutableStateOf(user.value?.firstName ?: "")
    var lastName = mutableStateOf(user.value?.lastName ?: "")
    var email = mutableStateOf(user.value?.email ?: "")
    var phoneNumber = mutableStateOf(user.value?.phoneNumber ?: "")

    private val userService: UserService = RetrofitInstance.api

    fun fetchUserProfile() {
        viewModelScope.launch {
            isLoading.value = true
            val accessToken = SecurePreferences.getAccessToken(getApplication())
            val call = userService.me("Bearer $accessToken")
            call.enqueue(object : Callback<UserDTO> {
                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            SecurePreferences.saveUser(getApplication(), user)
                            firstName.value = user.firstName
                            lastName.value = user.lastName
                            email.value = user.email
                            phoneNumber.value = user.phoneNumber ?: ""
                        } ?: run {
                            Log.d("ProfileViewModel", "User profile response body is null")
                        }
                    } else {
                        Log.e("ProfileViewModel", "Failed to fetch user profile: ${response.errorBody()?.string()}")
                    }
                    isLoading.value = false
                }

                override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                    Log.e("ProfileViewModel", "Error fetching user profile", t)
                    isLoading.value = false
                }
            })
        }
    }

    fun updateUserProfile(firstName: String, lastName: String, email: String, phoneNumber: String) {
        viewModelScope.launch {
            val accessToken = SecurePreferences.getAccessToken(getApplication())
            // val userId = CurrentDataUtils.userId
            val updateRequest = UserUpdateRequest(
                firstName = firstName,
                lastName = lastName,
                email = email,
                phoneNumber = phoneNumber,
                username = email
            )

            if (userId == -1) {
                Log.e("ProfileViewModel", "User ID is not set")
                return@launch
            }
            val call = userService.updateUser("Bearer $accessToken", userId.toString(), updateRequest)
            call.enqueue(object : Callback<UserDTO> {
                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            this@ProfileViewModel.firstName.value = user.firstName
                            this@ProfileViewModel.lastName.value = user.lastName
                            this@ProfileViewModel.email.value = user.email
                            this@ProfileViewModel.phoneNumber.value = user.phoneNumber ?: ""
                            // Update profileImage.value if user has a profile image URL or resource ID
                        } ?: run {
                            Log.e("ProfileViewModel", "User profile update response body is null")
                        }
                    } else {
                        Log.e("ProfileViewModel", "Failed to update user profile: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                    Log.e("ProfileViewModel", "Error updating user profile", t)
                }
            })
        }
    }

    fun logout(context: Context) {
        SecurePreferences.clearAll(context)
        val packageManager: PackageManager = context.packageManager
        val intent: Intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
        val componentName: ComponentName = intent.component!!
        val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(restartIntent)
        Runtime
            .getRuntime()
            .exit(0)
    }
}
