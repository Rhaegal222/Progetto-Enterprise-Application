package com.android.frontend.view_models.user

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.TokenManager
import com.android.frontend.dto.WishlistDTO
import com.android.frontend.dto.creation.WishlistCreateDTO

class WishlistViewModel : ViewModel() {
    private val wishlist = MutableLiveData<List<WishlistDTO>>()
    val wishlistLiveData: MutableLiveData<List<WishlistDTO>> get() = wishlist

    private val wishlistDetails = MutableLiveData<WishlistDTO>()
    val wishlistDetailsLiveData: MutableLiveData<WishlistDTO> get() = wishlistDetails

    fun createWishlist(context: Context, wishlistDTO: WishlistDTO) {
        val wishlistService = RetrofitInstance.getWishlistApi(context)
        val accessToken = TokenManager.getInstance().getAccessToken(context)
        val wishlistCreateDTO = WishlistCreateDTO(wishlistDTO.wishlistName, wishlistDTO.visibility)
        wishlistService.createWishlist("Bearer $accessToken", wishlistCreateDTO)
    }

    fun fetchAllWishlists(context: Context) {
        val wishlistService = RetrofitInstance.getWishlistApi(context)
        val accessToken = TokenManager.getInstance().getAccessToken(context)
        val call = wishlistService.getAllLoggedUserWishlists("Bearer $accessToken")
        call.enqueue(object : retrofit2.Callback<List<WishlistDTO>> {
            override fun onResponse(
                call: retrofit2.Call<List<WishlistDTO>>,
                response: retrofit2.Response<List<WishlistDTO>>) {
                if (response.isSuccessful) {
                    response.body()?.let { wishlists ->
                        wishlist.postValue(wishlists)
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<List<WishlistDTO>>, t: Throwable) {
                Log.e("DEBUG", "Error fetching wishlists", t)
            }
        })
    }

}