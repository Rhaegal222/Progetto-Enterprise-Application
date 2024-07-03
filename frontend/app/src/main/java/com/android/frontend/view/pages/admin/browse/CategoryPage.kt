package com.android.frontend.view.pages.admin.browse

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import com.android.frontend.dto.CategoryDTO
import com.android.frontend.view_models.admin.ProductCategoryBrandViewModel
import com.android.frontend.navigation.Navigation

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
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
                    IconButton(onClick = { navController.navigate(Navigation.AdminMenu.route)  }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
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
            let {
                categories.forEach { category ->
                    item {
                        CategoryCard(category = category) {
                            viewModel.deleteCategory(context, category.id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: CategoryDTO, onDelete: (CategoryDTO) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = category.name)
            IconButton(onClick = { onDelete(category) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(id = R.string.delete), tint = Color.Red)
            }
        }
    }
}
