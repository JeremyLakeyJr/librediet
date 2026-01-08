package com.librediet.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Represents a food item with its nutritional information.
 */
@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String? = null,
    val barcode: String? = null,
    val servingSize: Float = 100f,
    val servingUnit: String = "g",
    val calories: Float = 0f,
    val protein: Float = 0f,
    val carbohydrates: Float = 0f,
    val fat: Float = 0f,
    val fiber: Float = 0f,
    val sugar: Float = 0f,
    val sodium: Float = 0f,
    val saturatedFat: Float = 0f,
    val cholesterol: Float = 0f,
    val potassium: Float = 0f,
    val vitaminA: Float = 0f,
    val vitaminC: Float = 0f,
    val calcium: Float = 0f,
    val iron: Float = 0f,
    val imageUrl: String? = null,
    val isCustom: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Categories for meal types throughout the day.
 */
enum class MealCategory(val displayName: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    SNACK("Snack")
}

/**
 * Represents a logged meal entry.
 */
@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val foodItemId: Long,
    val foodName: String,
    val category: MealCategory,
    val quantity: Float,
    val unit: String,
    val calories: Float,
    val protein: Float,
    val carbohydrates: Float,
    val fat: Float,
    val fiber: Float,
    val sugar: Float,
    val sodium: Float,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val notes: String? = null
)

/**
 * Represents a meal template for quick meal entry.
 */
@Entity(tableName = "meal_templates")
data class MealTemplate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val category: MealCategory,
    val items: String, // JSON serialized list of template items
    val isFavorite: Boolean = false,
    val usageCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUsedAt: LocalDateTime? = null
)

/**
 * Data class for meal template items (serialized to JSON).
 */
data class TemplateItem(
    val foodItemId: Long,
    val foodName: String,
    val quantity: Float,
    val unit: String
)

/**
 * Aggregated nutrition summary for a time period.
 */
data class NutritionSummary(
    val totalCalories: Float = 0f,
    val totalProtein: Float = 0f,
    val totalCarbohydrates: Float = 0f,
    val totalFat: Float = 0f,
    val totalFiber: Float = 0f,
    val totalSugar: Float = 0f,
    val totalSodium: Float = 0f,
    val mealCount: Int = 0
)

/**
 * Daily nutrition goals set by the user.
 */
@Entity(tableName = "nutrition_goals")
data class NutritionGoals(
    @PrimaryKey
    val id: Int = 1, // Single row for user goals
    val dailyCalories: Float = 2000f,
    val dailyProtein: Float = 50f,
    val dailyCarbohydrates: Float = 250f,
    val dailyFat: Float = 65f,
    val dailyFiber: Float = 25f,
    val dailySugar: Float = 50f,
    val dailySodium: Float = 2300f
)

/**
 * Search result from the nutritional database API.
 */
data class FoodSearchResult(
    val items: List<FoodItem>,
    val totalCount: Int,
    val page: Int,
    val pageSize: Int
)

/**
 * Export options for generating reports.
 */
data class ExportOptions(
    val format: ExportFormat,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val includeNutritionSummary: Boolean = true,
    val includeMealDetails: Boolean = true,
    val groupByDay: Boolean = true
)

enum class ExportFormat {
    CSV,
    PDF
}
