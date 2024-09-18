package com.example.power.data.viewmodels.plan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.power.data.repository.PlanRepository
import com.example.power.data.room.Plan
import com.example.power.data.room.PlanType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel to view delete filter and search Plans.
 */
class PlanViewModel(private val workoutRepository: PlanRepository) : ViewModel() {
    // flow to show plan
    private val _plans = MutableStateFlow<List<Plan>>(emptyList())
    // exposing the plan flow
    val plans: StateFlow<List<Plan>> get() = _plans
    // the filtered plans
    var filterParamsState by mutableStateOf(FilterParams())
        private set

    /**
     * updates the filter params
     */
    fun updateFilterParams(params: FilterParams) {
        filterParamsState = params
    }

    /**
     * collects plan changes
     */
    init {
        viewModelScope.launch {
            workoutRepository.getAllPlansStream().collect {
                _plans.emit(it)
            }
        }
    }

    // search text
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    /**
     * changes the text value
     */
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    /**
     * deletes a plan from the repository
     */
    fun onDelete(planName: String) {
        viewModelScope.launch {
            val plan = workoutRepository.getPlanByName(planName)
            if (plan != null)
                workoutRepository.deletePlan(plan)
        }
    }
}

/**
 * parameters for filtering out plans
 */
data class FilterParams (
    val minExercises: Int = 0,
    val maxExercises: Int = 100,
    val planType: PlanType = PlanType.GYM
)