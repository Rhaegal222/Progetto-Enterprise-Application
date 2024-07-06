package com.android.frontend

import android.content.Context
import android.util.Log
import com.android.frontend.config.TokenInterceptor
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.service.*
import it.unical.inf.ea.backend.data.services.interfaces.OrderService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val CLIENT_BASE_URL = CurrentDataUtils.baseUrl

    suspend fun initializeBackendBaseUrl() {
        if (CurrentDataUtils.backendBaseUrl == "") {
            val apiService = Retrofit.Builder()
                .baseUrl(CLIENT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)

            val response = withContext(Dispatchers.IO) {
                apiService.getBaseUrl()
            }
            CurrentDataUtils.backendBaseUrl = response.baseUrl
            Log.d("DEBUG", "Backend base url: $CurrentDataUtils.backendBaseUrl")
        }
    }

    private fun getClientRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(CLIENT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getBackendRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(CurrentDataUtils.backendBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getAuthenticatedRetrofit(context: Context): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(context))
            .build()

        return Retrofit.Builder()
            .baseUrl(CLIENT_BASE_URL)
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

    fun getProductImageApi(context: Context): ProductImageService {
        return getAuthenticatedRetrofit(context).create(ProductImageService::class.java)
    }

    fun getAddressApi(context: Context): AddressService {
        return getAuthenticatedRetrofit(context).create(AddressService::class.java)
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

    fun getWishlistApi(context: Context): WishlistService {
        return getAuthenticatedRetrofit(context).create(WishlistService::class.java)
    }

    fun getOrderApi(context: Context): OrderService {
        return getAuthenticatedRetrofit(context).create(OrderService::class.java)
    }
}
