package com.librediet.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.librediet.app.data.model.FoodItem
import com.librediet.app.data.model.Meal
import com.librediet.app.data.model.MealCategory
import com.librediet.app.data.model.MealTemplate
import com.librediet.app.data.model.NutritionGoals
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface FoodItemDao {
    @Query("SELECT * FROM food_items ORDER BY name ASC")
    fun getAllFoodItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getFoodItemById(id: Long): FoodItem?

    @Query("SELECT * FROM food_items WHERE barcode = :barcode LIMIT 1")
    suspend fun getFoodItemByBarcode(barcode: String): FoodItem?

    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' ORDER BY name ASC LIMIT :limit")
    suspend fun searchFoodItems(query: String, limit: Int = 50): List<FoodItem>

    @Query("SELECT * FROM food_items WHERE isCustom = 1 ORDER BY createdAt DESC")
    fun getCustomFoodItems(): Flow<List<FoodItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItem(foodItem: FoodItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItems(foodItems: List<FoodItem>)

    @Update
    suspend fun updateFoodItem(foodItem: FoodItem)

    @Delete
    suspend fun deleteFoodItem(foodItem: FoodItem)

    @Query("DELETE FROM food_items WHERE isCustom = 1")
    suspend fun deleteAllCustomFoodItems()
}

@Dao
interface MealDao {
    @Query("SELECT * FROM meals ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<Meal>>

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getMealById(id: Long): Meal?

    @Query("SELECT * FROM meals WHERE timestamp >= :startDate AND timestamp < :endDate ORDER BY timestamp DESC")
    fun getMealsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Meal>>

    @Query("SELECT * FROM meals WHERE timestamp >= :startDate AND timestamp < :endDate ORDER BY timestamp DESC")
    suspend fun getMealsByDateRangeSync(startDate: LocalDateTime, endDate: LocalDateTime): List<Meal>

    @Query("SELECT * FROM meals WHERE category = :category AND timestamp >= :startDate AND timestamp < :endDate ORDER BY timestamp DESC")
    fun getMealsByCategory(category: MealCategory, startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Meal>>

    @Query("SELECT * FROM meals ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMeals(limit: Int = 10): List<Meal>

    @Query("""
        SELECT 
            SUM(calories) as totalCalories,
            SUM(protein) as totalProtein,
            SUM(carbohydrates) as totalCarbohydrates,
            SUM(fat) as totalFat,
            SUM(fiber) as totalFiber,
            SUM(sugar) as totalSugar,
            SUM(sodium) as totalSodium,
            COUNT(*) as mealCount
        FROM meals 
        WHERE timestamp >= :startDate AND timestamp < :endDate
    """)
    suspend fun getNutritionSummary(startDate: LocalDateTime, endDate: LocalDateTime): NutritionSummaryResult

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<Meal>)

    @Update
    suspend fun updateMeal(meal: Meal)

    @Delete
    suspend fun deleteMeal(meal: Meal)

    @Query("DELETE FROM meals")
    suspend fun deleteAllMeals()

    @Query("DELETE FROM meals WHERE timestamp >= :startDate AND timestamp < :endDate")
    suspend fun deleteMealsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime)
}

data class NutritionSummaryResult(
    val totalCalories: Float?,
    val totalProtein: Float?,
    val totalCarbohydrates: Float?,
    val totalFat: Float?,
    val totalFiber: Float?,
    val totalSugar: Float?,
    val totalSodium: Float?,
    val mealCount: Int
)

@Dao
interface MealTemplateDao {
    @Query("SELECT * FROM meal_templates ORDER BY usageCount DESC, lastUsedAt DESC")
    fun getAllTemplates(): Flow<List<MealTemplate>>

    @Query("SELECT * FROM meal_templates WHERE id = :id")
    suspend fun getTemplateById(id: Long): MealTemplate?

    @Query("SELECT * FROM meal_templates WHERE isFavorite = 1 ORDER BY usageCount DESC")
    fun getFavoriteTemplates(): Flow<List<MealTemplate>>

    @Query("SELECT * FROM meal_templates WHERE category = :category ORDER BY usageCount DESC")
    fun getTemplatesByCategory(category: MealCategory): Flow<List<MealTemplate>>

    @Query("SELECT * FROM meal_templates WHERE name LIKE '%' || :query || '%' ORDER BY usageCount DESC LIMIT :limit")
    suspend fun searchTemplates(query: String, limit: Int = 20): List<MealTemplate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: MealTemplate): Long

    @Update
    suspend fun updateTemplate(template: MealTemplate)

    @Query("UPDATE meal_templates SET usageCount = usageCount + 1, lastUsedAt = :timestamp WHERE id = :id")
    suspend fun incrementUsageCount(id: Long, timestamp: LocalDateTime = LocalDateTime.now())

    @Delete
    suspend fun deleteTemplate(template: MealTemplate)

    @Query("DELETE FROM meal_templates")
    suspend fun deleteAllTemplates()
}

@Dao
interface NutritionGoalsDao {
    @Query("SELECT * FROM nutrition_goals WHERE id = 1")
    fun getNutritionGoals(): Flow<NutritionGoals?>

    @Query("SELECT * FROM nutrition_goals WHERE id = 1")
    suspend fun getNutritionGoalsSync(): NutritionGoals?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGoals(goals: NutritionGoals)
}
