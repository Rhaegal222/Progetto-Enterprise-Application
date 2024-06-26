package com.android.frontend

import com.android.frontend.service.GoogleAuthenticationService
import com.android.frontend.service.ProductService
import com.android.frontend.service.UserService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080") // Use this for the emulator
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val googleAuthApi: GoogleAuthenticationService by lazy {
        retrofit.create(GoogleAuthenticationService::class.java)
    val productApi: ProductService by lazy {
        retrofit.create(ProductService::class.java)
    }
}