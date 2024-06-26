package com.android.frontend.model

import com.android.frontend.R
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import com.android.frontend.RetrofitInstance
import com.android.frontend.service.GoogleAuthenticationService
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlin.random.Random
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GoogleAuthentication(private val context: Context) {

    private lateinit var request: GetCredentialRequest
    private var filterAuthorizedAccounts = true
    private val webClientId = context.getString(R.string.web_client_id)
    private val credentialManager = CredentialManager.create(context)
    private val googleAuthService: GoogleAuthenticationService = RetrofitInstance.googleAuthApi

    var accessToken by mutableStateOf("")
    var refreshToken by mutableStateOf("")

    private fun getNonce(): String {
        val nonceLength = 32
        val nonceBytes = ByteArray(nonceLength)
        Random.nextBytes(nonceBytes)
        return nonceBytes.joinToString("") { "%02X".format(it) }
    }

    private fun setFilterAuthorizedAccounts(filterAuthorizedAccounts: Boolean) {
        this.filterAuthorizedAccounts = filterAuthorizedAccounts
    }

    private fun getGoogleIdOption(): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(filterAuthorizedAccounts)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(true)
            .setNonce(getNonce())
            .build()
    }

    private fun getSignInGoogleOption(): GetSignInWithGoogleOption {
        return GetSignInWithGoogleOption.Builder(webClientId)
            .setNonce(getNonce())
            .build()
    }

    private fun printRequest(request: GetCredentialRequest) {
        Log.d(TAG, "Request Details:")
        Log.d(TAG, "Credential Options:")
        for (option in request.credentialOptions) {
            Log.d(TAG, option.toString())
        }
        Log.d(TAG, "Origin: ${request.origin ?: "null"}")
        Log.d(TAG, "PreferIdentityDocUI: ${request.preferIdentityDocUi}")
        Log.d(
            TAG,
            "PreferUiBrandingComponentName: ${request.preferUiBrandingComponentName ?: "null"}"
        )
        Log.d(
            TAG,
            "PreferImmediatelyAvailableCredentials: ${request.preferImmediatelyAvailableCredentials}"
        )
    }

    private fun createRequest(button: Boolean): GetCredentialRequest {
        request = if (button) {
            GetCredentialRequest.Builder().addCredentialOption(getGoogleIdOption()).build()
        } else {
            GetCredentialRequest.Builder().addCredentialOption(getSignInGoogleOption()).build()
        }
        printRequest(request)
        return request
    }


    suspend fun signIn(button: Boolean) {
        Log.d(TAG, "Initiating sign-in flow" + if (button) " with GoogleIdOption" else " with SignInWithGoogleOption")
        request = createRequest(button)
        try {
            val result = credentialManager.getCredential(
                context = context,
                request = request,
            )
            handleSignIn(result)
        } catch (e: GetCredentialException) {
            handleFailure(e)
        }
    }

    private fun handleFailure(e: GetCredentialException) {
        Log.i(TAG, "WebClientId: $webClientId")
        Log.i(TAG, "Origin request: ${request.origin}")
        Log.i(TAG, "Credential options request: ${request.credentialOptions}")
        Log.e(TAG, "Error getting credential: ${e.message}")
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        Log.d(TAG, "Sign-in flow completed")

        when (val credential = result.credential) {
            is PublicKeyCredential -> {
                val responseJson = credential.authenticationResponseJson
            }

            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password
            }

            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        sendGoogleIdTokenToBackend(idToken) { success, errorMessage ->
                            if (success) {
                                Log.d(TAG, "Successfully sent google id token to backend")
                            } else {
                                Log.e(TAG, "Failed to send google id token to backend: $errorMessage")
                            }
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun sendGoogleIdTokenToBackend(idToken: String, onResult: (Boolean, String?) -> Unit) {
        val call = googleAuthService.googleAuth(idToken)
        call.enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    val tokenMap = response.body()!!
                    accessToken = tokenMap["accessToken"].toString()
                    refreshToken = tokenMap["refreshToken"].toString()
                    CurrentDataUtils.accessToken = tokenMap["accessToken"].toString()
                    CurrentDataUtils.refreshToken = tokenMap["refreshToken"].toString()
                    onResult(true, null)
                    refreshAccessToken()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Errore sconosciuto"
                    onResult(false, errorMessage)
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                val failureMessage = t.message ?: "Errore sconosciuto"
                onResult(false, failureMessage)
            }
        })
    }

    fun refreshAccessToken() {
        if (refreshToken.isNotEmpty()) {
            val token = if (refreshToken.startsWith("Bearer ")) refreshToken else "Bearer $refreshToken"
            val call = googleAuthService.refreshToken(token)
            call.enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                    if (response.isSuccessful) {
                        val tokenMap = response.body()!!
                        accessToken = tokenMap["accessToken"].toString()
                        refreshToken = tokenMap["refreshToken"].toString()
                        CurrentDataUtils.accessToken = tokenMap["accessToken"].toString()
                        CurrentDataUtils.refreshToken = tokenMap["refreshToken"].toString()
                    } else {

                    }
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {

                }
            })
        }
    }

}
