package com.android.frontend.service

import com.android.frontend.dto.OrderDTO
import com.android.frontend.dto.creation.OrderCreateDTO
import retrofit2.Call
import retrofit2.http.*

interface OrderService {
    @POST("/api/v1/orders/addOrder")
    fun addOrder(
        @Header("Authorization") authorization: String,
        @Body orderCreateDTO: OrderCreateDTO
    ): Call<Void>

    @PUT("/api/v1/orders/updateOrder/{id}")
    fun updateOrder(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Body orderDTO: OrderDTO
    ): Call<Void>

    @DELETE("/api/v1/orders/deleteOrder/{id}")
    fun deleteOrder(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<Void>

    @GET("/api/v1/orders/getOrder/{id}")
    fun getOrder(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<OrderDTO>

    @GET("/api/v1/orders/getAllOrders")
    fun getAllOrders(
        @Header("Authorization") authorization: String
    ): Call<List<OrderDTO>>
}
