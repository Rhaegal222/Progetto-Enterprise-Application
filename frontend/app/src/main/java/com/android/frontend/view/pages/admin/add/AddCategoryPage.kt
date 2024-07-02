package com.android.frontend.view.pages.admin.add

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.android.frontend.R
import com.android.frontend.dto.creation.ProductCategoryCreateDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.view_models.admin.ProductCategoryBrandViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddCategoryPage(navController: NavHostController, viewModel: ProductCategoryBrandViewModel = viewModel()) {
    val context = LocalContext.current
    val categoryName by viewModel.title.observeAsState("")
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.add_category)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Navigation.CategoryPage.route)  }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = categoryName,
                onValueChange = { viewModel.title.value = it },
                label = { Text("Category Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val categoryCreateDTO = ProductCategoryCreateDTO(categoryName = categoryName)
                    viewModel.addCategory(categoryCreateDTO, context)
                    showSuccessDialog = true
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Add Category")
            }
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    title = { Text("Success") },
                    text = { Text("Category added successfully!") },
                    confirmButton = {
                        TextButton(onClick = {
                            showSuccessDialog = false
                            navController.navigate(Navigation.CategoryPage.route)
                            // Navigate back or perform other actions
                        }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}
