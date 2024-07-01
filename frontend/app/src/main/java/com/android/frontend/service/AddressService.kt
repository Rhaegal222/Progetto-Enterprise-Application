// ShippingService.kt
package com.android.frontend.service

import com.android.frontend.dto.creation.AddressCreateDTO
import com.android.frontend.dto.AddressDTO
import retrofit2.Call
import retrofit2.http.*

interface AddressService {
    @GET("/api/v1/shipping-addresses/getAllShippingAddresses")
    fun getAllShippingAddresses(
        @Header("Authorization") authorization: String
    ): Call<List<AddressDTO>>

    @POST("/api/v1/shipping-addresses/addShippingAddress")
    fun addShippingAddress(
        @Header("Authorization") authorization: String,
        @Body shippingAddressCreateDTO: AddressCreateDTO,
    ): Call<AddressDTO>

    @DELETE("/api/v1/shipping-addresses/deleteShippingAddress/{id}")
    fun deleteShippingAddress(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<Void>

    @PUT("/api/v1/shipping-addresses/setDefaultShippingAddress/{id}")
    fun setDefaultShippingAddress(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<Void>
}
