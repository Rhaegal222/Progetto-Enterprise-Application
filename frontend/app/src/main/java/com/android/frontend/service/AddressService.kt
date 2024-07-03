// ShippingService.kt
package com.android.frontend.service

import com.android.frontend.dto.creation.AddressCreateDTO
import com.android.frontend.dto.AddressDTO
import retrofit2.Call
import retrofit2.http.*

interface AddressService {

    @POST("/api/v1/shipping-addresses/addAddress")
    fun addAddress(
        @Header("Authorization") authorization: String,
        @Body addressCreateDTO: AddressCreateDTO,
    ): Call<AddressDTO>

    @DELETE("/api/v1/shipping-addresses/deleteAddress/{id}")
    fun deleteAddress(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<Void>

    @PUT("/api/v1/shipping-addresses/setAddress/{id}")
    fun setAddress(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<Void>

    @PUT("/api/v1/shipping-addresses/updateAddress/{id}")
    fun updateAddress(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Body shippingAddressDTO: AddressDTO
    ): Call<AddressDTO>

    @GET("/api/v1/shipping-addresses/getShippingAddress/{id}")
    fun getShippingAddress(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<AddressDTO>

    @GET("/api/v1/shipping-addresses/getAllShippingAddresses")
    fun getAllShippingAddresses(
        @Header("Authorization") authorization: String
    ): Call<List<AddressDTO>>



    @GET("/api/v1/shipping-addresses/getAllLoggedUserAddresses")
    fun getAllLoggedUserAddresses(
        @Header("Authorization") authorization: String
    ): Call<List<AddressDTO>>
}
