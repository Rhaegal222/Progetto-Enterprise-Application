package com.android.frontend.service

import com.android.frontend.dto.PaymentMethodDTO
import com.android.frontend.dto.creation.PaymentMethodCreateDTO
import retrofit2.Call
import retrofit2.http.*

interface PaymentService {

    @POST("/api/v1/paymentMethods/addPaymentMethod")
    fun addPaymentMethod(
        @Header("Authorization") authorization: String,
        @Body paymentMethodCreateDTO: PaymentMethodCreateDTO,
    ): Call<PaymentMethodDTO>

    @PUT("/api/v1/paymentMethods/setDefaultPaymentMethod/{id}")
    fun setDefaultPaymentMethod(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<Void>

    @PUT("/api/v1/paymentMethods/updatePaymentMethod/{id}")
    fun updatePaymentMethod(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Body paymentMethodDTO: PaymentMethodDTO
    ): Call<Void>

    @DELETE("/api/v1/paymentMethods/deletePaymentMethod/{id}")
    fun deletePaymentMethod(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<Void>

    @GET("/api/v1/getPaymentMethodById/{id}")
    fun getPaymentMethodById(
        @Path("id") id: String
    ): Call<PaymentMethodDTO>

    @GET("/api/v1/paymentMethods/getAllPaymentMethods")
    fun getAllPaymentMethods(
        @Header("Authorization") authorization: String
    ): Call<List<PaymentMethodDTO>>

    @GET("/api/v1/paymentMethodss/getAllLoggedUserPaymentMethods")
    fun getAllLoggedUserPaymentMethods(
        @Header("Authorization") authorization: String
    ): Call<List<PaymentMethodDTO>>
}
