package com.example.power.data.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.power.data.repository.InfoRepository
import com.example.power.data.room.HistoryItem
import com.example.power.data.room.Info
import com.example.power.data.room.Plan
import kotlinx.coroutines.launch

class InfoViewModel(private val infoRepository: InfoRepository): ViewModel() {
    var infoUiState by mutableStateOf(Info())
        private set
    init {
        viewModelScope.launch {
            infoUiState = infoRepository.getInfo()
        }
    }
    fun updateInfo(
        username: String = infoUiState.username,
        currentPlan: Plan? = infoUiState.currentPlan,
        workoutHistory: List<HistoryItem> = infoUiState.workoutHistory,
        planHistory: List<HistoryItem> = infoUiState.planHistory
    ) {
        viewModelScope.launch{
            infoRepository.updateInfo(
                infoUiState.copy(
                    username = username,
                    currentPlan = currentPlan,
                    workoutHistory = workoutHistory,
                    planHistory = planHistory
                )
            )
            infoUiState = infoRepository.getInfo()
        }
    }
}