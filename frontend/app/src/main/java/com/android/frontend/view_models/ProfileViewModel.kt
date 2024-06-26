package com.android.frontend.view_models

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.R
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.UserDTO
import com.android.frontend.model.CurrentDataUtils
import com.android.frontend.model.SecurePreferences
import com.android.frontend.service.UserService
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
            val accessToken = SecurePreferences.getAccessToken(getApplication())
            Log.d("ProfileViewModel", "AccessToken: $accessToken") // Log access token
            val call = userService.me("Bearer $accessToken")
            call.enqueue(object : Callback<UserDTO> {
                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            Log.d("ProfileViewModel", "User profile fetched successfully: $user") // Log user data
                            firstName.value = user.firstName
                            lastName.value = user.lastName
                            email.value = user.email
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

}
