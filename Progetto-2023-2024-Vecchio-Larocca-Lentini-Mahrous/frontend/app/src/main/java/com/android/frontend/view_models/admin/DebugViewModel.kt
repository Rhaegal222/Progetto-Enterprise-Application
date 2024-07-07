package com.android.frontend.view_models.admin

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.creation.BrandCreateDTO
import com.android.frontend.dto.creation.CategoryCreateDTO
import com.android.frontend.view_models.user.ProductViewModel
import com.android.frontend.view_models.user.AddressViewModel
import com.android.frontend.view_models.user.PaymentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DebugViewModel : ViewModel() {

    private val addresViewModel = AddressViewModel()
    private val brandViewModel = BrandViewModel()
    private val categoryViewModel = CategoryViewModel()
    private val paymentViewModel = PaymentViewModel()
    private val productViewModel = ProductViewModel()

    private val _isLoading = MutableLiveData(false)

    fun generateAddress(context: Context) {
        Log.d("DEBUG", "${getCurrentStackTrace()} Generating address")
        addresViewModel.addShippingAddress(context, "Mario Rossi", "1234567890", "Street", "Additional info", "12345", "City", "Province", "Country", false)
    }
    fun generateBrand(context: Context) {
        Log.d("DEBUG", "${getCurrentStackTrace()} Generating brand")
        val brand = BrandCreateDTO("Brand", "Description")
        brandViewModel.addBrand( brand, context)
    }
    fun generateProductCategory(context: Context) {
        Log.d("DEBUG", "${getCurrentStackTrace()} Generating product category")
        val category = CategoryCreateDTO("Category")
        categoryViewModel.addCategory(category, context)
    }
    fun generatePayment(context: Context) {
        Log.d("DEBUG", "${getCurrentStackTrace()} Generating payment")
        paymentViewModel.addPaymentCard(context, "1111222233334444", "12", "20", "Mario Rossi", false)
    }

    fun generateProduct(context: Context) {
        // productViewModel.generateProduct(context)
    }

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
