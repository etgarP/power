package com.example.power.data.view_models.workout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.power.data.repository.WorkoutRepository
import com.example.power.data.room.CardioExercise
import com.example.power.data.room.Exercise
import com.example.power.data.room.ExerciseHolder
import com.example.power.data.room.ExerciseType
import com.example.power.data.room.RepsExercise
import com.example.power.data.room.TimeExercise
import com.example.power.data.room.WeightExercise
import com.example.power.data.room.Workout
import java.util.UUID

/**
 * ViewModel to validate and insert exercises in the Room database.
 */
class WorkoutEntryViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {

    /**
     * Holds current exercise ui state
     */
    var workoutUiState by mutableStateOf(WorkoutUiState())
        private set
    var showPreview by mutableStateOf(true)

    /**
     * Updates the [workoutUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(workoutDetails: WorkoutDetails) {
        workoutUiState =
            WorkoutUiState(workoutDetails = workoutDetails, isEntryValid = validateInput(workoutDetails))
    }

    private fun validateInput(uiState: WorkoutDetails = workoutUiState.workoutDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && name.length < 50 && exercises.isNotEmpty() && validSets()
        }
    }
    private fun validSets() : Boolean {
        for (e in workoutUiState.workoutDetails.exercises) {
            val rightSetsNumber = e.exerciseHolder.sets in 1.. 30
            if (!rightSetsNumber) {
                return false
            }
        }
        return true
    }
    suspend fun saveWorkout() {
        if (validateInput()) {
            workoutRepository.insertWorkout(workoutUiState.workoutDetails.toWorkout())
        }
    }
    suspend fun updateWorkout() {
        if (validateInput()) {
            workoutRepository.updateWorkout(workoutUiState.workoutDetails.toWorkout())
        }
    }

    fun removeExercise(exerciseHolderItem: ExerciseHolderItem) {
        val position = exerciseHolderItem.exerciseHolder.position
        val list = workoutUiState.workoutDetails.exercises
        val newList = list.toMutableList().apply {
            // Remove the exercise
            remove(exerciseHolderItem)
            // Update positions
            forEach { e ->
                if (e.exerciseHolder.position > position) {
                    e.exerciseHolder.position--
                }
            }
        }
        updateUiState(workoutUiState.workoutDetails.copy(exercises = newList))
    }
    suspend fun loadWorkoutDetails(workoutName: String?) : Boolean {
        if (workoutName != null) {
            val workout = workoutRepository.getWorkoutByName(workoutName)
            if (workout != null) {
                workoutUiState = workout.toWorkoutUiState()
                return true
            }
        }
        return false
    }

    fun addExerciseHolder(exercise: Exercise) {
        val exerciseHolder = when (exercise.type) {
            ExerciseType.REPS -> RepsExercise(
                exercise = exercise,
                position = 0,
            )
            ExerciseType.DURATION -> TimeExercise(
                exercise = exercise,
                position = 0
            )
            ExerciseType.CARDIO -> CardioExercise(
                exercise = exercise,
                position = 0
            )
            ExerciseType.WEIGHT -> WeightExercise(
                exercise = exercise,
                position = 0
            )
        }
        workoutUiState.workoutDetails.addExercise(exerciseHolder)
    }
    fun reorderList(firstIndex: Int, secondIndex: Int) {
        val list = workoutUiState.workoutDetails.exercises
        val newList = list.toMutableList().apply {
            // Swap the positions of the selected items based on IDs
            val temp = this[firstIndex].exerciseHolder.position
            this[firstIndex].exerciseHolder.position = this[secondIndex].exerciseHolder.position
            this[secondIndex].exerciseHolder.position = temp
        }.sortedBy { it.exerciseHolder.position }
        updateUiState(workoutUiState.workoutDetails.copy(
            exercises = newList,
        ))
    }
}

/**
 * Represents Ui State for an exercise.
 */
data class WorkoutUiState(
    val workoutDetails: WorkoutDetails = WorkoutDetails(),
    val isEntryValid: Boolean = false,
)

data class ExerciseHolderItem(
    var exerciseHolder: ExerciseHolder,
    val uniqueKey: String = UUID.randomUUID().toString()
)

fun ExerciseHolder.toItem() = ExerciseHolderItem(this)
fun toExerciseHolderItemList(exercises: List<ExerciseHolder>) =
    exercises.map { it -> it.toItem() }
fun toExerciseHolderList(exercises: List<ExerciseHolderItem>) =
    exercises.map { it -> it.exerciseHolder }

data class WorkoutDetails(
    var id: Int = 0,
    var name: String = "",
    var exercises: List<ExerciseHolderItem> = emptyList(),
) {
    fun addExercise(exercise: ExerciseHolder) {
        exercise.position = exercises.size
        exercises = exercises + exercise.toItem()
    }
}

/**
 * Extension function to convert [WorkoutDetails] to [Exercise]. If the value of [WorkoutDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [ItemDetails.quantity] is not a valid [Int], then the quantity will be set to 0
 */
fun WorkoutDetails.toWorkout(): Workout = Workout(
    id = id,
    name = name,
    exercises = toExerciseHolderList(exercises),
    numOfExercises = exercises.size,
)

/**
 * Extension function to convert [Item] to [ItemUiState]
 */
fun Workout.toWorkoutUiState(isEntryValid: Boolean = false): WorkoutUiState = WorkoutUiState(
    workoutDetails = this.toWorkoutDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Item] to [ItemDetails]
 */
fun Workout.toWorkoutDetails(): WorkoutDetails = WorkoutDetails(
    id = id,
    name = name,
    exercises = toExerciseHolderItemList(exercises),
)
