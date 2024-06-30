package com.android.frontend.view.page.product

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.persistence.CurrentDataUtils
import com.android.frontend.view_models.ProductViewModel

@SuppressLint("SuspiciousIndentation")
@Composable
fun ProductDetailsPage(productViewModel: ProductViewModel) {

    val context = LocalContext.current

    val productId = CurrentDataUtils.currentProductId
    val productDetails = productViewModel.productDetailsLiveData.observeAsState().value

    productViewModel.getProductDetails(context, productId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (productDetails != null) {
                        Text(text = "Product Name: ${productDetails.title}", style = MaterialTheme.typography.h6)
                    }
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    if (productDetails != null) {
                        Text(text = "${productDetails.title}", style = MaterialTheme.typography.h4)
                        Log.d("DEBUG", "${getCurrentStackTrace()}, Product details: $productDetails")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    if (productDetails != null) {
                        Column {

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Product Brand: ${productDetails.brand.name}", style = MaterialTheme.typography.body1)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = "Product Category: ${productDetails.productCategory.categoryName}", style = MaterialTheme.typography.body1)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Product Price: ${productDetails.productPrice}", style = MaterialTheme.typography.body1)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Product Description: ${productDetails.description}", style = MaterialTheme.typography.body1)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text ="Product Quantity: ${productDetails.quantity}", style = MaterialTheme.typography.body1)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text ="Product ingredients: ${productDetails.ingredients}", style = MaterialTheme.typography.body1)
                        }
                    }
                }
            }
        }
    )
}