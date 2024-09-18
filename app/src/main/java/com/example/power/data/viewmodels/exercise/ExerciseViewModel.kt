package com.example.power.data.viewmodels.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.power.data.repository.WorkoutRepository
import com.example.power.data.repository.exercise.ExercisesRepository
import com.example.power.data.room.Exercise
import com.example.power.data.room.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for displaying searching and deleting exercises.
 * It interacts with the repositories and handles the business logic for exercises and workouts.
 */
class ExerciseViewModel(
    private val exercisesRepository: ExercisesRepository,
    private val workoutsRepository: WorkoutRepository
) : ViewModel() {

    /**
     * updating list of exercises
     */
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())

    /**
     * Public flow to expose the list of exercises, making it immutable from outside
     */
    val exercises: StateFlow<List<Exercise>> get() = _exercises


    /**
     * updates the exercises list as its changing
     */
    init {
        viewModelScope.launch {
            exercisesRepository.getAllExercisesStream().collect {
                _exercises.emit(it)
            }
        }
    }

    /**
     * refreshing workout list
     */
    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())

    /**
     * Public flow to expose the list of workouts, making it immutable from outside
     */
    val workouts: StateFlow<List<Workout>> get() = _workouts

    /**
     * updates the exercises list as its changing
     */
    init {
        viewModelScope.launch {
            workoutsRepository.getAllWorkoutsStream().collect {
                _workouts.emit(it)
            }
        }
    }
    // the string containing the search text
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    /**
     * changes the search text string
     */
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    /**
     * if the exercise is in a workout returns false, else returns true
     */
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

    /**
     * return exercise by exercise name from from the repository
     */
    suspend fun getExercise(exerciseName: String): Exercise? {
        return exercisesRepository.getExerciseByName(exerciseName)
    }
}