package com.librediet.app.ui.screens.export

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.librediet.app.data.model.ExportFormat
import com.librediet.app.data.model.ExportOptions
import com.librediet.app.data.repository.MealRepository
import com.librediet.app.util.ExportService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

enum class DateRangePreset(val displayName: String) {
    TODAY("Today"),
    WEEK("This Week"),
    MONTH("This Month"),
    CUSTOM("Custom")
}

data class ExportUiState(
    val isExporting: Boolean = false,
    val selectedPreset: DateRangePreset = DateRangePreset.WEEK,
    val startDate: LocalDate = LocalDate.now().minusWeeks(1),
    val endDate: LocalDate = LocalDate.now(),
    val selectedFormat: ExportFormat = ExportFormat.CSV,
    val includeNutritionSummary: Boolean = true,
    val includeMealDetails: Boolean = true,
    val groupByDay: Boolean = true,
    val mealCount: Int = 0,
    val exportUri: Uri? = null,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val exportService: ExportService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    init {
        loadMealCount()
    }

    private fun loadMealCount() {
        viewModelScope.launch {
            val state = _uiState.value
            val startDateTime = state.startDate.atStartOfDay()
            val endDateTime = state.endDate.plusDays(1).atStartOfDay()
            val meals = mealRepository.getMealsByDateRangeSync(startDateTime, endDateTime)
            _uiState.update { it.copy(mealCount = meals.size) }
        }
    }

    fun selectDatePreset(preset: DateRangePreset) {
        val today = LocalDate.now()
        val (start, end) = when (preset) {
            DateRangePreset.TODAY -> Pair(today, today)
            DateRangePreset.WEEK -> Pair(today.minusWeeks(1), today)
            DateRangePreset.MONTH -> Pair(today.minusMonths(1), today)
            DateRangePreset.CUSTOM -> return // Handle custom separately
        }
        _uiState.update {
            it.copy(
                selectedPreset = preset,
                startDate = start,
                endDate = end
            )
        }
        loadMealCount()
    }

    fun selectFormat(format: ExportFormat) {
        _uiState.update { it.copy(selectedFormat = format) }
    }

    fun toggleNutritionSummary() {
        _uiState.update { it.copy(includeNutritionSummary = !it.includeNutritionSummary) }
    }

    fun toggleMealDetails() {
        _uiState.update { it.copy(includeMealDetails = !it.includeMealDetails) }
    }

    fun toggleGroupByDay() {
        _uiState.update { it.copy(groupByDay = !it.groupByDay) }
    }

    fun exportData(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, error = null) }

            try {
                val state = _uiState.value
                val startDateTime = state.startDate.atStartOfDay()
                val endDateTime = state.endDate.atTime(LocalTime.MAX)

                val meals = mealRepository.getMealsByDateRangeSync(startDateTime, endDateTime)
                val summary = mealRepository.getNutritionSummaryForRange(startDateTime, endDateTime)

                val options = ExportOptions(
                    format = state.selectedFormat,
                    startDate = startDateTime,
                    endDate = endDateTime,
                    includeNutritionSummary = state.includeNutritionSummary,
                    includeMealDetails = state.includeMealDetails,
                    groupByDay = state.groupByDay
                )

                val uri = exportService.exportMeals(context, meals, summary, options)

                if (uri != null) {
                    _uiState.update {
                        it.copy(
                            isExporting = false,
                            exportUri = uri,
                            successMessage = "Export created successfully"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isExporting = false,
                            error = "Failed to create export file"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        error = "Export failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun shareExport(context: Context) {
        exportData(context)
    }

    fun clearExportUri() {
        _uiState.update { it.copy(exportUri = null) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
