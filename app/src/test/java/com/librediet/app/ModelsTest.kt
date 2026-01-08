package com.librediet.app

import com.librediet.app.data.model.FoodItem
import com.librediet.app.data.model.Meal
import com.librediet.app.data.model.MealCategory
import com.librediet.app.data.model.NutritionGoals
import com.librediet.app.data.model.NutritionSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.LocalDateTime

/**
 * Unit tests for data models.
 */
class ModelsTest {

    @Test
    fun `FoodItem default values are correct`() {
        val foodItem = FoodItem(
            name = "Test Food"
        )
        
        assertEquals("Test Food", foodItem.name)
        assertEquals(0L, foodItem.id)
        assertEquals(100f, foodItem.servingSize, 0.01f)
        assertEquals("g", foodItem.servingUnit)
        assertEquals(0f, foodItem.calories, 0.01f)
        assertEquals(false, foodItem.isCustom)
    }

    @Test
    fun `FoodItem with nutritional values`() {
        val foodItem = FoodItem(
            id = 1,
            name = "Chicken Breast",
            brand = "Generic",
            calories = 165f,
            protein = 31f,
            carbohydrates = 0f,
            fat = 3.6f,
            servingSize = 100f
        )
        
        assertEquals(165f, foodItem.calories, 0.01f)
        assertEquals(31f, foodItem.protein, 0.01f)
        assertEquals(0f, foodItem.carbohydrates, 0.01f)
        assertEquals(3.6f, foodItem.fat, 0.01f)
    }

    @Test
    fun `MealCategory has correct display names`() {
        assertEquals("Breakfast", MealCategory.BREAKFAST.displayName)
        assertEquals("Lunch", MealCategory.LUNCH.displayName)
        assertEquals("Dinner", MealCategory.DINNER.displayName)
        assertEquals("Snack", MealCategory.SNACK.displayName)
    }

    @Test
    fun `Meal creation with all fields`() {
        val timestamp = LocalDateTime.now()
        val meal = Meal(
            id = 1,
            foodItemId = 100,
            foodName = "Grilled Chicken",
            category = MealCategory.LUNCH,
            quantity = 1.5f,
            unit = "serving",
            calories = 247.5f,
            protein = 46.5f,
            carbohydrates = 0f,
            fat = 5.4f,
            fiber = 0f,
            sugar = 0f,
            sodium = 74f,
            timestamp = timestamp,
            notes = "With salad"
        )
        
        assertEquals(1L, meal.id)
        assertEquals("Grilled Chicken", meal.foodName)
        assertEquals(MealCategory.LUNCH, meal.category)
        assertEquals(1.5f, meal.quantity, 0.01f)
        assertEquals(247.5f, meal.calories, 0.01f)
        assertEquals("With salad", meal.notes)
    }

    @Test
    fun `NutritionSummary aggregation`() {
        val summary = NutritionSummary(
            totalCalories = 1500f,
            totalProtein = 75f,
            totalCarbohydrates = 150f,
            totalFat = 50f,
            totalFiber = 20f,
            totalSugar = 30f,
            totalSodium = 1500f,
            mealCount = 3
        )
        
        assertEquals(1500f, summary.totalCalories, 0.01f)
        assertEquals(3, summary.mealCount)
    }

    @Test
    fun `NutritionGoals default values`() {
        val goals = NutritionGoals()
        
        assertEquals(2000f, goals.dailyCalories, 0.01f)
        assertEquals(50f, goals.dailyProtein, 0.01f)
        assertEquals(250f, goals.dailyCarbohydrates, 0.01f)
        assertEquals(65f, goals.dailyFat, 0.01f)
        assertEquals(25f, goals.dailyFiber, 0.01f)
        assertEquals(50f, goals.dailySugar, 0.01f)
        assertEquals(2300f, goals.dailySodium, 0.01f)
    }

    @Test
    fun `NutritionGoals custom values`() {
        val goals = NutritionGoals(
            dailyCalories = 2500f,
            dailyProtein = 100f,
            dailyCarbohydrates = 300f,
            dailyFat = 80f
        )
        
        assertEquals(2500f, goals.dailyCalories, 0.01f)
        assertEquals(100f, goals.dailyProtein, 0.01f)
    }

    @Test
    fun `Calculate remaining calories`() {
        val goals = NutritionGoals(dailyCalories = 2000f)
        val consumed = 1500f
        val remaining = goals.dailyCalories - consumed
        
        assertEquals(500f, remaining, 0.01f)
    }

    @Test
    fun `Calculate progress percentage`() {
        val goals = NutritionGoals(dailyCalories = 2000f)
        val consumed = 1500f
        val progress = (consumed / goals.dailyCalories) * 100
        
        assertEquals(75f, progress, 0.01f)
    }
}
