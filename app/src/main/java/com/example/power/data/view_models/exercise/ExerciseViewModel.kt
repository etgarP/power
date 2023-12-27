package com.example.power.data.view_models.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.power.data.repository.WorkoutRepository
import com.example.power.data.repository.exercise.ExercisesRepository
import com.example.power.data.room.Exercise
import com.example.power.data.room.Workout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val exercisesRepository: ExercisesRepository,
    private val workoutsRepository: WorkoutRepository
) : ViewModel() {
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> get() = _exercises
    init {
        viewModelScope.launch {
            exercisesRepository.getAllExercisesStream().collect {
                _exercises.emit(it)
            }
        }
    }
    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> get() = _workouts
    init {
        viewModelScope.launch {
            workoutsRepository.getAllWorkoutsStream().collect {
                _workouts.emit(it)
            }
        }
    }
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
    suspend fun getWorkouts(): List<Workout> {
        val workoutsFlow: Flow<List<Workout>> = workoutsRepository.getAllWorkoutsStream()

    // Collect values from the flow using the collect extension function
        var workouts: List<Workout> = mutableListOf()
        workoutsFlow.collect { emittedWorkouts ->
            workouts = emittedWorkouts
        }

    // Now 'workouts' contains the collected values from the flow
        return workouts
    }
    suspend fun onDelete(exerciseName: String, workouts: List<Workout>) : Boolean {
        val exercise = exercisesRepository.getExerciseByName(exerciseName)
        if (exercise != null) {
            for (workout in workouts) {
                for (e in workout.exercises) {
                    if (e.exercise.id == exercise.id) {
                        return false
                    }
                }
            }
            exercisesRepository.deleteExercise(exercise)
        }
        return true
    }
    suspend fun getExercise(exerciseName: String): Exercise? {
        return exercisesRepository.getExerciseByName(exerciseName)
    }
}