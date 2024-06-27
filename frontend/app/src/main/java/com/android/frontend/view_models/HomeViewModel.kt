package com.android.frontend.view_models

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.UserDTO
import com.android.frontend.model.SecurePreferences
import com.android.frontend.service.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

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
                            SecurePreferences.saveUser(getApplication(), user)
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
