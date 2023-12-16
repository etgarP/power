package com.example.power.data.view_models.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.power.data.repository.PlanRepository
import com.example.power.data.repository.WorkoutRepository
import com.example.power.data.room.Workout
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository,
    private val plansRepository: PlanRepository,
) : ViewModel() {
    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> get() = _workouts
    init {
        viewModelScope.launch {
            workoutRepository.getAllWorkoutsStream().collect {
                _workouts.emit(it)
            }
        }
    }
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
    suspend fun onDelete(workoutName: String) : Boolean {
        val resultDeferred = CompletableDeferred<Boolean>()
        viewModelScope.launch {
            val workout = workoutRepository.getWorkoutByName(workoutName)
            if (workout != null) {
                plansRepository.getAllPlansStream().collect{
                    for (plan in it) {
                        for (w in plan.workouts) {
                            if (w.id == workout.id) {
                                resultDeferred.complete(false)
                                return@collect;
                            }
                        }
                    }
                    workoutRepository.deleteWorkout(workout)
                    resultDeferred.complete(true)
                }
            }
        }
        return resultDeferred.await()
    }
}