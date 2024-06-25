package com.example.frontend.model

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.example.frontend.R
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlin.random.Random

class GoogleAuthentication(private val context: Context) {

    private lateinit var request: GetCredentialRequest
    private var filterAuthorizedAccounts = true
    private val webClientId = context.getString(R.string.web_client_id)
    private val credentialManager = CredentialManager.create(context)

    private fun getNonce(): String {
        val nonceLength = 32
        val nonceBytes = ByteArray(nonceLength)
        Random.nextBytes(nonceBytes)
        return nonceBytes.joinToString("") { "%02X".format(it) }
    }

    // Imposta il filtro per gli account autorizzati
    // Gli account autorizzati sono quelli che hanno già effettuato l'accesso all'app con Google
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

    private fun createRequest(button: Boolean): GetCredentialRequest {
        request = if (button) {
            GetCredentialRequest.Builder().addCredentialOption(getGoogleIdOption()).build()
        } else {
            GetCredentialRequest.Builder().addCredentialOption(getSignInGoogleOption()).build()
        }
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

    private fun handleSignIn(result: GetCredentialResponse) {
        Log.d(TAG, "Sign-in flow completed")
        // Handle the successfully returned credential.
        when (val credential = result.credential) {

            // Passkey credential
            is PublicKeyCredential -> {
                // Share responseJson such as a GetCredentialResponse on your server to
                // validate and authenticate
                var responseJson = credential.authenticationResponseJson
            }

            // Password credential
            is PasswordCredential -> {
                // Send ID and password to your server to validate and authenticate.
                val username = credential.id
                val password = credential.password
            }

            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun handleFailure(e: GetCredentialException) {
        Log.i(TAG, "WebClientId: $webClientId")
        Log.i(TAG, "Origin request: ${request.origin}")
        Log.i(TAG, "Credential options request: ${request.credentialOptions}")
        Log.e(TAG, "Error getting credential", e)
    }
}
