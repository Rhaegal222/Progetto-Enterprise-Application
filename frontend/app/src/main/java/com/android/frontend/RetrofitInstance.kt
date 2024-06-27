package com.android.frontend
import com.android.frontend.service.GoogleAuthenticationService
import com.android.frontend.service.ProductService
import com.android.frontend.service.UserService
import com.example.frontend.service.PaymentService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080").client(okHttpClient) // Use this for the emulator
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val googleAuthApi: GoogleAuthenticationService by lazy {
        retrofit.create(GoogleAuthenticationService::class.java)
    }

    val productApi: ProductService by lazy {
        retrofit.create(ProductService::class.java)
    }

    val paymentApi: PaymentService by lazy {
        retrofit.create(PaymentService::class.java)
    }

    private val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

}