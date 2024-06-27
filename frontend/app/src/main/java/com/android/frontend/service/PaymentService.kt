package com.example.frontend.service

import com.example.frontend.controller.models.PaymentMethodDTO
import com.android.frontend.controller.models.PaymentMethodCreateDTO
import retrofit2.Call
import retrofit2.http.*

interface PaymentService {
    @GET("/api/v1/payment-methods")
    fun getPaymentMethods(): Call<List<PaymentMethodDTO>>

    @POST("/api/v1/payment-methods")
    fun createPaymentMethod(
        @Body paymentMethodCreateDTO: PaymentMethodCreateDTO,
    ): Call<PaymentMethodDTO>

    @PUT("/api/v1/payment-methods/{id}")
    fun updatePaymentMethod(
        @Path("id") id: String,
        @Body paymentMethodDTO: PaymentMethodDTO
    ): Call<PaymentMethodDTO>

    @DELETE("/api/v1/payment-methods/{id}")
    fun deletePaymentMethod(
        @Path("id") id: String
    ): Call<Void>

    @GET("/api/v1/payment-methods/{id}")
    fun getPaymentMethod(
        @Path("id") id: String
    ): Call<PaymentMethodDTO>


    /*
    @GET("/api/v1/payment-methods")
    fun getMyPaymentMethods(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<Page<PaymentMethodBasicDTO>>
     */
}
