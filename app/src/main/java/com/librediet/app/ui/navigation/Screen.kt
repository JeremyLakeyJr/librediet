package com.librediet.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    data object Home : Screen(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object MealEntry : Screen(
        route = "meal_entry",
        title = "Add Meal",
        selectedIcon = Icons.Filled.Add,
        unselectedIcon = Icons.Outlined.Add
    )

    data object FoodSearch : Screen(
        route = "food_search?query={query}",
        title = "Search Food"
    ) {
        fun createRoute(query: String = "") = "food_search?query=$query"
    }

    data object BarcodeScanner : Screen(
        route = "barcode_scanner",
        title = "Scan Barcode"
    )

    data object History : Screen(
        route = "history",
        title = "History",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    )

    data object Export : Screen(
        route = "export",
        title = "Export",
        selectedIcon = Icons.Filled.FileDownload,
        unselectedIcon = Icons.Outlined.FileDownload
    )

    data object Settings : Screen(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    data object MealDetail : Screen(
        route = "meal_detail/{mealId}",
        title = "Meal Details"
    ) {
        fun createRoute(mealId: Long) = "meal_detail/$mealId"
    }

    data object FoodDetail : Screen(
        route = "food_detail/{foodId}",
        title = "Food Details"
    ) {
        fun createRoute(foodId: Long) = "food_detail/$foodId"
    }

    data object MealTemplates : Screen(
        route = "meal_templates",
        title = "Meal Templates"
    )

    data object NutritionGoals : Screen(
        route = "nutrition_goals",
        title = "Nutrition Goals"
    )
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.History,
    Screen.MealEntry,
    Screen.Export,
    Screen.Settings
)
