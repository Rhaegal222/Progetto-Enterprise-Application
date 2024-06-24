package com.example.frontend

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.frontend.navigation.NavGraph
import com.example.frontend.ui.theme.FrontendTheme
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException

class MainActivity : ComponentActivity() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        oneTapClient = Identity.getSignInClient(this)

        setContent {
            FrontendTheme {
                navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }

    fun startSignIn() {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false) // True se vuoi filtrare per account già autorizzati
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                } catch (e: Exception) {
                    Toast.makeText(this, "Errore di inizio autenticazione: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    Log.d("MainActivity", "Errore di inizio autenticazione: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                Toast.makeText(this, "Errore di inizio autenticazione: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                Log.d("MainActivity", "Errore di inizio autenticazione: ${e.localizedMessage}")
            }
    }
}
