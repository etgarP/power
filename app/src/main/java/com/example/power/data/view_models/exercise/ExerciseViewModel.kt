package com.example.power.data.view_models.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.power.data.repository.WorkoutRepository
import com.example.power.data.repository.exercise.ExercisesRepository
import com.example.power.data.room.Exercise
import kotlinx.coroutines.CompletableDeferred
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
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }
    suspend fun onDelete(exerciseName: String) : Boolean {
        val resultDeferred = CompletableDeferred<Boolean>()
        viewModelScope.launch {
            val exercise = exercisesRepository.getExerciseByName(exerciseName)
            if (exercise != null) {
                workoutsRepository.getAllWorkoutsStream().collect{
                    for (workout in it) {
                        for (e in workout.exercises) {
                            if (e.exercise.id == exercise.id) {
                                resultDeferred.complete(false)
                                return@collect;
                            }
                        }
                    }
                    exercisesRepository.deleteExercise(exercise)
                    resultDeferred.complete(true)
                }
            }
        }
        return resultDeferred.await()
    }
    suspend fun getExercise(exerciseName: String): Exercise? {
        return exercisesRepository.getExerciseByName(exerciseName)
    }
}