package com.android.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.android.frontend.view.admin.AddBrandPage
import com.android.frontend.view.admin.AddCategoryPage
import com.android.frontend.view.admin.AddProductPage
import com.android.frontend.view.admin.AdminMenu
import com.android.frontend.view.admin.BrandPage
import com.android.frontend.view.admin.CategoryPage
import com.android.frontend.view.admin.ProductPage
import com.android.frontend.view.admin.UserPage

@Composable
fun AdminGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Navigation.AdminMenu.route) {
        composable(Navigation.AdminMenu.route) { AdminMenu(navController) }
        composable(Navigation.AddProductPage.route) { AddProductPage(navController) }
        composable(Navigation.AddCategoryPage.route) { AddCategoryPage(navController) }
        composable(Navigation.AddBrandPage.route) { AddBrandPage(navController) }
        composable(Navigation.CategoryPage.route) { CategoryPage(navController) }
        composable(Navigation.BrandPage.route) { BrandPage(navController) }
        composable(Navigation.ProductPage.route) { ProductPage(navController) }
        composable(Navigation.UserPage.route) { UserPage(navController) }
    }
}
