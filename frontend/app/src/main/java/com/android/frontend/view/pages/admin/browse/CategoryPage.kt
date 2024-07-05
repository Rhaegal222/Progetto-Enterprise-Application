package com.android.frontend.view.pages.admin.browse

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.android.frontend.config.getCurrentStackTrace
import com.android.frontend.dto.CategoryDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.view.component.ErrorDialog
import com.android.frontend.view_models.admin.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CategoryPage(navController: NavHostController, viewModel: CategoryViewModel = viewModel()) {
    val context = LocalContext.current
    val categories by viewModel.allCategories.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val hasError by viewModel.hasError.observeAsState(false)

    LaunchedEffect(Unit) {
        Log.d("DEBUG", "${getCurrentStackTrace()} Loading categories")
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
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (hasError) {
            ErrorDialog(
                title = stringResource(id = R.string.fetching_error),
                onDismiss = { navController.popBackStack() },
                onRetry = { viewModel.fetchAllCategories(context) },
                errorMessage = stringResource(id = R.string.categories_load_failed)
            )
        } else {
            CategoryContent(navController, categories, viewModel)
        }
    }
}

@Composable
fun CategoryContent(navController: NavHostController, categories: List<CategoryDTO>, viewModel: CategoryViewModel) {
    val context = LocalContext.current
    Column {
        categories.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp, 0.dp, 20.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(70.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(it) { category ->
                        CategoryCard(category, onDelete = {
                            viewModel.deleteCategory(it.id, context)
                            viewModel.fetchAllCategories(context)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: CategoryDTO, onDelete: (CategoryDTO) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = category.name, modifier = Modifier.weight(1f))
            IconButton(
                onClick = { onDelete(category) },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.delete),
                    tint = Color.Red
                )
            }
        }
    }
}