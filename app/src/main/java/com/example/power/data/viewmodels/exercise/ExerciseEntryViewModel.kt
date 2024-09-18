package com.example.power.data.viewmodels.exercise
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.power.data.repository.exercise.ExercisesRepository
import com.example.power.data.room.BodyType
import com.example.power.data.room.Exercise
import com.example.power.data.room.ExerciseType
import com.example.power.data.room.bodyTypeMap
import com.example.power.data.room.bodyTypeMapFromString
import com.example.power.data.room.exerciseTypeMap
import com.example.power.data.room.exerciseTypeMapFromString

/**
 * ViewModel to edit and create exercises.
 * This ViewModel interacts with the repository and manages the UI state for exercise entries.
 */
class ExerciseEntryViewModel(private val exercisesRepository: ExercisesRepository) : ViewModel() {

    /**
     * Holds current exercise ui state
     */
    var exerciseUiState by mutableStateOf(ExerciseUiState())
        private set

    /**
     * Updates the current [exerciseUiState] with new details.
     * It also performs validation on the input values to ensure they're valid.
     */
    fun updateUiState(exerciseDetails: ExerciseDetails) {
        exerciseUiState =
            ExerciseUiState(exerciseDetails = exerciseDetails, isEntryValid = validateInput(exerciseDetails))
    }

    /**
     * Validates the input details for the exercise.
     * It checks if the exercise name is not blank and does not exceed 50 characters.
     */
    private fun validateInput(uiState: ExerciseDetails = exerciseUiState.exerciseDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && name.length < 50
        }
    }

    /**
     * Saves a new exercise to the repository if the input data is valid.
     * This is a suspend function and must be called from a coroutine.
     */
    suspend fun saveExercise() {
        if (validateInput()) {
            exercisesRepository.insertExercise(exerciseUiState.exerciseDetails.toExercise())
        }
    }

    /**
     * Updates an existing exercise in the repository if the input data is valid.
     * This is a suspend function and must be called from a coroutine.
     */
    suspend fun updateExercise() {
        if (validateInput()) {
            exercisesRepository.updateExercise(exerciseUiState.exerciseDetails.toExercise())
        }
    }

    /**
     * Loads the details of an exercise by its name from the repository.
     * If the exercise is found, it updates the UI state.
     * Returns true if the exercise was loaded successfully, otherwise false.
     */
    suspend fun loadExerciseDetails(exerciseName: String?) : Boolean {
        if (exerciseName != null) {
            val exercise = exercisesRepository.getExerciseByName(exerciseName)
            if (exercise != null) {
                exerciseUiState = exercise.toExerciseUiState()
                return true
            }
        }
        return false
    }
}

/**
 * Represents the UI state for an exercise.
 * It contains the exercise details and a flag indicating whether the input is valid.
 */
data class ExerciseUiState(
    val exerciseDetails: ExerciseDetails = ExerciseDetails(),
    val isEntryValid: Boolean = false
)

/**
 * Data class representing the details of an exercise.
 * This includes the exercise's ID, type (e.g., "Weight"), body target (e.g., "Arms"), and name.
 */
data class ExerciseDetails(
    val id: Int = 0,
    var type: String = "Weight",
    var body: String = "Arms",
    var name: String = "",
)

/**
 * Extension function to convert [ExerciseDetails] to [Exercise].
 * It ensures that valid types and body targets are used, with defaults if necessary.
 */
fun ExerciseDetails.toExercise(): Exercise = Exercise(
    id = id,
    name = name,
    body = when (val result = bodyTypeMapFromString[body]) {
        is BodyType -> result
        else -> BodyType.ARMS
    },
    type = when (val result = exerciseTypeMapFromString[type]) {
        is ExerciseType -> result
        else -> ExerciseType.WEIGHT
    }
)

/**
 * Extension function to convert [Exercise] to [ExerciseUiState].
 * This is used to load exercise data into the UI state, with an optional validation flag.
 */
fun Exercise.toExerciseUiState(isEntryValid: Boolean = false): ExerciseUiState = ExerciseUiState(
    exerciseDetails = this.toExerciseDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Item] to [ItemDetails]
 */
fun Exercise.toExerciseDetails(): ExerciseDetails = ExerciseDetails(
    id = id,
    name = name,
    body = when (val result = bodyTypeMap[body]) {
        is String -> result
        else -> "Arms"
    },
    type = when (val result = exerciseTypeMap[type]) {
        is String -> result
        else -> "Weight"
    }
)
