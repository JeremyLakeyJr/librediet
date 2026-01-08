package com.librediet.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.librediet.app.data.model.NutritionGoals
import com.librediet.app.data.repository.NutritionGoalsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NutritionGoalsUiState(
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val calories: String = "2000",
    val protein: String = "50",
    val carbs: String = "250",
    val fat: String = "65",
    val fiber: String = "25",
    val sugar: String = "50",
    val sodium: String = "2300"
)

@HiltViewModel
class NutritionGoalsViewModel @Inject constructor(
    private val repository: NutritionGoalsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionGoalsUiState())
    val uiState: StateFlow<NutritionGoalsUiState> = _uiState.asStateFlow()

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            repository.getNutritionGoals().collect { goals ->
                goals?.let {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            calories = it.dailyCalories.toInt().toString(),
                            protein = it.dailyProtein.toInt().toString(),
                            carbs = it.dailyCarbohydrates.toInt().toString(),
                            fat = it.dailyFat.toInt().toString(),
                            fiber = it.dailyFiber.toInt().toString(),
                            sugar = it.dailySugar.toInt().toString(),
                            sodium = it.dailySodium.toInt().toString()
                        )
                    }
                } ?: run {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun updateCalories(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.update { it.copy(calories = value) }
        }
    }

    fun updateProtein(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.update { it.copy(protein = value) }
        }
    }

    fun updateCarbs(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.update { it.copy(carbs = value) }
        }
    }

    fun updateFat(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.update { it.copy(fat = value) }
        }
    }

    fun updateFiber(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.update { it.copy(fiber = value) }
        }
    }

    fun updateSugar(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.update { it.copy(sugar = value) }
        }
    }

    fun updateSodium(value: String) {
        if (value.all { it.isDigit() }) {
            _uiState.update { it.copy(sodium = value) }
        }
    }

    fun saveGoals() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val state = _uiState.value
            val goals = NutritionGoals(
                dailyCalories = state.calories.toFloatOrNull() ?: 2000f,
                dailyProtein = state.protein.toFloatOrNull() ?: 50f,
                dailyCarbohydrates = state.carbs.toFloatOrNull() ?: 250f,
                dailyFat = state.fat.toFloatOrNull() ?: 65f,
                dailyFiber = state.fiber.toFloatOrNull() ?: 25f,
                dailySugar = state.sugar.toFloatOrNull() ?: 50f,
                dailySodium = state.sodium.toFloatOrNull() ?: 2300f
            )
            
            repository.saveNutritionGoals(goals)
            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}
