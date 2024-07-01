package com.android.frontend

import android.content.Context
import com.android.frontend.config.TokenInterceptor
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.service.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val BASE_URL = CurrentDataUtils.baseUrl

    private fun getSimpleRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getAuthenticatedRetrofit(context: Context): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun getUserApi(context: Context): UserService {
        return getAuthenticatedRetrofit(context).create(UserService::class.java)
    }

    fun getGoogleAuthApi(context: Context): GoogleAuthenticationService {
        return getAuthenticatedRetrofit(context).create(GoogleAuthenticationService::class.java)
    }

    fun getProductApi(context: Context): ProductService {
        return getAuthenticatedRetrofit(context).create(ProductService::class.java)
    }

    fun getPaymentApi(context: Context): PaymentService {
        return getAuthenticatedRetrofit(context).create(PaymentService::class.java)
    }

    fun getUserImageApi(context: Context): UserImageService {
        return getAuthenticatedRetrofit(context).create(UserImageService::class.java)
    }

    fun getAddressApi(context: Context): AddressService {
        return getAuthenticatedRetrofit(context).create(AddressService::class.java)
    }

    // Funzione per ottenere UserService senza autenticazione
    fun getSimpleUserApi(): UserService {
        return getSimpleRetrofit().create(UserService::class.java)
    }

    fun getCartApi(context: Context): CartService {
        return getAuthenticatedRetrofit(context).create(CartService::class.java)
    }

    fun getProductCategoryApi(context: Context): ProductCategoryService {
        return getAuthenticatedRetrofit(context).create(ProductCategoryService::class.java)
    }

    fun getBrandApi(context: Context): BrandService {
        return getAuthenticatedRetrofit(context).create(BrandService::class.java)
    }
}
