// ShippingService.kt
package com.android.frontend.service

import com.android.frontend.dto.creation.AddressCreateDTO
import com.android.frontend.dto.AddressDTO
import retrofit2.Call
import retrofit2.http.*

interface AddressService {

    @POST("/api/v1/addresses/addAddress")
    fun addAddress(
        @Header("Authorization") authorization: String,
        @Body addressCreateDTO: AddressCreateDTO,
    ): Call<AddressDTO>

    @DELETE("/api/v1/addresses/deleteAddress/{id}")
    fun deleteAddress(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<Void>

    @PUT("/api/v1/addresses/setDefaultAddress/{id}")
    fun setAddress(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<Void>

    @PUT("/api/v1/addresses/updateAddress/{id}")
    fun updateAddress(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Body shippingAddressDTO: AddressDTO
    ): Call<AddressDTO>

    @GET("/api/v1/addresses/getAddress/{id}")
    fun getShippingAddress(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<AddressDTO>

    @GET("/api/v1/addresses/getAllAddresses")
    fun getAllShippingAddresses(
        @Header("Authorization") authorization: String
    ): Call<List<AddressDTO>>

    @GET("/api/v1/addresses/getAllLoggedUserAddresses")
    fun getAllLoggedUserAddresses(
        @Header("Authorization") authorization: String
    ): Call<List<AddressDTO>>
}
