package com.android.frontend.view_models

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DebugViewModel : ViewModel() {

    private val addresViewModel = AddressViewModel()
    private val brandViewModel = BrandViewModel()
    private val categoryViewModel = ProductCategoryViewModel()
    private val paymentViewModel = PaymentViewModel()
    private val productViewModel = ProductViewModel()

    private val _isLoading = MutableLiveData(false)

    fun generateAddress(context: Context) {
        addresViewModel.addShippingAddress(context, "Firstname", "Lastname", "Country", "City", "Street", "ZipCode", false)
    }
    fun generateBrand(context: Context) {
        brandViewModel.addBrand(context, "Brand", "Description")
    }
    fun generateProductCategory(context: Context) {
        categoryViewModel.addProductCategory(context, "Category")
    }
    fun generatePayment(context: Context) {
        paymentViewModel.addPaymentCard(context, "1111222233334444", "12", "20", "Mario Rossi", false)
    }

//    fun generateProduct(): ProductCreateDTO {
//        return ProductCreateDTO(
//            title = "Product",
//            productPrice = 10.0.toBigDecimal(),
//            deliveryPrice = 5.0.toBigDecimal(),
//            availability = ProductDTO.Availability.AVAILABLE,
//            quantity = 10,
//            brand = ProductDTO.BrandDTO("Brand"),
//            productCategory = ProductDTO.ProductCategoryDTO("Category")
//        )
//    }

    fun rejectToken(context: Context) {
        viewModelScope.launch {
            val userService = RetrofitInstance.getUserApi(context)
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val call = userService.rejectToken("Bearer $accessToken")
            try {
                val response = withContext(Dispatchers.IO) {
                    call.execute()
                }
                if (response.isSuccessful) {
                    Log.d("DEBUG", "${getCurrentStackTrace()} Token rejected")
                } else {
                    Log.d("DEBUG", "${getCurrentStackTrace()} Token rejection failed")
                }
            } catch (e: Exception) {
                Log.e("DEBUG", "${getCurrentStackTrace()} Error rejecting token", e)
            }
        }
    }

    fun showToken(context: Context) {
        val accessToken = TokenManager.getInstance().getAccessToken(context)
        val refreshToken = TokenManager.getInstance().getRefreshToken(context)
        Log.d("DEBUG", "${getCurrentStackTrace()} Access token: $accessToken")
        Log.d("DEBUG", "${getCurrentStackTrace()} Refresh token: $refreshToken")
    }
}
