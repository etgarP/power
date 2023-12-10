package com.example.power.data.view_models.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.power.data.repository.exercise.ExercisesRepository
import com.example.power.data.room.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(private val exercisesRepository: ExercisesRepository) : ViewModel() {
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> get() = _exercises
    init {
        viewModelScope.launch {
            exercisesRepository.getAllExercisesStream().collect {
                _exercises.emit(it)
            }
        }
    }
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
    fun onDelete(exerciseName: String) {
        viewModelScope.launch {
            val exercise = exercisesRepository.getExerciseByName(exerciseName)
            if (exercise != null)
                exercisesRepository.deleteExercise(exercise)
        }
    }
    suspend fun getExercise(exerciseName: String): Exercise? {
        return exercisesRepository.getExerciseByName(exerciseName)
    }
}