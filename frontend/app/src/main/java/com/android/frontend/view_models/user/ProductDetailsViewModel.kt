package com.android.frontend.view_models.user

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.frontend.RetrofitInstance
import com.android.frontend.config.TokenManager
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.ProductDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.net.SocketTimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProductDetailsViewModel: ViewModel() {

    private val prodImag = MutableLiveData<Uri?>(null)
    val prodImagLiveData : LiveData<Uri?> get() = prodImag

    val productDetails = MutableLiveData<ProductDTO>()

    fun getProductDetails(context: Context, id: Long) {
        viewModelScope.launch {
            val productService = RetrofitInstance.getProductApi(context)
            val accessToken = TokenManager.getInstance().getAccessToken(context)
            val call = productService.getProductById("Bearer $accessToken", id)
            call.enqueue(object : retrofit2.Callback<ProductDTO> {
                override fun onResponse(
                    call: retrofit2.Call<ProductDTO>,
                    response: retrofit2.Response<ProductDTO>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { product ->
                            Log.d("DEBUG", "Product details: $product")
                            productDetails.value = product
                        }
                    } else {
                        Log.e("DEBUG", "Failed to fetch product: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: retrofit2.Call<ProductDTO>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        Log.e("DEBUG", "Timeout error fetching product details", t)
                    } else {
                        Log.e("DEBUG", "Error fetching product details", t)
                    }
                }
            })
        }
    }

    fun getProductImage(context: Context, productId: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val type = "product_photos"
                    val folderName = productId.toString()
                    val fileName = "photoProduct.png"
                    val responseBody = fetchImage(context, type, folderName, fileName)
                    responseBody?.let {
                        val tempFile = saveImageToFile(context, responseBody)
                        prodImag.postValue(Uri.fromFile(tempFile))
                    } ?: run {
                        Log.e("DEBUG", "${getCurrentStackTrace()},Image retrieval failed")
                    }
                } catch (e: Exception) {
                    Log.e("DEBUG", "${getCurrentStackTrace()},Image retrieval error: ${e.message}")
                }
            }
        }
    }

    private suspend fun fetchImage(context: Context, type: String, folderName: String, fileName: String): ResponseBody? {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val productImageService = RetrofitInstance.getProductImageApi(context)
                val call = productImageService.getImage(type, folderName, fileName)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            continuation.resume(response.body())
                        } else {
                            continuation.resume(null)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        continuation.resume(null)
                    }
                })
            }
        }
    }

    private suspend fun saveImageToFile(context: Context, responseBody: ResponseBody): File {
        return withContext(Dispatchers.IO) {
            val tempFile = File.createTempFile("product", "jpg", context.cacheDir)
            val inputStream = responseBody.byteStream()
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            tempFile
        }
    }
}