package com.librediet.app.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.librediet.app.data.model.Meal
import com.librediet.app.data.model.NutritionGoals
import com.librediet.app.data.model.NutritionSummary
import com.librediet.app.data.repository.MealRepository
import com.librediet.app.data.repository.NutritionGoalsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HistoryUiState(
    val isLoading: Boolean = true,
    val selectedDate: LocalDate = LocalDate.now(),
    val meals: List<Meal> = emptyList(),
    val nutritionSummary: NutritionSummary = NutritionSummary(),
    val nutritionGoals: NutritionGoals = NutritionGoals(),
    val error: String? = null
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val goalsRepository: NutritionGoalsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadDataForDate(LocalDate.now())
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            goalsRepository.getNutritionGoals().collect { goals ->
                _uiState.update { it.copy(nutritionGoals = goals ?: NutritionGoals()) }
            }
        }
    }

    private fun loadDataForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedDate = date) }
            
            mealRepository.getMealsByDateRange(date, date).collect { meals ->
                val summary = mealRepository.getNutritionSummaryForDate(date)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        meals = meals,
                        nutritionSummary = summary
                    )
                }
            }
        }
    }

    fun previousDay() {
        val newDate = _uiState.value.selectedDate.minusDays(1)
        loadDataForDate(newDate)
    }

    fun nextDay() {
        val currentDate = _uiState.value.selectedDate
        if (currentDate < LocalDate.now()) {
            loadDataForDate(currentDate.plusDays(1))
        }
    }

    fun goToToday() {
        loadDataForDate(LocalDate.now())
    }

    fun selectDate(date: LocalDate) {
        if (date <= LocalDate.now()) {
            loadDataForDate(date)
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.deleteMeal(meal)
        }
    }
}
