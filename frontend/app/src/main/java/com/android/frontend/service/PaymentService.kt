package com.android.frontend.service

import com.android.frontend.dto.PaymentMethodDTO
import com.android.frontend.dto.creation.PaymentMethodCreateDTO
import retrofit2.Call
import retrofit2.http.*

interface PaymentService {
    @GET("/api/v1/payment-methods/getAllPaymentMethods")
    fun getAllPaymentMethods(
        @Header("Authorization") authorization: String
    ): Call<List<PaymentMethodDTO>>

    @POST("/api/v1/payment-methods/addPaymentMethod")
    fun addPaymentMethod(
        @Header("Authorization") authorization: String,
        @Body paymentMethodCreateDTO: PaymentMethodCreateDTO,
    ): Call<PaymentMethodDTO>

    @DELETE("/api/v1/payment-methods/deletePaymentMethod/{id}")
    fun deletePaymentMethod(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<Void>

    @GET("/api/v1/payment-methods/{id}")
    fun getPaymentMethod(
        @Path("id") id: String
    ): Call<PaymentMethodDTO>

    // Set as default payment method
    @PUT("/api/v1/payment-methods/setDefaultPaymentMethod/{id}")
    fun setDefaultPaymentMethod(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<Void>

    @GET("/api/v1/payment-methods/getAllLoggedUserPaymentMethods")
    fun getAllLoggedUserPaymentMethods(
        @Header("Authorization") authorization: String
    ): Call<List<PaymentMethodDTO>>
/*
    @PUT("/api/v1/payment-methods/{id}")
    fun updatePaymentMethod(
        @Path("id") id: String,
        @Body paymentMethodDTO: PaymentMethodDTO
    ): Call<PaymentMethodDTO>
 */
}
