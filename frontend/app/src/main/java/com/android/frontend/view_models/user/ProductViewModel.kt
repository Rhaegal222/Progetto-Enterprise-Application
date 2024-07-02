package com.android.frontend.view_models.user


import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.TokenManager
import com.android.frontend.dto.creation.CartCreateDTO
import com.android.frontend.dto.CartDTO
import com.android.frontend.dto.ProductDTO
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.creation.ProductCreateDTO
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class ProductViewModel : ViewModel() {

    private val products = MutableLiveData<List<ProductDTO>>()
    val productsLiveData: LiveData<List<ProductDTO>> get() = products


    private val productDetails = MutableLiveData<ProductDTO>()
    val productDetailsLiveData: LiveData<ProductDTO> get() = productDetails

    fun setProduct(context: Context, productCreateDTO: ProductCreateDTO) {
        viewModelScope.launch {
            val productService = RetrofitInstance.getProductApi(context)
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            productService.addProduct("Bearer $accessToken", productCreateDTO)
        }
    }

    fun addProductToCart(context: Context, userId: String, productId: String, quantity: Int) {
        viewModelScope.launch {
            val cartService = RetrofitInstance.getCartApi(context)
            val cartCreateDTO = CartCreateDTO(userId, productId, quantity)
            val call = cartService.addProductToCart(cartCreateDTO)
            call.enqueue(object : retrofit2.Callback<CartDTO> {
                override fun onResponse(call: retrofit2.Call<CartDTO>, response: retrofit2.Response<CartDTO>) {
                    if (response.isSuccessful) {
                        response.body()?.let { cart ->
                            // aggiornare il conteggio degli articoli nel carrello
                            CartViewModel().loadCart(context)
                        }
                    } else {
                        Log.e("DEBUG", "Failed to add product to cart: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<CartDTO>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("DEBUG", "Timeout error adding product to cart", t)
                    } else {
                        Log.e("DEBUG", "Error adding product to cart", t)
                    }
                }
            })
        }
    }


    fun getProductDetails(context: Context, id: String) {
        viewModelScope.launch {
            val productService = RetrofitInstance.getProductApi(context)
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val call = productService.getProductById("Bearer $accessToken",id)
            call.enqueue(object : retrofit2.Callback<ProductDTO> {
                override fun onResponse(
                    call: retrofit2.Call<ProductDTO>,
                    response: retrofit2.Response<ProductDTO>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { product ->
                            Log.d("DEBUG", "${getCurrentStackTrace()} Product details: $product")
                            productDetails.value = product
                        }
                    } else {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Failed to fetch products: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<ProductDTO>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Timeout error fetching product details", t)
                    } else {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching product details", t)
                    }
                }
            }
            )
        }
    }
    fun fetchAllProducts(context: Context) {

        viewModelScope.launch {
            val productService = RetrofitInstance.getProductApi(context)
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val call = productService.getAllProducts("Bearer $accessToken")
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
                        Log.e("DEBUG", " ${getCurrentStackTrace()} Failed to fetch products: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<ProductDTO>>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Timeout error fetching products", t)
                    } else {
                        Log.e("DEBUG", "${getCurrentStackTrace()} Error fetching products", t)
                    }
                }
            }
            )
        }
    }
}
