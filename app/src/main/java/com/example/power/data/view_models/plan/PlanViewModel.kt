package com.example.power.data.view_models.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.power.data.repository.PlanRepository
import com.example.power.data.room.Plan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlanViewModel(private val workoutRepository: PlanRepository) : ViewModel() {
    private val _plans = MutableStateFlow<List<Plan>>(emptyList())
    val workouts: StateFlow<List<Plan>> get() = _plans
    init {
        viewModelScope.launch {
            workoutRepository.getAllPlansStream().collect {
                _plans.emit(it)
            }
        }
    }
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
    fun onDelete(planName: String) {
        viewModelScope.launch {
            val plan = workoutRepository.getPlanByName(planName)
            if (plan != null)
                workoutRepository.deletePlan(plan)
        }
    }
}