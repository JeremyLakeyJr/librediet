package com.librediet.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.librediet.app.ui.screens.export.ExportScreen
import com.librediet.app.ui.screens.foodsearch.BarcodeScannerScreen
import com.librediet.app.ui.screens.foodsearch.FoodSearchScreen
import com.librediet.app.ui.screens.history.HistoryScreen
import com.librediet.app.ui.screens.home.HomeScreen
import com.librediet.app.ui.screens.mealentry.MealEntryScreen
import com.librediet.app.ui.screens.settings.NutritionGoalsScreen
import com.librediet.app.ui.screens.settings.SettingsScreen

@Composable
fun LibreDietNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToMealEntry = { navController.navigate(Screen.MealEntry.route) },
                onNavigateToFoodSearch = { navController.navigate(Screen.FoodSearch.createRoute()) },
                onNavigateToBarcode = { navController.navigate(Screen.BarcodeScanner.route) }
            )
        }

        composable(Screen.MealEntry.route) {
            MealEntryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToFoodSearch = { navController.navigate(Screen.FoodSearch.createRoute()) },
                onNavigateToBarcode = { navController.navigate(Screen.BarcodeScanner.route) },
                onNavigateToTemplates = { navController.navigate(Screen.MealTemplates.route) }
            )
        }

        composable(
            route = Screen.FoodSearch.route,
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            FoodSearchScreen(
                initialQuery = query,
                onNavigateBack = { navController.popBackStack() },
                onFoodSelected = { foodId ->
                    // Navigate to meal entry with selected food
                    navController.previousBackStackEntry?.savedStateHandle?.set("selectedFoodId", foodId)
                    navController.popBackStack()
                },
                onNavigateToBarcode = { navController.navigate(Screen.BarcodeScanner.route) }
            )
        }

        composable(Screen.BarcodeScanner.route) {
            BarcodeScannerScreen(
                onNavigateBack = { navController.popBackStack() },
                onBarcodeScanned = { barcode ->
                    // Navigate to food search with barcode result
                    navController.previousBackStackEntry?.savedStateHandle?.set("scannedBarcode", barcode)
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onNavigateToMealDetail = { mealId ->
                    navController.navigate(Screen.MealDetail.createRoute(mealId))
                }
            )
        }

        composable(Screen.Export.route) {
            ExportScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToNutritionGoals = { navController.navigate(Screen.NutritionGoals.route) }
            )
        }

        composable(Screen.NutritionGoals.route) {
            NutritionGoalsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MealTemplates.route) {
            // Meal templates screen
            MealEntryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToFoodSearch = { navController.navigate(Screen.FoodSearch.createRoute()) },
                onNavigateToBarcode = { navController.navigate(Screen.BarcodeScanner.route) },
                onNavigateToTemplates = { }
            )
        }

        composable(
            route = Screen.MealDetail.route,
            arguments = listOf(
                navArgument("mealId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getLong("mealId") ?: 0L
            // Meal detail screen - for now show history
            HistoryScreen(
                onNavigateToMealDetail = { }
            )
        }
    }
}
