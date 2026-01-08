package com.librediet.app.ui.screens.mealentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.librediet.app.data.model.FoodItem
import com.librediet.app.data.model.Meal
import com.librediet.app.data.model.MealCategory
import com.librediet.app.data.repository.FoodRepository
import com.librediet.app.data.repository.MealRepository
import com.librediet.app.util.VoiceInputService
import com.librediet.app.util.VoiceInputState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class MealEntryUiState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val foodName: String = "",
    val quantity: String = "1",
    val unit: String = "serving",
    val category: MealCategory = MealCategory.LUNCH,
    val calories: String = "",
    val protein: String = "",
    val carbs: String = "",
    val fat: String = "",
    val fiber: String = "",
    val sugar: String = "",
    val sodium: String = "",
    val notes: String = "",
    val selectedFood: FoodItem? = null,
    val recentFoods: List<FoodItem> = emptyList(),
    val isVoiceInputActive: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MealEntryViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val foodRepository: FoodRepository,
    private val voiceInputService: VoiceInputService
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealEntryUiState())
    val uiState: StateFlow<MealEntryUiState> = _uiState.asStateFlow()

    init {
        loadRecentFoods()
        observeVoiceInput()
    }

    private fun loadRecentFoods() {
        viewModelScope.launch {
            foodRepository.getAllFoodItems().collect { foods ->
                _uiState.update { it.copy(recentFoods = foods.take(10)) }
            }
        }
    }

    private fun observeVoiceInput() {
        viewModelScope.launch {
            voiceInputService.state.collect { state ->
                when (state) {
                    is VoiceInputState.Result -> {
                        _uiState.update { 
                            it.copy(
                                foodName = state.text,
                                isVoiceInputActive = false
                            )
                        }
                        voiceInputService.reset()
                    }
                    is VoiceInputState.Error -> {
                        _uiState.update { 
                            it.copy(
                                error = state.message,
                                isVoiceInputActive = false
                            )
                        }
                        voiceInputService.reset()
                    }
                    is VoiceInputState.Listening -> {
                        _uiState.update { it.copy(isVoiceInputActive = true) }
                    }
                    is VoiceInputState.Idle -> {
                        _uiState.update { it.copy(isVoiceInputActive = false) }
                    }
                }
            }
        }
    }

    fun updateFoodName(name: String) {
        _uiState.update { it.copy(foodName = name) }
    }

    fun updateQuantity(quantity: String) {
        _uiState.update { it.copy(quantity = quantity) }
    }

    fun updateCategory(category: MealCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun updateCalories(calories: String) {
        _uiState.update { it.copy(calories = calories) }
    }

    fun updateProtein(protein: String) {
        _uiState.update { it.copy(protein = protein) }
    }

    fun updateCarbs(carbs: String) {
        _uiState.update { it.copy(carbs = carbs) }
    }

    fun updateFat(fat: String) {
        _uiState.update { it.copy(fat = fat) }
    }

    fun updateFiber(fiber: String) {
        _uiState.update { it.copy(fiber = fiber) }
    }

    fun updateSodium(sodium: String) {
        _uiState.update { it.copy(sodium = sodium) }
    }

    fun updateNotes(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun selectFood(food: FoodItem) {
        _uiState.update {
            it.copy(
                selectedFood = food,
                foodName = food.name,
                calories = food.calories.toString(),
                protein = food.protein.toString(),
                carbs = food.carbohydrates.toString(),
                fat = food.fat.toString(),
                fiber = food.fiber.toString(),
                sugar = food.sugar.toString(),
                sodium = food.sodium.toString()
            )
        }
    }

    fun toggleVoiceInput() {
        val currentState = _uiState.value
        if (currentState.isVoiceInputActive) {
            voiceInputService.stopListening()
        }
        // Voice input requires context, will be triggered from UI
    }

    fun saveMeal() {
        val state = _uiState.value
        
        if (state.foodName.isBlank()) {
            _uiState.update { it.copy(error = "Please enter a food name") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val quantity = state.quantity.toFloatOrNull() ?: 1f
                val calories = state.calories.toFloatOrNull() ?: 0f
                val protein = state.protein.toFloatOrNull() ?: 0f
                val carbs = state.carbs.toFloatOrNull() ?: 0f
                val fat = state.fat.toFloatOrNull() ?: 0f
                val fiber = state.fiber.toFloatOrNull() ?: 0f
                val sugar = state.sugar.toFloatOrNull() ?: 0f
                val sodium = state.sodium.toFloatOrNull() ?: 0f

                // Save food item if it's new
                val foodItemId = state.selectedFood?.id ?: run {
                    val newFood = FoodItem(
                        name = state.foodName,
                        calories = calories / quantity,
                        protein = protein / quantity,
                        carbohydrates = carbs / quantity,
                        fat = fat / quantity,
                        fiber = fiber / quantity,
                        sugar = sugar / quantity,
                        sodium = sodium / quantity,
                        isCustom = true
                    )
                    foodRepository.insertFoodItem(newFood)
                }

                // Create and save meal
                val meal = Meal(
                    foodItemId = foodItemId,
                    foodName = state.foodName,
                    category = state.category,
                    quantity = quantity,
                    unit = state.unit,
                    calories = calories,
                    protein = protein,
                    carbohydrates = carbs,
                    fat = fat,
                    fiber = fiber,
                    sugar = sugar,
                    sodium = sodium,
                    timestamp = LocalDateTime.now(),
                    notes = state.notes.takeIf { it.isNotBlank() }
                )

                mealRepository.insertMeal(meal)
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to save meal: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
