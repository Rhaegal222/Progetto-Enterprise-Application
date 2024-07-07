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
            _isLoading.postValue(true)
            _hasError.postValue(false)
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                _isLoading.postValue(false)
                _hasError.postValue(true)
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
                _hasError.postValue(true)
            }
            _isLoading.postValue(false)
        }
    }

    fun deleteUser(userId: String, context: Context) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            _hasError.postValue(false)
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                _isLoading.postValue(false)
                _hasError.postValue(true)
                return@launch
            }

            val userService = RetrofitInstance.getUserApi(context)
            val response = Request().executeRequest(context) {
                userService.deleteUser("Bearer $accessToken", userId)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} User deleted successfully with id: $userId")
                fetchAllUsers(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to delete user: ${response?.errorBody()?.string()}")
                _hasError.postValue(true)
            }
            _isLoading.postValue(false)
        }
    }

    fun changeUserRole(userId: String, role: String, context: Context) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            _hasError.postValue(false)
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                _isLoading.postValue(false)
                _hasError.postValue(true)
                return@launch
            }

            val userService = RetrofitInstance.getUserApi(context)
            val response = Request().executeRequest(context) {
                userService.changeRole("Bearer $accessToken", userId, role)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} User role changed successfully with id: $userId")
                fetchAllUsers(context)
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to change user role: ${response?.errorBody()?.string()}")
                _hasError.postValue(true)
            }
            _isLoading.postValue(false)
        }
    }

    private suspend fun saveImageToFile(context: Context, responseBody: ResponseBody): File {
        return withContext(Dispatchers.IO) {
            val tempFile = File.createTempFile("profile", "png", context.cacheDir)
            val inputStream = responseBody.byteStream()
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            tempFile
        }
    }

    private suspend fun getPhotoProfileById(context: Context, userId: String): ResponseBody? {
        return withContext(Dispatchers.IO) {
            _isLoading.postValue(true)
            _hasError.postValue(false)
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            if (accessToken == null) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                _isLoading.postValue(false)
                _hasError.postValue(true)
                return@withContext null
            }
            val userImageService = RetrofitInstance.getUserImageApi(context)
            val response = Request().executeRequest(context) {
                userImageService.getPhotoProfileById("Bearer $accessToken", userId)
            }
            if (response?.isSuccessful == true) {
                Log.d("DEBUG", "${getCurrentStackTrace()} Fetched image")
                response.body()
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching image: ${response?.errorBody()?.string()}")
                _isLoading.postValue(false)
                _hasError.postValue(true)
                null
            }
        }
    }

    private fun fetchUserProfileImage(context: Context, userId: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            _hasError.postValue(false)
            try {
                val responseBody = getPhotoProfileById(context, userId)
                responseBody?.let {
                    val tempFile = saveImageToFile(context, responseBody)
                    val imageUri = Uri.fromFile(tempFile)
                    val currentImages = _userImagesLiveData.value?.toMutableMap() ?: mutableMapOf()
                    currentImages[userId] = imageUri
                    _userImagesLiveData.postValue(currentImages)
                } ?: run {
                    Log.e("DEBUG", "${getCurrentStackTrace()}, Image retrieval failed")
                    _hasError.postValue(true)  // Use postValue instead of setValue
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Image retrieval error: ${e.message}")
                _hasError.postValue(true)  // Use postValue instead of setValue
            } finally {
                _isLoading.postValue(false)  // Use postValue instead of setValue
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
