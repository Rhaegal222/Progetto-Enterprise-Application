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
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.UserDTO
import com.android.frontend.controller.models.UserImageDTO
import com.android.frontend.model.SecurePreferences
import com.android.frontend.service.UserService
import com.android.frontend.controller.models.UserUpdateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    val photoId = SecurePreferences.getUser(application)?.photoProfile?.id ?: ""

    private var user = mutableStateOf(SecurePreferences.getUser(application))
    var profileImageUri = mutableStateOf<Uri?>(null)
    var isLoading = mutableStateOf(true)

    private var userId = user.value?.id ?: -1
    var firstName = mutableStateOf(user.value?.firstName ?: "")
    var lastName = mutableStateOf(user.value?.lastName ?: "")
    var email = mutableStateOf(user.value?.email ?: "")
    var phoneNumber = mutableStateOf(user.value?.phoneNumber ?: "")

    private val userService: UserService = RetrofitInstance.api
    val userImageService = RetrofitInstance.userImageApi

    fun fetchUserProfile() {
        viewModelScope.launch {
            isLoading.value = true
            val accessToken = SecurePreferences.getAccessToken(getApplication())
            val call = userService.me("Bearer $accessToken")
            call.enqueue(object : Callback<UserDTO> {
                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            SecurePreferences.saveUser(getApplication(), user)
                            firstName.value = user.firstName
                            lastName.value = user.lastName
                            email.value = user.email
                            phoneNumber.value = user.phoneNumber ?: ""
                        } ?: run {
                            Log.d("ProfileViewModel", "User profile response body is null")
                        }
                    } else {
                        Log.e("ProfileViewModel", "Failed to fetch user profile: ${response.errorBody()?.string()}")
                    }
                    isLoading.value = false
                }

                override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                    Log.e("ProfileViewModel", "Error fetching user profile", t)
                    isLoading.value = false
                }
            })
        }
    }

    fun updateUserProfile(firstName: String, lastName: String, email: String, phoneNumber: String) {
        viewModelScope.launch {
            val accessToken = SecurePreferences.getAccessToken(getApplication())
            // val userId = CurrentDataUtils.userId
            val updateRequest = UserUpdateRequest(
                firstName = firstName,
                lastName = lastName,
                email = email,
                phoneNumber = phoneNumber,
                username = email
            )

            if (userId == -1) {
                Log.e("ProfileViewModel", "User ID is not set")
                return@launch
            }
            val call = userService.updateUser("Bearer $accessToken", userId.toString(), updateRequest)
            call.enqueue(object : Callback<UserDTO> {
                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            this@ProfileViewModel.firstName.value = user.firstName
                            this@ProfileViewModel.lastName.value = user.lastName
                            this@ProfileViewModel.email.value = user.email
                            this@ProfileViewModel.phoneNumber.value = user.phoneNumber ?: ""
                            // Update profileImage.value if user has a profile image URL or resource ID
                        } ?: run {
                            Log.e("ProfileViewModel", "User profile update response body is null")
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

    fun logout(context: Context) {
        SecurePreferences.clearAll(context)
        val packageManager: PackageManager = context.packageManager
        val intent: Intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
        val componentName: ComponentName = intent.component!!
        val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(restartIntent)
        Runtime
            .getRuntime()
            .exit(0)
    }

    fun updatePhotoUser(imageUri: Uri) {
        viewModelScope.launch {
            val photoProfileId = SecurePreferences.getUser(getApplication())?.photoProfile?.id ?: ""
            if (photoProfileId.isNotEmpty()) {
                deletePhotoUser()
            }
            uploadImage(imageUri)
        }
    }

    private fun uploadImage(imageUri: Uri) {
        val file = getFileFromUri(getApplication(), imageUri) ?: return

        val requestFile = RequestBody.create(
            "image/*".toMediaTypeOrNull(),
            file
        )

        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val description = RequestBody.create("text/plain".toMediaTypeOrNull(), "Profile Image")

        val accessToken = SecurePreferences.getAccessToken(getApplication())
        val call = userImageService.savePhotoUser("Bearer $accessToken", body, description)

        call.enqueue(object : Callback<UserImageDTO> {
            override fun onResponse(call: Call<UserImageDTO>, response: Response<UserImageDTO>) {
                if (response.isSuccessful) {
                    response.body()?.let { userImageDTO ->
                        profileImageUri.value = Uri.parse(userImageDTO.urlPhoto)
                        SecurePreferences.saveUser(getApplication(), user.value!!.copy(photoProfile = userImageDTO))
                    }
                } else {
                    Log.e("ProfileViewModel", "Image upload failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<UserImageDTO>, t: Throwable) {
                Log.e("ProfileViewModel", "Image upload error: ${t.message}")
            }
        })
    }

    private fun deletePhotoUser() {
        val photoProfileId = SecurePreferences.getUser(getApplication())?.photoProfile?.id ?: ""
        val accessToken = SecurePreferences.getAccessToken(getApplication())

        val call = userImageService.deletePhotoUser("Bearer $accessToken", photoProfileId)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    Log.e("ProfileViewModel", "Image delete failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ProfileViewModel", "Image delete error: ${t.message}")
            }
        })
    }

    fun fetchUserProfileImage() {
        viewModelScope.launch {
            try {
                val type = "user_photos"
                val folderName = SecurePreferences.getUser(getApplication())?.id ?: ""
                val fileName = "photoProfile.png"
                val responseBody = fetchImage(type, folderName, fileName)
                responseBody?.let {
                    val tempFile = saveImageToFile(responseBody)
                    profileImageUri.value = Uri.fromFile(tempFile)
                } ?: run {
                    println("Image retrieval failed")
                }
            } catch (e: Exception) {
                println("Image retrieval error: ${e.message}")
            }
        }
    }

    private suspend fun fetchImage(type: String, folderName: String, fileName: String): ResponseBody? {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
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

    private suspend fun saveImageToFile(responseBody: ResponseBody): File {
        return withContext(Dispatchers.IO) {
            val tempFile = File.createTempFile("profile", "jpg", getApplication<Application>().cacheDir)
            val inputStream = responseBody.byteStream()
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            tempFile
        }
    }


    fun getFileFromUri(context: Context, uri: Uri): File? {
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
