package com.example.power.data.view_models.exercise
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
 * ViewModel to validate and insert exercises in the Room database.
 */
class ExerciseEntryViewModel(private val exercisesRepository: ExercisesRepository) : ViewModel() {

    /**
     * Holds current exercise ui state
     */
    var exerciseUiState by mutableStateOf(ExerciseUiState())
        private set

    /**
     * Updates the [exerciseUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(exerciseDetails: ExerciseDetails) {
        exerciseUiState =
            ExerciseUiState(exerciseDetails = exerciseDetails, isEntryValid = validateInput(exerciseDetails))
    }

    private fun validateInput(uiState: ExerciseDetails = exerciseUiState.exerciseDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && name.length < 50
        }
    }
    suspend fun saveExercise() {
        if (validateInput()) {
            exercisesRepository.insertExercise(exerciseUiState.exerciseDetails.toExercise())
        }
    }
    suspend fun updateExercise() {
        if (validateInput()) {
            exercisesRepository.updateExercise(exerciseUiState.exerciseDetails.toExercise())
        }
    }
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
 * Represents Ui State for an exercise.
 */
data class ExerciseUiState(
    val exerciseDetails: ExerciseDetails = ExerciseDetails(),
    val isEntryValid: Boolean = false
)

data class ExerciseDetails(
    val id: Int = 0,
    var type: String = "Weight",
    var body: String = "Arms",
    var name: String = "",
)

/**
 * Extension function to convert [ExerciseDetails] to [Exercise]. If the value of [ExerciseDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [ItemDetails.quantity] is not a valid [Int], then the quantity will be set to 0
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
 * Extension function to convert [Item] to [ItemUiState]
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
