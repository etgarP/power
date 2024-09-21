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
import kotlinx.coroutines.launch
import java.util.Date

/**
 * allows interaction with the permanent information about the user
 */
class InfoViewModel(private val infoRepository: InfoRepository): ViewModel() {
    /**
     * saves a mutable state info for the ui
     */
    var infoUiState by mutableStateOf(Info())
        private set
    /**
     * saves if a plan is selected, also a mutable state
     */
    var planSelected by mutableStateOf(0)

    /**
     * sets plan to 2 if there is a plan selected otherwise 1
     * also collects the info from the repository
     */
    init {
        viewModelScope.launch {
            infoRepository.getInfo().collect {
                if (it != null) {
                    infoUiState = it // Update the mutableStateOf
                    planSelected = if (infoUiState.currentPlan != null) 2 else 1
                } else {
                    viewModelScope.launch { infoRepository.insertInfo() }
                }
            }
        }
    }
    /**
     * deletes the info if needed (for debug purpose)
     */
    fun deleteInfo() {
        viewModelScope.launch {
            infoRepository.deleteInfo()
        }
    }

    /**
     * updates an info with a new info in the repository
     */
    fun updateInfo(info: Info) {
        info.date = Date()
        viewModelScope.launch{
            infoRepository.updateInfo(info)
            infoUiState = info
        }
    }

    /**
     * marks that another workout was completed by upping the num workouts done in a week
     */
    fun completeWorkout(index: Int) {
        val workoutNum = infoUiState.currentPlan?.weeksList?.get(index)?.numOfWorkoutsDone
        if (workoutNum != null) {
            infoUiState.currentPlan?.weeksList?.get(index)?.numOfWorkoutsDone = workoutNum + 1
        }
        viewModelScope.launch{
            infoRepository.updateInfo(infoUiState)
        }
    }

    /**
     * returns the current plan
     */
    fun getCurrentPlan(): Plan? {
        return infoUiState.currentPlan
    }

    /**
     * adds the finished plan to the history
     * it had the time it was finished in and the plan name
     */
    fun addFinishedPlan(planName: String) {
        val historyItem = HistoryItem(planName, Date())
        infoUiState.planHistory.add(historyItem)
        viewModelScope.launch{
            infoRepository.updateInfo(infoUiState)
        }
    }

    /**
     * adds a finished workouts to the history
     * it adds the time and the workout name
     */
    fun addFinishedWorkout(workoutsName: String) {
        val historyItem = HistoryItem(workoutsName, Date())
        infoUiState.workoutHistory.add(historyItem)
        viewModelScope.launch{
            infoRepository.updateInfo(infoUiState)
        }
    }
}