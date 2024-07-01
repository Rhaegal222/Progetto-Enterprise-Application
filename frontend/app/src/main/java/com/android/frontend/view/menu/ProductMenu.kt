package com.android.frontend.view.menu

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

