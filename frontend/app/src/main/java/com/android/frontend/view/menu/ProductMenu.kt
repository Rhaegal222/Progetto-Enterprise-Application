package com.android.frontend.view.menu
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.frontend.view_models.ProductViewModel

//@Composable
//fun ProductMenu(navController: NavController, productViewModel: ProductViewModel) {
//    val categories = productViewModel.categoriesLiveData.observeAsState().value
//    productViewModel.fetchAllCategories()
//
//    Column(modifier = Modifier
//        .fillMaxSize()
//        .padding(16.dp)) {
//        Text(
//            text = "Products Menu",
//            style = MaterialTheme.typography.titleLarge,
//            modifier = Modifier.padding(16.dp)
//        )
//
//        LazyColumn {
//            items(categories ?: emptyList()) { category ->
//                Text(
//                    text = category.name,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//                            navController.navigate("products/${category.id}")
//                        }
//                )
//            }
//        }
//    }
//}

