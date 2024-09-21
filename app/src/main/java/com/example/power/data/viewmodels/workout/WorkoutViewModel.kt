package com.example.power.data.viewmodels.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.power.data.repository.PlanRepository
import com.example.power.data.repository.WorkoutRepository
import com.example.power.data.room.Plan
import com.example.power.data.room.Workout
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * viewModel for searching and deleting workouts
 */
class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository,
    private val plansRepository: PlanRepository,
) : ViewModel() {

    /**
     * flow of the workouts
     */
    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())

    /**
     * the workouts, updates with changes
     */
    val workouts: StateFlow<List<Workout>> get() = _workouts

    /**
     * makes sure the workouts get updated
     */
    init {
        viewModelScope.launch {
            workoutRepository.getAllWorkoutsStream().collect {
                _workouts.emit(it)
            }
        }
    }
    /**
     * saves the search text
     */
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    /**
     * saves the text on change
     */
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    /**
     * returns the list of the plans for deleting a workout
     * (so it wont delete a workout thats in a plan)
     */
    suspend fun getPlans(): List<Plan> {
        val plans = emptyList<Plan>()
        val collector: FlowCollector<List<Plan>> = FlowCollector { plansRepository.getAllPlansStream() }
        collector.emit(plans)
        return plans
    }

    /**
     * deletes a workout only if its not in the plans list
     */
    suspend fun onDelete(workoutName: String, plans : List<Plan>) : Boolean {
        val workout = workoutRepository.getWorkoutByName(workoutName)
        if (workout != null) {
            for (plan in plans) {
                for (w in plan.workouts) {
                    if (w.id == workout.id) {
                        return false
                    }
                }
            }
            workoutRepository.deleteWorkout(workout)
        }
        return true
    }
}