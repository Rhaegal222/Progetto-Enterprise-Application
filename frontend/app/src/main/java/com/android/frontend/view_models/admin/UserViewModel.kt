package com.android.frontend.view_models.admin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.MainActivity
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.Request
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.UserDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream

class UserViewModel : ViewModel() {
    private val _usersLiveData = MutableLiveData<List<UserDTO>>()
    val usersLiveData: LiveData<List<UserDTO>> = _usersLiveData

    // Map to store user images
    private val _userImagesLiveData = MutableLiveData<Map<String, Uri>>()
    val userImagesLiveData: LiveData<Map<String, Uri>> = _userImagesLiveData

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError

    fun fetchAllUsers(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }
            val userService = RetrofitInstance.getUserApi(context)
            val response = Request().executeRequest(context) {
                userService.getAllUsers("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Fetched all users")
                val users = response.body() ?: emptyList()
                _usersLiveData.postValue(users)
                users.forEach { user ->
                    fetchUserProfileImage(context, user.id)
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch all users: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }


    fun deleteUser(userId: String, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }

            val userService = RetrofitInstance.getUserApi(context)
            val response = Request().executeRequest(context) {
                userService.deleteUser("Bearer $accessToken", userId)
            }
            if (response?.isSuccessful ==  true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} User deleted successfully with id: $userId")
                fetchAllUsers(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to delete user: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }


    fun changeUserRole(userId: String, role: String, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                _isLoading.value = false
                _hasError.value = true
                return@launch
            }

            val userService = RetrofitInstance.getUserApi(context)
            val response = Request().executeRequest(context){
                userService.changeRole("Bearer $accessToken", userId, role)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} User role changed successfully with id: $userId")
                fetchAllUsers(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to change user role: ${response?.errorBody()?.string()}")
                _hasError.value = true
            }
            _isLoading.value = false
        }
    }


    private suspend fun fetchImage(context: Context, type: String, folderName: String, fileName: String): ResponseBody? {
        return withContext(Dispatchers.IO) {
            val userImageService = RetrofitInstance.getUserImageApi(context)
            val response = Request().executeRequest(context) {
                userImageService.getImage(type, folderName, fileName)
            }
            if (response?.isSuccessful == true) {
                response.body()
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching image: ${response?.errorBody()?.string()}")
                null
            }
        }
    }

    private suspend fun saveImageToFile(context: Context, responseBody: ResponseBody): File {
        return withContext(Dispatchers.IO) {
            val tempFile = File.createTempFile("profile", "jpg", context.cacheDir)
            val inputStream = responseBody.byteStream()
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            tempFile
        }
    }

    private fun fetchUserProfileImage(context: Context, userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasError.value = false

            try {
                val type = "user_photos"
                val fileName = "photoProfile.png"
                val responseBody = fetchImage(context, type, userId, fileName)
                responseBody?.let {
                    val tempFile = saveImageToFile(context, responseBody)
                    val imageUri = Uri.fromFile(tempFile)
                    val currentImages = _userImagesLiveData.value?.toMutableMap() ?: mutableMapOf()
                    currentImages[userId] = imageUri
                    _userImagesLiveData.postValue(currentImages)
                } ?: run {
                    Log.e("DEBUG", "${getCurrentStackTrace()}, Image retrieval failed")
                    _hasError.value = true
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Image retrieval error: ${e.message}")
                _hasError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout(context: Context) {
        TokenManager
            .getInstance()
            .clearTokens(context)
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }
}
