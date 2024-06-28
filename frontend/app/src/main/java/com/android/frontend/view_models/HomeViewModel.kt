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

    fun fetchUserProfile(context: Context) {
        viewModelScope.launch {
            // get pair tokens
            val tokens = SecurePreferences.getTokens(context)
            Log.d("HomeViewModel", "Access token: ${tokens.first}\nRefresh token: ${tokens.second}")
            val userService: UserService = RetrofitInstance.getUserApi(context)
            val call = userService.me("Bearer ${tokens.first}")
            call.enqueue(object : Callback<UserDTO> {
                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            Log.d("HomeViewModel", "User profile fetched successfully: $responseBody")
                            SecurePreferences.saveUser(context, responseBody)
                        } else {
                            Log.d("HomeViewModel", "User profile response body is null")
                        }
                    } else {
                        Log.e("HomeViewModel", "Failed to fetch user profile: ${response.code()} ${response.message()}")
                        response.errorBody()?.let {
                            Log.e("HomeViewModel", "Error body: ${it.string()}")
                        }
                    }
                }

                override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                    Log.e("HomeViewModel", "Error fetching user profile: ${t.message}", t)
                }
            })
        }
    }
}
