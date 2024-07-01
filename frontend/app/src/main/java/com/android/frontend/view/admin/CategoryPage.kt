package com.android.frontend.view.admin

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.view_models.admin.ProductCategoryBrandViewModel
import com.android.frontend.dto.ProductCategoryDTO
import com.android.frontend.navigation.Navigation

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CategoryPage(navController: NavHostController, viewModel: ProductCategoryBrandViewModel = viewModel()) {
    val context = LocalContext.current
    val categories by viewModel.allCategories.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchAllCategories(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.category)) },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Navigation.AddCategoryPage.route)
                    }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_category))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryCard(category = category, onDelete = {
                    viewModel.deleteCategory(it.id, context)
                    viewModel.fetchAllCategories(context)
                })
            }
        }
    }
}

@Composable
fun CategoryCard(category: ProductCategoryDTO, onDelete: (ProductCategoryDTO) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = category.categoryName, style = MaterialTheme.typography.h6)
            IconButton(onClick = { onDelete(category) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(id = R.string.delete), tint = Color.Red)
            }
        }
    }
}
