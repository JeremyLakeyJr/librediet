package com.librediet.app.data.repository

import com.librediet.app.data.local.FoodItemDao
import com.librediet.app.data.local.MealDao
import com.librediet.app.data.local.MealTemplateDao
import com.librediet.app.data.local.NutritionGoalsDao
import com.librediet.app.data.model.FoodItem
import com.librediet.app.data.model.FoodSearchResult
import com.librediet.app.data.model.Meal
import com.librediet.app.data.model.MealCategory
import com.librediet.app.data.model.MealTemplate
import com.librediet.app.data.model.NutritionGoals
import com.librediet.app.data.model.NutritionSummary
import com.librediet.app.data.remote.OpenFoodFactsApi
import com.librediet.app.data.remote.toFoodItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepository @Inject constructor(
    private val foodItemDao: FoodItemDao,
    private val api: OpenFoodFactsApi
) {
    fun getAllFoodItems(): Flow<List<FoodItem>> = foodItemDao.getAllFoodItems()

    fun getCustomFoodItems(): Flow<List<FoodItem>> = foodItemDao.getCustomFoodItems()

    suspend fun getFoodItemById(id: Long): FoodItem? = foodItemDao.getFoodItemById(id)

    suspend fun getFoodItemByBarcode(barcode: String): FoodItem? {
        // First check local database
        val localItem = foodItemDao.getFoodItemByBarcode(barcode)
        if (localItem != null) return localItem

        // If not found locally, fetch from API
        return try {
            val response = api.getProductByBarcode(barcode)
            if (response.isSuccessful && response.body()?.status == 1) {
                response.body()?.product?.toFoodItem()?.also { foodItem ->
                    // Cache the result locally
                    foodItemDao.insertFoodItem(foodItem)
                }
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun searchFoodItems(query: String): FoodSearchResult {
        // First search local database
        val localResults = foodItemDao.searchFoodItems(query, 20)

        // Then search API
        val apiResults = try {
            val response = api.searchProducts(query)
            if (response.isSuccessful) {
                response.body()?.products?.mapNotNull { it.toFoodItem() } ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        // Combine results, preferring local items
        val combinedResults = (localResults + apiResults)
            .distinctBy { it.barcode ?: it.name }
            .take(50)

        return FoodSearchResult(
            items = combinedResults,
            totalCount = combinedResults.size,
            page = 1,
            pageSize = 50
        )
    }

    suspend fun insertFoodItem(foodItem: FoodItem): Long = foodItemDao.insertFoodItem(foodItem)

    suspend fun updateFoodItem(foodItem: FoodItem) = foodItemDao.updateFoodItem(foodItem)

    suspend fun deleteFoodItem(foodItem: FoodItem) = foodItemDao.deleteFoodItem(foodItem)
}

@Singleton
class MealRepository @Inject constructor(
    private val mealDao: MealDao
) {
    fun getAllMeals(): Flow<List<Meal>> = mealDao.getAllMeals()

    suspend fun getMealById(id: Long): Meal? = mealDao.getMealById(id)

    fun getMealsForToday(): Flow<List<Meal>> {
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay()
        val endOfDay = today.plusDays(1).atStartOfDay()
        return mealDao.getMealsByDateRange(startOfDay, endOfDay)
    }

    fun getMealsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Meal>> {
        return mealDao.getMealsByDateRange(
            startDate.atStartOfDay(),
            endDate.plusDays(1).atStartOfDay()
        )
    }

    suspend fun getMealsByDateRangeSync(startDate: LocalDateTime, endDate: LocalDateTime): List<Meal> {
        return mealDao.getMealsByDateRangeSync(startDate, endDate)
    }

    fun getMealsByCategory(category: MealCategory, date: LocalDate): Flow<List<Meal>> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.plusDays(1).atStartOfDay()
        return mealDao.getMealsByCategory(category, startOfDay, endOfDay)
    }

    suspend fun getRecentMeals(limit: Int = 10): List<Meal> = mealDao.getRecentMeals(limit)

    suspend fun getNutritionSummaryForToday(): NutritionSummary {
        val today = LocalDate.now()
        return getNutritionSummaryForDate(today)
    }

    suspend fun getNutritionSummaryForDate(date: LocalDate): NutritionSummary {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.plusDays(1).atStartOfDay()
        val result = mealDao.getNutritionSummary(startOfDay, endOfDay)
        return NutritionSummary(
            totalCalories = result.totalCalories ?: 0f,
            totalProtein = result.totalProtein ?: 0f,
            totalCarbohydrates = result.totalCarbohydrates ?: 0f,
            totalFat = result.totalFat ?: 0f,
            totalFiber = result.totalFiber ?: 0f,
            totalSugar = result.totalSugar ?: 0f,
            totalSodium = result.totalSodium ?: 0f,
            mealCount = result.mealCount
        )
    }

    suspend fun getNutritionSummaryForRange(startDate: LocalDateTime, endDate: LocalDateTime): NutritionSummary {
        val result = mealDao.getNutritionSummary(startDate, endDate)
        return NutritionSummary(
            totalCalories = result.totalCalories ?: 0f,
            totalProtein = result.totalProtein ?: 0f,
            totalCarbohydrates = result.totalCarbohydrates ?: 0f,
            totalFat = result.totalFat ?: 0f,
            totalFiber = result.totalFiber ?: 0f,
            totalSugar = result.totalSugar ?: 0f,
            totalSodium = result.totalSodium ?: 0f,
            mealCount = result.mealCount
        )
    }

    suspend fun insertMeal(meal: Meal): Long = mealDao.insertMeal(meal)

    suspend fun updateMeal(meal: Meal) = mealDao.updateMeal(meal)

    suspend fun deleteMeal(meal: Meal) = mealDao.deleteMeal(meal)

    suspend fun deleteAllMeals() = mealDao.deleteAllMeals()
}

@Singleton
class MealTemplateRepository @Inject constructor(
    private val templateDao: MealTemplateDao
) {
    fun getAllTemplates(): Flow<List<MealTemplate>> = templateDao.getAllTemplates()

    fun getFavoriteTemplates(): Flow<List<MealTemplate>> = templateDao.getFavoriteTemplates()

    fun getTemplatesByCategory(category: MealCategory): Flow<List<MealTemplate>> =
        templateDao.getTemplatesByCategory(category)

    suspend fun getTemplateById(id: Long): MealTemplate? = templateDao.getTemplateById(id)

    suspend fun searchTemplates(query: String): List<MealTemplate> =
        templateDao.searchTemplates(query)

    suspend fun insertTemplate(template: MealTemplate): Long = templateDao.insertTemplate(template)

    suspend fun updateTemplate(template: MealTemplate) = templateDao.updateTemplate(template)

    suspend fun useTemplate(id: Long) = templateDao.incrementUsageCount(id)

    suspend fun deleteTemplate(template: MealTemplate) = templateDao.deleteTemplate(template)
}

@Singleton
class NutritionGoalsRepository @Inject constructor(
    private val goalsDao: NutritionGoalsDao
) {
    fun getNutritionGoals(): Flow<NutritionGoals?> = goalsDao.getNutritionGoals()

    suspend fun getNutritionGoalsSync(): NutritionGoals = 
        goalsDao.getNutritionGoalsSync() ?: NutritionGoals()

    suspend fun saveNutritionGoals(goals: NutritionGoals) = goalsDao.insertOrUpdateGoals(goals)
}
