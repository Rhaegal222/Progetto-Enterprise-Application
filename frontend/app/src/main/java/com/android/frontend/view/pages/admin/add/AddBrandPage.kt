package com.android.frontend.view.pages.admin.add

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.android.frontend.dto.creation.BrandCreateDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.view_models.admin.BrandViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddBrandPage(navController: NavHostController, viewModel: BrandViewModel = viewModel()) {
    val context = LocalContext.current
    val brandName by viewModel.name.observeAsState("")
    val brandDescription by viewModel.description.observeAsState("")
    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.add_brand)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Navigation.BrandPage.route)  }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
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
            Spacer(modifier = Modifier.height(50.dp))

            TextField(
                value = brandName,
                onValueChange = { viewModel.name.value = it },
                label = { Text(stringResource(id = R.string.brand_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = brandDescription,
                onValueChange = { viewModel.description.value = it },
                label = { Text(stringResource(id = R.string.brand_description)) },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val brandCreateDTO = BrandCreateDTO(name = brandName, description = brandDescription)
                    viewModel.addBrand(brandCreateDTO, context)
                    showSuccessDialog = true
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(id = R.string.add_brand))
            }
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    title = { Text(stringResource(id = R.string.success)) },
                    text = { Text(stringResource(id = R.string.add_brand_successfully)) },
                    confirmButton = {
                        TextButton(onClick = {
                            showSuccessDialog = false
                            navController.navigate(Navigation.BrandPage.route)
                        }) {
                            Text(stringResource(id = R.string.okay))
                        }
                    }
                )
            }
        }
    }
}
