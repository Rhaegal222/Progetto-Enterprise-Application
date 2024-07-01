package com.android.frontend.view_models

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.basic.UserBasicDTO
import com.android.frontend.dto.UserDTO
import com.android.frontend.persistence.SecurePreferences
import com.android.frontend.dto.UserUpdateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val _user = MutableLiveData<UserBasicDTO>()
    val userLiveData: LiveData<UserBasicDTO> get() = _user

    private val _profile = MutableLiveData<UserDTO>()
    val profileLiveData: LiveData<UserDTO> get() = _profile

    val profileImage = MutableLiveData<Uri?>(null)
    val profileImageLiveData : LiveData<Uri?> get() = profileImage

    var isLoading = mutableStateOf(true)

    fun fetchData(context: Context) {
        viewModelScope.launch {
            isLoading.value = true
            getUserDTO(context)
            fetchUserProfileImage(context)
            isLoading.value = false
        }
    }

    private suspend fun getUserBasicDTO(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val accessToken = TokenManager.getInstance().getAccessToken(context)
                val userService = RetrofitInstance.getUserApi(context)
                val call = userService.userBasicDTOCall("Bearer $accessToken")
                val response = call.execute()

                if (response.isSuccessful) {
                    response.body()?.let {
                        _user.postValue(it)
                    }
                } else {
                    Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch user profile: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching user profile", e)
            }
        }
    }

    private suspend fun getUserDTO(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val accessToken = TokenManager.getInstance().getAccessToken(context)
                val userService = RetrofitInstance.getUserApi(context)
                val call = userService.userDTOCall("Bearer $accessToken")
                val response = call.execute()

                if (response.isSuccessful) {
                    response.body()?.let {
                        _profile.postValue(it)
                    }
                } else {
                    Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch user profile: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching user profile", e)
            }
        }
    }



    fun updateUserProfile(context: Context, firstName: String, lastName: String, email: String, phoneNumber: String) {
        viewModelScope.launch {
            val accessToken = TokenManager.getInstance().getAccessToken(context)

            val updateRequest = UserUpdateRequest(
                firstName = firstName,
                lastName = lastName,
                email = email,
                phoneNumber = phoneNumber,
                username = email
            )

            if (_user.value?.id == "-1" || _user.value?.id == null || _user.value?.id == "" || _user.value?.id == "null") {
                Log.e("DEBUG", "${getCurrentStackTrace()},User ID is not set")
                return@launch
            }
            val userService = RetrofitInstance.getUserApi(context)
            val call = userService.updateUser("Bearer $accessToken", _user.value?.id ?: "", updateRequest)
            call.enqueue(object : Callback<UserDTO> {
                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                    if (response.isSuccessful) {
                        viewModelScope.launch {
                            getUserBasicDTO(context)
                        }
                    } else {
                        Log.e("DEBUG", "${getCurrentStackTrace()},Failed to update user profile: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                    Log.e("DEBUG", "${getCurrentStackTrace()},Error updating user profile", t)
                }
            })
        }
    }

    fun logout(context: Context) {
        TokenManager.getInstance().clearTokens(context)
        val packageManager: PackageManager = context.packageManager
        val intent: Intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
        val componentName: ComponentName = intent.component!!
        val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(restartIntent)
        Runtime.getRuntime().exit(0)
    }

    fun updatePhotoUser(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            val photoProfileId = SecurePreferences.getUser(getApplication())?.photoProfile?.id ?: ""
            if (photoProfileId.isNotEmpty()) {
                deletePhotoUser(context)
            }
            uploadImage(context, imageUri)
        }
    }

    private suspend fun uploadImage(context: Context, imageUri: Uri) {
        withContext(Dispatchers.IO) {
            val file = getFileFromUri(context, imageUri) ?: return@withContext

            val requestFile = file
                .asRequestBody("image/*".toMediaTypeOrNull())

            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val description = "Profile Image".toRequestBody("text/plain".toMediaTypeOrNull())

            try {
                val accessToken = TokenManager.getInstance().getAccessToken(context)
                val userImageService = RetrofitInstance.getUserImageApi(context)
                val call = userImageService.savePhotoUser("Bearer $accessToken", body, description)
                val response = call.execute()

                if (response.isSuccessful) {
                    response.body()?.let { userImageDTO ->
                        profileImage.postValue(Uri.parse(userImageDTO.urlPhoto))
                        SecurePreferences.saveUser(context, _user.value!!.copy(photoProfile = userImageDTO))
                        getUserBasicDTO(context) // Assuming this is a suspend function
                    }
                } else {
                    Log.e("DEBUG", "${getCurrentStackTrace()},Image upload failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()},Image upload error", e)
            }
        }
    }

    private fun deletePhotoUser(context: Context) {
        val photoProfileId = SecurePreferences.getUser(context)?.photoProfile?.id ?: ""
        val accessToken = TokenManager.getInstance().getAccessToken(context)

        val userImageService = RetrofitInstance.getUserImageApi(context)
        val call = userImageService.deletePhotoUser("Bearer $accessToken", photoProfileId)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    Log.e("DEBUG", "${getCurrentStackTrace()},Image delete failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("DEBUG", "${getCurrentStackTrace()},Image delete error: ${t.message}")
            }
        })
    }

    private suspend fun fetchUserProfileImage(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val type = "user_photos"
                val folderName = SecurePreferences.getUser(context)?.id ?: ""
                val fileName = "photoProfile.png"
                val responseBody = fetchImage(type, folderName, fileName)
                responseBody?.let {
                    val tempFile = saveImageToFile(context, responseBody)
                    profileImage.postValue(Uri.fromFile(tempFile))
                } ?: run {
                    Log.e("DEBUG", "${getCurrentStackTrace()},Image retrieval failed")
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()},Image retrieval error: ${e.message}")
            }
        }
    }

    private suspend fun fetchImage(type: String, folderName: String, fileName: String): ResponseBody? {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val userImageService = RetrofitInstance.getUserImageApi(getApplication())
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

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val fileName = "photoProfile.png"
        val tempFile = File(context.cacheDir, fileName)
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return tempFile
    }
}
