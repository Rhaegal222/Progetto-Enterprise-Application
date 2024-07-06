package com.android.frontend.config

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.annotation.StringRes
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.android.frontend.R
import com.android.frontend.RetrofitInstance
import com.android.frontend.persistence.SecurePreferences
import com.android.frontend.service.GoogleAuthenticationService
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class GoogleAuthentication(private val context: Context) {

    private lateinit var request: GetCredentialRequest
    private var filterAuthorizedAccounts = true
    private val webClientId = context.getString(R.string.web_client_id)
    private val credentialManager = CredentialManager.create(context)
    private val googleAuthService: GoogleAuthenticationService = RetrofitInstance.getGoogleAuthApi(context)

    private var accessToken = TokenManager.getInstance().getAccessToken(context) ?: ""
    private var refreshToken = TokenManager.getInstance().getRefreshToken(context) ?: ""
    private var provider = SecurePreferences.getProvider(context) ?: ""

    suspend fun signIn(onResult: (Map<String, String>?, String?) -> Unit) {
        request = createRequest()
        try {
            val result = credentialManager.getCredential(context = context, request = request)
            handleSignIn(result, onResult)
        } catch (e: GetCredentialException) {
            withContext(Dispatchers.Main) {
                handleFailure(e)
                onResult(null, e.message)
            }
        }
    }

    private fun getNonce(): String {
        val nonceLength = 32
        val nonceBytes = ByteArray(nonceLength)
        Random.nextBytes(nonceBytes)
        return nonceBytes.joinToString("") { "%02X".format(it) }
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

    @SuppressLint("QueryPermissionsNeeded")
    private fun promptAddGoogleAccount() {
        Log.d("DEBUG", "${getCurrentStackTrace()} Prompting user to add Google account")
        try {
            val intent = Intent(Settings.ACTION_ADD_ACCOUNT).apply {
                putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Log.e("ERROR", "${getCurrentStackTrace()} No activity found to handle Google account addition")
                showErrorDialog(R.string.no_google_account_activity_error)
            }
        } catch (e: Exception) {
            Log.e("ERROR", "${getCurrentStackTrace()} Failed to prompt for Google account addition", e)
            showErrorDialog(R.string.google_account_prompt_error)
        }
    }

    private fun showErrorDialog(@StringRes messageResId: Int) {
        AlertDialog.Builder(context)
            .setTitle(R.string.error_dialog_title)
            .setMessage(messageResId)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    private fun userAlreadySignedIn(): Boolean {
        Log.d("DEBUG", "${getCurrentStackTrace()} Checking if user is already signed in")
        return accessToken.isNotEmpty() && refreshToken.isNotEmpty() && provider == "google"
    }

    private fun createRequest(): GetCredentialRequest {
        Log.d("DEBUG", "${getCurrentStackTrace()} Creating request")
        return try {
            if (userAlreadySignedIn()) {
                Log.d("DEBUG", "${getCurrentStackTrace()} User is already signed in")
                GetCredentialRequest.Builder().addCredentialOption(getGoogleIdOption()).build()
            } else {
                Log.d("DEBUG", "${getCurrentStackTrace()} User is not signed in")
                GetCredentialRequest.Builder().addCredentialOption(getSignInGoogleOption()).build()
            }
        } catch (e: GetCredentialException) {
            Log.e("DEBUG", "${getCurrentStackTrace()} Error creating request: ${e.message}")
            TokenManager.getInstance().clearTokens(context)
            promptAddGoogleAccount()
            GetCredentialRequest.Builder().build()
        }
    }

    private fun handleFailure(e: GetCredentialException) {
        Log.e("DEBUG", "${getCurrentStackTrace()} Error getting credential: ${e.message}")
        TokenManager.getInstance().clearTokens(context)
    }

    private suspend fun handleSignIn(result: GetCredentialResponse, onResult: (Map<String, String>?, String?) -> Unit) {
        Log.d("DEBUG", "${getCurrentStackTrace()} Sign-in flow completed")
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        sendGoogleIdTokenToBackend(idToken) { success ->
                            if (success != null) {
                                Log.d("DEBUG", "${getCurrentStackTrace()} Successfully sent google id token to backend")
                                onResult(success, null)
                            } else {
                                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to send google id token to backend: null response")
                                onResult(null, context.getString(R.string.server_communication_error))
                            }
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Received an invalid google id token response", e)
                        withContext(Dispatchers.Main) {
                            onResult(null, context.getString(R.string.google_auth_communication_error))
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onResult(null, context.getString(R.string.google_auth_communication_error))
                    }
                }
            }
            else -> {
                withContext(Dispatchers.Main) {
                    Log.e("DEBUG", "${getCurrentStackTrace()} Received an invalid credential type")
                    onResult(null, context.getString(R.string.google_auth_communication_error))
                }
            }
        }
    }

    private fun sendGoogleIdTokenToBackend(idToken: String, onResult: (Map<String, String>?) -> Unit) {
        val call = googleAuthService.googleAuth(idToken)
        call.enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    val tokenMap = response.body()!!
                    Log.d("DEBUG", "${getCurrentStackTrace()} Map received: $tokenMap")
                    onResult(tokenMap)
                    refreshAccessToken()
                } else {
                    Log.e("DEBUG", "${getCurrentStackTrace()} Failed to send google id token to backend: ${response.code()}")
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Failed to send google id token to backend", t)
                onResult(null)
            }
        })
    }

    private fun refreshAccessToken() {
        if (refreshToken.isNotEmpty()) {
            val token = if (refreshToken.startsWith("Bearer ")) refreshToken else "Bearer $refreshToken"
            val call = googleAuthService.refreshToken(token)
            call.enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                    if (response.isSuccessful) {
                        val tokenMap = response.body()!!
                        accessToken = tokenMap["accessToken"].toString()
                        refreshToken = tokenMap["refreshToken"].toString()
                    } else {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Failed to refresh access token: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    Log.e("DEBUG", "${getCurrentStackTrace()} Failed to refresh access token", t)
                }
            })
        }
    }
}