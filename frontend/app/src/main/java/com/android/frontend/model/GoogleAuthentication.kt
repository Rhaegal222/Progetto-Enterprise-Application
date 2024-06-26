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
import android.content.Intent
import android.provider.Settings

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

    private fun userAlreadySignedIn(): Boolean {
        return accessToken.isNotEmpty() && refreshToken.isNotEmpty()
    }

    private fun promptAddGoogleAccount() {
        val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        context.startActivity(intent)
    }
    private fun createRequest(): GetCredentialRequest? {
        return try {
            if (userAlreadySignedIn()) {
                Log.d(TAG, "User already signed in")
                GetCredentialRequest.Builder().build()
            } else {
                Log.d(TAG, "User not signed in")
                GetCredentialRequest.Builder().addCredentialOption(getSignInGoogleOption()).build()
            }
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Error creating request: ${e.message}")
            promptAddGoogleAccount()
            return null
        }
    }


    suspend fun signIn(onResult: (Map<String, String>?, String?) -> Unit) {
        request = createRequest() ?: return onResult(null, "Errore durante la creazione della richiesta")
        try {
            val result = credentialManager.getCredential(
                context = context,
                request = request,
            )
            return handleSignIn(result, onResult)
        } catch (e: GetCredentialException) {
            handleFailure(e)
            return onResult(null, e.message)
        }
    }

    private fun handleFailure(e: GetCredentialException) {
        Log.i(TAG, "WebClientId: $webClientId")
        Log.i(TAG, "Origin request: ${request.origin}")
        Log.i(TAG, "Credential options request: ${request.credentialOptions}")
        Log.e(TAG, "Error getting credential: ${e.message}")
    }

    private fun handleSignIn(result: GetCredentialResponse, onResult: (Map<String, String>?, String?) -> Unit) {
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
                            if (success != null) {
                                Log.d(TAG, "Successfully sent google id token to backend")
                                onResult(success, null)
                            } else {
                                Log.e(TAG, "Failed to send google id token to backend: $errorMessage")
                                onResult(null, errorMessage)
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

    private fun sendGoogleIdTokenToBackend(idToken: String, onResult: (Map<String, String>?, String?) -> Unit) {
        val call = googleAuthService.googleAuth(idToken)
        call.enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    val tokenMap = response.body()!!
                    onResult(tokenMap, null)
                    refreshAccessToken()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Errore sconosciuto"
                    onResult(null, errorMessage)
                }
            }
            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                val failureMessage = t.message ?: "Errore sconosciuto"
                onResult(null, failureMessage)
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
