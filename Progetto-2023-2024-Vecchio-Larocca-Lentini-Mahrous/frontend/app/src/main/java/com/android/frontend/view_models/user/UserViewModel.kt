package com.android.frontend.view_models.user

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.Request
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.ProductDTO
import com.android.frontend.dto.basic.UserBasicDTO
import com.android.frontend.dto.UserDTO
import com.android.frontend.dto.UserUpdateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDetails = MutableLiveData<UserBasicDTO?>(null)
    val userDetailsLiveData: LiveData<UserBasicDTO?> get() = userDetails

    private val profileImage = MutableLiveData<Uri?>(null)
    val profileImageLiveData: LiveData<Uri?> get() = profileImage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _hasError = MutableLiveData(false)
    val hasError: LiveData<Boolean> get() = _hasError

    fun getUserDetails(context: Context) {
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
                userService.userBasicDTOCall("Bearer $accessToken")
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { user ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} User details: $user")
                    userDetails.postValue(user)
                    Log.d("DEBUG", "Fetching profile image: $user")
                    getProfileImage(context)
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch user: ${response?.errorBody()?.string()}")
                _hasError.postValue(true)
            }
            _isLoading.postValue(false)
        }
    }

    private suspend fun getMyPhotoProfile(context: Context): ResponseBody? {
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
                userImageService.getMyPhotoProfile("Bearer $accessToken")
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

    fun getProfileImage(context: Context) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            _hasError.postValue(false)
            try {
                val responseBody = getMyPhotoProfile(context)
                responseBody?.let {
                    val tempFile = saveImageToFile(context, responseBody)
                    profileImage.postValue(Uri.fromFile(tempFile))
                } ?: run {
                    Log.e("DEBUG", "${getCurrentStackTrace()},Image retrieval failed")
                    _hasError.postValue(true)
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()},Image retrieval error: ${e.message}")
                _isLoading.postValue(false)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun updateMe(context: Context, firstName: String, lastName: String, email: String, phoneNumber: String) {
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

            val updateRequest = UserUpdateRequest(
                firstName = firstName,
                lastName = lastName,
                email = email,
                phoneNumber = phoneNumber,
                username = email
            )
            val userService = RetrofitInstance.getUserApi(context)
            val response = Request().executeRequest(context) {
                userService.updateMe("Bearer $accessToken", updateRequest)
            }
            if (response?.isSuccessful == true) {
                response.body()?.let { user ->
                    Log.d("DEBUG", "${getCurrentStackTrace()} User update, details: $user")
                    userDetails.postValue(user)
                    getProfileImage(context)
                    getUserDetails(context)
                }
            } else {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to update user: ${response?.errorBody()?.string()}")
                _hasError.postValue(true)
            }
            _isLoading.postValue(false)
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
            _isLoading.postValue(true)
            _hasError.postValue(false)
            try {
                uploadImage(context, imageUri)
                getUserDetails(context)
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()},Image update error: ${e.message}")
                _hasError.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private suspend fun uploadImage(context: Context, imageUri: Uri) {
        withContext(Dispatchers.IO) {
            val file = getFileFromUri(context, imageUri) ?: return@withContext

            // Prepare the file part
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            _isLoading.postValue(true)
            _hasError.postValue(false)

            try {
                val accessToken = TokenManager.getInstance().getAccessToken(context)
                if (accessToken == null) {
                    Log.e("DEBUG", "${getCurrentStackTrace()} Access token missing")
                    _isLoading.postValue(false)
                    _hasError.postValue(true)
                    return@withContext
                }

                val userImageService = RetrofitInstance.getUserImageApi(context)
                val response = Request().executeRequest(context) {
                    userImageService.replaceMyPhotoProfile("Bearer $accessToken", body)
                }

                if (response?.isSuccessful == true) {
                    response.body()?.let { userImageDTO ->
                        Log.d("DEBUG", "${getCurrentStackTrace()}, Image upload success: $userImageDTO")
                    }
                } else {
                    Log.e("DEBUG", "${getCurrentStackTrace()}, Image upload failed: ${response?.errorBody()?.string()}")
                    _hasError.postValue(true)
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()}, Image upload error: ${e.message}", e)
                _hasError.postValue(true)
            } finally {
                _isLoading.postValue(false)
            }
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

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val tempFile = File.createTempFile("tempImage", null, context.cacheDir)
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            Log.e("DEBUG", "${getCurrentStackTrace()}, Error getting file from URI: ${e.message}")
            null
        }
    }
}
