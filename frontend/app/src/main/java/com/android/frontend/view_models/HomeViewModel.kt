package com.android.frontend.view_models

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
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

    fun fetchUserProfile(context : Context) {
        viewModelScope.launch {
            val accessToken = SecurePreferences.getAccessToken(context)
            Log.d("RootScreen", "Access token: $accessToken")
            val call = userService.me("Bearer $accessToken")
            call.enqueue(object : Callback<UserDTO> {
                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            SecurePreferences.saveUser(context, user)
                        } ?: run {
                            Log.d("HomeViewModel", "User profile response body is null")
                        }
                    } else {
                        Log.e("HomeViewModel", "Failed to fetch user profile: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                    Log.e("HomeViewModel", "Error fetching user profile", t)
                }
            })
        }
    }
}
