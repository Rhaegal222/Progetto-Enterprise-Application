package com.android.frontend.view_models.admin

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.UserDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserViewModel : ViewModel() {
    private val _usersLiveData = MutableLiveData<List<UserDTO>>()
    val usersLiveData: LiveData<List<UserDTO>> = _usersLiveData

    // Map to store user images
    private val _userImagesLiveData = MutableLiveData<Map<String, Uri>>()
    val userImagesLiveData: LiveData<Map<String, Uri>> = _userImagesLiveData

    fun fetchAllUsers(context: Context) {
        viewModelScope.launch {
            try {
                val accessToken = TokenManager.getInstance().getAccessToken(context)
                val userService = RetrofitInstance.getUserApi(context)
                val response = userService.getAllUsers("Bearer $accessToken")
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    _usersLiveData.postValue(users)
                    users.forEach { user ->
                        fetchUserProfileImage(context, user.id)
                    }
                } else {
                    Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch all users: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching all users", e)
            }
        }
    }

    fun deleteUser(userId: String, context: Context) {
        viewModelScope.launch {
            try {
                val accessToken = TokenManager.getInstance().getAccessToken(context)
                val userService = RetrofitInstance.getUserApi(context)
                val response = userService.deleteUser("Bearer $accessToken", userId)
                if (response.isSuccessful) {
                    fetchAllUsers(context)
                } else {
                    Log.e("DEBUG", "${getCurrentStackTrace()} Failed to delete user: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error deleting user", e)
            }
        }
    }

    fun changeUserRole(userId: String, role: String, context: Context) {
        viewModelScope.launch {
            try {
                val accessToken = TokenManager.getInstance().getAccessToken(context)
                val userService = RetrofitInstance.getUserApi(context)
                val response = userService.changeRole("Bearer $accessToken", userId, role)
                if (response.isSuccessful) {
                    fetchAllUsers(context)
                } else {
                    Log.e("DEBUG", "${getCurrentStackTrace()} Failed to change user role: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error changing user role", e)
            }
        }
    }

    private suspend fun fetchImage(context: Context, type: String, folderName: String, fileName: String): ResponseBody? {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val userImageService = RetrofitInstance.getUserImageApi(context)
                val call = userImageService.getImage(type, folderName, fileName)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            continuation.resume(response.body())
                        } else {
                            continuation.resume(null)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        continuation.resume(null)
                    }
                })
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
            withContext(Dispatchers.IO) {
                try {
                    val type = "user_photos"
                    val folderName = userId
                    val fileName = "photoProfile.png"
                    val responseBody = fetchImage(context, type, folderName, fileName)
                    responseBody?.let {
                        val tempFile = saveImageToFile(context, responseBody)
                        val imageUri = Uri.fromFile(tempFile)
                        val currentImages = _userImagesLiveData.value?.toMutableMap() ?: mutableMapOf()
                        currentImages[userId] = imageUri
                        _userImagesLiveData.postValue(currentImages)
                    } ?: run {
                        Log.e("DEBUG", "${getCurrentStackTrace()},Image retrieval failed")
                    }
                } catch (e: Exception) {
                    Log.e("DEBUG", "${getCurrentStackTrace()},Image retrieval error: ${e.message}")
                }
            }
        }
    }
}
