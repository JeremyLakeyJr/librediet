package com.librediet.app

import com.librediet.app.data.remote.Nutriments
import com.librediet.app.data.remote.Product
import com.librediet.app.data.remote.toFoodItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for API response parsing.
 */
class ApiParsingTest {

    @Test
    fun `Product with complete data converts to FoodItem`() {
        val nutriments = Nutriments(
            energy_kcal_100g = 250f,
            proteins_100g = 10f,
            carbohydrates_100g = 30f,
            fat_100g = 12f,
            fiber_100g = 3f,
            sugars_100g = 5f,
            sodium_100g = 0.5f,
            energy_kcal_serving = null,
            proteins_serving = null,
            carbohydrates_serving = null,
            fat_serving = null,
            fiber_serving = null,
            sugars_serving = null,
            sodium_serving = null,
            saturated_fat_100g = 4f,
            cholesterol_100g = 30f,
            potassium_100g = 200f,
            vitamin_a_100g = 100f,
            vitamin_c_100g = 10f,
            calcium_100g = 50f,
            iron_100g = 2f
        )
        
        val product = Product(
            code = "1234567890123",
            product_name = "Test Product",
            brands = "Test Brand",
            serving_size = "100g",
            nutriments = nutriments,
            image_url = "https://example.com/image.jpg"
        )
        
        val foodItem = product.toFoodItem()
        
        assertNotNull(foodItem)
        assertEquals("Test Product", foodItem?.name)
        assertEquals("Test Brand", foodItem?.brand)
        assertEquals("1234567890123", foodItem?.barcode)
        assertEquals(250f, foodItem?.calories ?: 0f, 0.01f)
        assertEquals(10f, foodItem?.protein ?: 0f, 0.01f)
        assertEquals(30f, foodItem?.carbohydrates ?: 0f, 0.01f)
        assertEquals(12f, foodItem?.fat ?: 0f, 0.01f)
        assertEquals(3f, foodItem?.fiber ?: 0f, 0.01f)
        assertEquals(5f, foodItem?.sugar ?: 0f, 0.01f)
        assertEquals(500f, foodItem?.sodium ?: 0f, 0.01f) // 0.5g * 1000 = 500mg
    }

    @Test
    fun `Product without name returns null FoodItem`() {
        val product = Product(
            code = "1234567890123",
            product_name = null,
            brands = "Test Brand",
            serving_size = "100g",
            nutriments = null,
            image_url = null
        )
        
        val foodItem = product.toFoodItem()
        
        assertNull(foodItem)
    }

    @Test
    fun `Product with missing nutriments has zero values`() {
        val product = Product(
            code = "1234567890123",
            product_name = "Test Product",
            brands = null,
            serving_size = null,
            nutriments = null,
            image_url = null
        )
        
        val foodItem = product.toFoodItem()
        
        assertNotNull(foodItem)
        assertEquals(0f, foodItem?.calories ?: -1f, 0.01f)
        assertEquals(0f, foodItem?.protein ?: -1f, 0.01f)
        assertEquals(0f, foodItem?.carbohydrates ?: -1f, 0.01f)
        assertEquals(0f, foodItem?.fat ?: -1f, 0.01f)
    }

    @Test
    fun `Product with partial nutriments handles null values`() {
        val nutriments = Nutriments(
            energy_kcal_100g = 100f,
            proteins_100g = null,
            carbohydrates_100g = 20f,
            fat_100g = null,
            fiber_100g = null,
            sugars_100g = null,
            sodium_100g = null,
            energy_kcal_serving = null,
            proteins_serving = null,
            carbohydrates_serving = null,
            fat_serving = null,
            fiber_serving = null,
            sugars_serving = null,
            sodium_serving = null,
            saturated_fat_100g = null,
            cholesterol_100g = null,
            potassium_100g = null,
            vitamin_a_100g = null,
            vitamin_c_100g = null,
            calcium_100g = null,
            iron_100g = null
        )
        
        val product = Product(
            code = "1234567890123",
            product_name = "Partial Product",
            brands = null,
            serving_size = null,
            nutriments = nutriments,
            image_url = null
        )
        
        val foodItem = product.toFoodItem()
        
        assertNotNull(foodItem)
        assertEquals(100f, foodItem?.calories ?: -1f, 0.01f)
        assertEquals(0f, foodItem?.protein ?: -1f, 0.01f)
        assertEquals(20f, foodItem?.carbohydrates ?: -1f, 0.01f)
        assertEquals(0f, foodItem?.fat ?: -1f, 0.01f)
    }
}
