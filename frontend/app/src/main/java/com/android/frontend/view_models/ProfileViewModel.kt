package com.android.frontend.view_models

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.UserDTO
import com.android.frontend.model.CurrentDataUtils
import com.android.frontend.service.UserService
import com.android.frontend.R
import com.android.frontend.controller.models.UserUpdateRequest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    var firstName = mutableStateOf("")
    var lastName = mutableStateOf("")
    var email = mutableStateOf("")
    var phoneNumber = mutableStateOf("")
    var profileImage = mutableStateOf(R.drawable.user_image) // Default image

    private val userService: UserService = RetrofitInstance.api

    fun fetchUserProfile() {
        viewModelScope.launch {
            val accessToken = CurrentDataUtils.accessToken
            Log.d("ProfileViewModel", "AccessToken: $accessToken") // Log access token
            val call = userService.me("Bearer $accessToken")
            call.enqueue(object : Callback<UserDTO> {
                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            Log.d("ProfileViewModel", "User profile fetched successfully: $user") // Log user data
                            CurrentDataUtils.userId = user.id
                            firstName.value = user.firstName
                            lastName.value = user.lastName
                            email.value = user.email
                            phoneNumber.value = user.phoneNumber ?: "Nessun numero di telefono"
                            // Set profileImage.value if user has a profile image URL or resource ID
                        } ?: run {
                            Log.d("ProfileViewModel", "User profile response body is null")
                        }
                    } else {
                        Log.e("ProfileViewModel", "Failed to fetch user profile: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                    Log.e("ProfileViewModel", "Error fetching user profile", t)
                }
            })
        }
    }

    fun updateUserProfile(firstName: String, lastName: String, email: String, phoneNumber: String) {
        viewModelScope.launch {
            val accessToken = CurrentDataUtils.accessToken
            val userId = CurrentDataUtils.userId
            val updateRequest = UserUpdateRequest(
                firstName = firstName,
                lastName = lastName,
                email = email,
                phoneNumber = phoneNumber,
                username = email
            )

            val call = userService.updateUser("Bearer $accessToken", userId, updateRequest)
            call.enqueue(object : Callback<UserDTO> {
                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            Log.d("ProfileViewModel", "User profile updated successfully: $user")
                            this@ProfileViewModel.firstName.value = user.firstName
                            this@ProfileViewModel.lastName.value = user.lastName
                            this@ProfileViewModel.email.value = user.email
                            this@ProfileViewModel.phoneNumber.value = user.phoneNumber ?: "Nessun numero di telefono"
                            // Update profileImage.value if user has a profile image URL or resource ID
                        } ?: run {
                            Log.d("ProfileViewModel", "User profile update response body is null")
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

}
