package com.example.power.data.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.power.data.repository.InfoRepository
import com.example.power.data.room.HistoryItem
import com.example.power.data.room.Info
import com.example.power.data.room.Plan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class InfoViewModel(private val infoRepository: InfoRepository): ViewModel() {
    var infoUiState by mutableStateOf(Info())
        private set
    private val _info = MutableStateFlow(Info())
    var planSelected by mutableStateOf(0)
    var choosingPlan by mutableStateOf(false)
    init {
        viewModelScope.launch {
            infoRepository.getInfo().collect {
                if (it != null) {
                    _info.value = it
                    infoUiState = it // Update the mutableStateOf
                    planSelected = if (infoUiState.currentPlan != null) 2 else 1
                } else {
                    viewModelScope.launch { infoRepository.insertInfo() }
                }
            }
        }
    }
    fun deleteInfo() {
        viewModelScope.launch {
            infoRepository.deleteInfo()
        }
    }
    fun updateInfo(info: Info) {
        info.date = Date()
        viewModelScope.launch{
            infoRepository.updateInfo(info)
            infoUiState = info
        }
    }

    fun completeWorkout(index: Int) {
        val workoutNum = infoUiState.currentPlan?.weeksList?.get(index)?.numOfWorkoutsDone
        if (workoutNum != null) {
            infoUiState.currentPlan?.weeksList?.get(index)?.numOfWorkoutsDone = workoutNum + 1
        }
        viewModelScope.launch{
            infoRepository.updateInfo(infoUiState)
        }
    }

    fun getCurrentPlan(): Plan? {
        return infoUiState.currentPlan
    }

    fun addFinishedPlan(planName: String) {
        val historyItem = HistoryItem(planName, Date())
        infoUiState.planHistory.add(historyItem)
        viewModelScope.launch{
            infoRepository.updateInfo(infoUiState)
        }
    }
    fun addFinishedWorkout(planName: String) {
        val historyItem = HistoryItem(planName, Date())
        infoUiState.workoutHistory.add(historyItem)
        viewModelScope.launch{
            infoRepository.updateInfo(infoUiState)
        }
    }
}