package com.librediet.app.ui.screens.home

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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val nutritionSummary: NutritionSummary = NutritionSummary(),
    val nutritionGoals: NutritionGoals = NutritionGoals(),
    val todaysMeals: List<Meal> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val goalsRepository: NutritionGoalsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                mealRepository.getMealsForToday(),
                goalsRepository.getNutritionGoals()
            ) { meals, goals ->
                Pair(meals, goals)
            }.collect { (meals, goals) ->
                val summary = mealRepository.getNutritionSummaryForToday()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        todaysMeals = meals,
                        nutritionSummary = summary,
                        nutritionGoals = goals ?: NutritionGoals()
                    )
                }
            }
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.deleteMeal(meal)
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadData()
    }
}
