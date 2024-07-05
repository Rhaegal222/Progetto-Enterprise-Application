package com.android.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.android.frontend.view.pages.admin.add.AddBrandPage
import com.android.frontend.view.pages.admin.add.AddCategoryPage
import com.android.frontend.view.pages.admin.add.AddProductPage
import com.android.frontend.view.pages.admin.menu.AdminMenu
import com.android.frontend.view.pages.admin.browse.BrandPage
import com.android.frontend.view.pages.admin.browse.CategoryPage
import com.android.frontend.view.pages.admin.browse.EditProductPage
import com.android.frontend.view.pages.admin.browse.ProductPage
import com.android.frontend.view.pages.admin.browse.UserPage
import com.android.frontend.view.pages.user.details.ProductDetailsPage

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
        composable(
            "${Navigation.EditProductPage}/{productId}",
            arguments = listOf(navArgument("productId") { type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            EditProductPage(navController = navController, productId = productId)
        }
    }
}
