package com.android.frontend.view.pages.admin.browse

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
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
import com.android.frontend.dto.BrandDTO
import com.android.frontend.navigation.Navigation
import com.android.frontend.view_models.admin.BrandViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BrandPage(navController: NavHostController, viewModel: BrandViewModel = viewModel()) {
    val context = LocalContext.current
    val brands by viewModel.allBrands.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchAllBrands(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.brand)) },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Navigation.AddBrandPage.route)
                    }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_brand))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Navigation.AdminMenu.route) }) {
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
            item{
                Spacer(modifier = Modifier.height(50.dp))

            }
            items(brands) { brand ->
                BrandCard(brand = brand, onDelete = {
                    viewModel.deleteBrand(it.id, context)
                    viewModel.fetchAllBrands(context)
                })
            }
        }
    }
}

@Composable
fun BrandCard(brand: BrandDTO, onDelete: (BrandDTO) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = brand.name)
            IconButton(onClick = { onDelete(brand) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = stringResource(id = R.string.delete), tint = Color.Red)
            }
        }
    }
}