package com.example.frontend.view_models

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    fun validateForm(): Boolean {
        // Aggiungi la logica di validazione qui
        return username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
    }

    // Funzione per creare l'utente (simulata)
    fun createUser(user: UserModel) {
        // Implementa la logica per creare l'utente qui
    }
}

// Modello di esempio per l'utente
data class UserModel(val email: String, val password: String, val userName: String, val profilePicture: String)