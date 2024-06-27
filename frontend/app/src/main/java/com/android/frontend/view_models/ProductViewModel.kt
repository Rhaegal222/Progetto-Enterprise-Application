package com.android.frontend.view_models


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.controller.models.ProductDTO
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class ProductViewModel : ViewModel() {
    private val products = MutableLiveData<List<ProductDTO>>()
    val productsLiveData: LiveData<List<ProductDTO>> get() = products

    private val productService = RetrofitInstance.productApi
    private val _productDetails = MutableLiveData<ProductDTO>()
    val productDetails: LiveData<ProductDTO> get() = _productDetails

    fun setProduct(productDTO: ProductDTO) {
        viewModelScope.launch {
            RetrofitInstance.productApi.addProduct(productDTO)
        }
    }

    fun getProductDetails(id: String) {
        viewModelScope.launch {
            val call = productService.getProductById(id)
            Log.d("ProductViewModel", "Fetching product details for id riga 31: $id")
            call.enqueue(object : retrofit2.Callback<ProductDTO> {
                override fun onResponse(
                    call: retrofit2.Call<ProductDTO>,
                    response: retrofit2.Response<ProductDTO>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { product ->
                            _productDetails.value = product
                        }
                    } else {
                        Log.e(
                            "ProductViewModel",
                            "Failed to fetch products: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: retrofit2.Call<ProductDTO>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("ProductViewModel", "Timeout error fetching product details", t)
                    } else {
                        Log.e("ProductViewModel", "Error fetching product details", t)
                    }
                }
            }
            )
        }
    }
    fun fetchAllProducts() {

        viewModelScope.launch {
            val call = productService.getAllProducts()
            call.enqueue(object : retrofit2.Callback<List<ProductDTO>> {
                override fun onResponse(
                    call: retrofit2.Call<List<ProductDTO>>,
                    response: retrofit2.Response<List<ProductDTO>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { productsList ->
                            products.value = productsList
                        }
                    } else {
                        Log.e(
                            "ProductViewModel",
                            "Failed to fetch products: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<ProductDTO>>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("ProductViewModel", "Timeout error fetching products", t)
                    } else {
                        Log.e("ProductViewModel", "Error fetching products", t)
                    }
                }
            }
            )
        }
    }
}

