package com.android.frontend.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.android.frontend.controller.models.UserDTO
import com.google.gson.Gson

object SecurePreferences {

    private const val PREFS_FILENAME = "secure_prefs"
    private const val ACCESS_TOKEN_KEY = "access_token"
    private const val REFRESH_TOKEN_KEY = "refresh_token"
    private const val USER_KEY = "user_data"
    private const val PROVIDER = "user_provider"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        return EncryptedSharedPreferences.create(
            PREFS_FILENAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveAccessToken(context: Context, accessToken: String) {
        Log.d("SecurePreferences", "Saving access token")
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            putString(ACCESS_TOKEN_KEY, accessToken)
            apply()
        }
    }

    fun getAccessToken(context: Context): String? {
        Log.d("SecurePreferences", "Getting access token")
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
    }

    fun saveRefreshToken(context: Context, refreshToken: String) {
        Log.d("SecurePreferences", "Saving refresh token")
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            putString(REFRESH_TOKEN_KEY, refreshToken)
            apply()
        }
    }

    fun getRefreshToken(context: Context): String? {
        Log.d("SecurePreferences", "Getting refresh token")
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null)
    }

    fun saveProvider(context: Context, provider: String) {
        Log.d("SecurePreferences", "Saving provider $provider")
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            putString(PROVIDER, provider)
            apply()
        }
    }

    fun getProvider(context: Context): String? {
        Log.d("SecurePreferences", "Getting provider")
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(PROVIDER, null)
    }

    fun saveUser(context: Context, user: UserDTO) {
        val sharedPreferences = getSharedPreferences(context)
        val userJson = Gson().toJson(user)
        with(sharedPreferences.edit()) {
            putString(USER_KEY, userJson)
            apply()
        }
    }

    fun getUser(context: Context): UserDTO? {
        val sharedPreferences = getSharedPreferences(context)
        val userJson = sharedPreferences.getString(USER_KEY, null)
        return if (userJson != null) {
            Gson().fromJson(userJson, UserDTO::class.java)
        } else {
            null
        }
    }

    private fun clearTokens(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(ACCESS_TOKEN_KEY)
            remove(REFRESH_TOKEN_KEY)
            apply()
        }
    }

    private fun clearUser(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(USER_KEY)
            apply()
        }
    }

    private fun clearProvider(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(PROVIDER)
            apply()
        }
    }

    fun clearAll(context: Context) {
        clearTokens(context)
        clearUser(context)
        clearProvider(context)
        Log.d("SecurePreferences", "All secure preferences cleared.")
    }
}