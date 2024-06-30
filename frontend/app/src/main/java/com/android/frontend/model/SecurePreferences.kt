package com.android.frontend.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.android.frontend.controller.models.AddressDTO
import com.android.frontend.controller.models.UserDTO
import com.android.frontend.controller.models.PaymentMethodDTO
import com.android.frontend.controller.models.UserBasicDTO
import com.google.gson.Gson

object SecurePreferences {

    private const val PREFS_FILENAME = "secure_prefs"
    private const val ACCESS_TOKEN_KEY = "access_token"
    private const val REFRESH_TOKEN_KEY = "refresh_token"
    private const val USER_KEY = "user_data"
    private const val PROVIDER = "user_provider"

    private const val ADDRESS_KEY = "address_data"
    private const val DEFAULT_ADDRESS_KEY = "default_address_data"
    private const val CURRENT_ADDRESS_KEY = "current_address_data"
    private const val PAYMENT_METHOD_KEY = "payment_method_data"
    private const val DEFAULT_PAYMENT_METHOD_KEY = "default_payment_method_data"
    private const val CURRENT_PAYMENT_METHOD_KEY = "current_payment_method_data"

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
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            putString(ACCESS_TOKEN_KEY, accessToken)
            apply()
        }
    }

    fun getAccessToken(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, null)
    }

    fun getTokens(context: Context): Pair<String?, String?> {
        val sharedPreferences = getSharedPreferences(context)
        return Pair(sharedPreferences.getString(ACCESS_TOKEN_KEY, null), sharedPreferences.getString(REFRESH_TOKEN_KEY, null))
    }

    fun clearAccessToken(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(ACCESS_TOKEN_KEY)
            apply()
        }
    }

    fun saveRefreshToken(context: Context, refreshToken: String) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            putString(REFRESH_TOKEN_KEY, refreshToken)
            apply()
        }
    }

    fun getRefreshToken(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null)
    }

    fun clearRefreshToken(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(REFRESH_TOKEN_KEY)
            apply()
        }
    }

    fun saveTokens(context: Context, accessToken: String, refreshToken: String) {
        saveAccessToken(context, accessToken)
        saveRefreshToken(context, refreshToken)
    }

    private fun clearTokens(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(ACCESS_TOKEN_KEY)
            remove(REFRESH_TOKEN_KEY)
            apply()
        }
    }

    fun saveUser(context: Context, user: UserBasicDTO) {
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

    private fun clearUser(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(USER_KEY)
            apply()
        }
    }

    fun saveProvider(context: Context, provider: String) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            putString(PROVIDER, provider)
            apply()
        }
    }

    fun getProvider(context: Context): String? {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(PROVIDER, null)
    }

    private fun clearProvider(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(PROVIDER)
            apply()
        }
    }

    fun saveAddress(context: Context, address: AddressDTO) {
        val sharedPreferences = getSharedPreferences(context)
        val addressJson = Gson().toJson(address)
        with(sharedPreferences.edit()) {
            putString(ADDRESS_KEY, addressJson)
            apply()
        }
    }

    fun getAddress(context: Context): AddressDTO? {
        val sharedPreferences = getSharedPreferences(context)
        val addressJson = sharedPreferences.getString(ADDRESS_KEY, null)
        return if (addressJson != null) {
            Gson().fromJson(addressJson, AddressDTO::class.java)
        } else {
            null
        }
    }

    private fun clearAddress(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(ADDRESS_KEY)
            apply()
        }
    }

    fun saveDefaultAddress(context: Context, address: AddressDTO) {
        val sharedPreferences = getSharedPreferences(context)
        val addressJson = Gson().toJson(address)
        with(sharedPreferences.edit()) {
            putString(DEFAULT_ADDRESS_KEY, addressJson)
            apply()
        }
    }

    fun getDefaultAddress(context: Context): AddressDTO? {
        val sharedPreferences = getSharedPreferences(context)
        val addressJson = sharedPreferences.getString(DEFAULT_ADDRESS_KEY, null)
        return if (addressJson != null) {
            Gson().fromJson(addressJson, AddressDTO::class.java)
        } else {
            null
        }
    }

    private fun clearDefaultAddress(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(DEFAULT_ADDRESS_KEY)
            apply()
        }
    }

    fun saveCurrentAddress(context: Context, address: AddressDTO) {
        val sharedPreferences = getSharedPreferences(context)
        val addressJson = Gson().toJson(address)
        with(sharedPreferences.edit()) {
            putString(CURRENT_ADDRESS_KEY, addressJson)
            apply()
        }
    }

    fun getCurrentAddress(context: Context): AddressDTO? {
        val sharedPreferences = getSharedPreferences(context)
        val addressJson = sharedPreferences.getString(CURRENT_ADDRESS_KEY, null)
        return if (addressJson != null) {
            Gson().fromJson(addressJson, AddressDTO::class.java)
        } else {
            null
        }
    }

    private fun clearCurrentAddress(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(CURRENT_ADDRESS_KEY)
            apply()
        }
    }

    fun savePaymentMethod(context: Context, paymentMethod: PaymentMethodDTO) {
        val sharedPreferences = getSharedPreferences(context)
        val paymentMethodJson = Gson().toJson(paymentMethod)
        with(sharedPreferences.edit()) {
            putString(PAYMENT_METHOD_KEY, paymentMethodJson)
            apply()
        }
    }

    fun getPaymentMethod(context: Context): PaymentMethodDTO? {
        val sharedPreferences = getSharedPreferences(context)
        val paymentMethodJson = sharedPreferences.getString(PAYMENT_METHOD_KEY, null)
        return if (paymentMethodJson != null) {
            Gson().fromJson(paymentMethodJson, PaymentMethodDTO::class.java)
        } else {
            null
        }
    }

    private fun clearPaymentMethod(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(PAYMENT_METHOD_KEY)
            apply()
        }
    }

    fun saveDefaultPaymentMethod(context: Context, paymentMethod: PaymentMethodDTO) {
        val sharedPreferences = getSharedPreferences(context)
        val paymentMethodJson = Gson().toJson(paymentMethod)
        with(sharedPreferences.edit()) {
            putString(DEFAULT_PAYMENT_METHOD_KEY, paymentMethodJson)
            apply()
        }
    }

    fun getDefaultPaymentMethod(context: Context): PaymentMethodDTO? {
        val sharedPreferences = getSharedPreferences(context)
        val paymentMethodJson = sharedPreferences.getString(DEFAULT_PAYMENT_METHOD_KEY, null)
        return if (paymentMethodJson != null) {
            Gson().fromJson(paymentMethodJson, PaymentMethodDTO::class.java)
        } else {
            null
        }
    }

    private fun clearDefaultPaymentMethod(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(DEFAULT_PAYMENT_METHOD_KEY)
            apply()
        }
    }


    fun saveCurrentPaymentMethod(context: Context, paymentMethod: PaymentMethodDTO) {
        val sharedPreferences = getSharedPreferences(context)
        val paymentMethodJson = Gson().toJson(paymentMethod)
        with(sharedPreferences.edit()) {
            putString(CURRENT_PAYMENT_METHOD_KEY, paymentMethodJson)
            apply()
        }
    }

    fun getCurrentPaymentMethod(context: Context): PaymentMethodDTO? {
        val sharedPreferences = getSharedPreferences(context)
        val paymentMethodJson = sharedPreferences.getString(CURRENT_PAYMENT_METHOD_KEY, null)
        return if (paymentMethodJson != null) {
            Gson().fromJson(paymentMethodJson, PaymentMethodDTO::class.java)
        } else {
            null
        }
    }

    private fun clearCurrentPaymentMethod(context: Context) {
        val sharedPreferences = getSharedPreferences(context)
        with(sharedPreferences.edit()) {
            remove(CURRENT_PAYMENT_METHOD_KEY)
            apply()
        }
    }

    fun clearAll(context: Context) {
        clearTokens(context)
        clearUser(context)
        clearProvider(context)
        clearAddress(context)
        clearDefaultAddress(context)
        clearCurrentAddress(context)
        clearPaymentMethod(context)
        clearDefaultPaymentMethod(context)
        clearCurrentPaymentMethod(context)
    }
}