package com.example.power.data.viewmodels.workout
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.power.data.repository.WorkoutRepository
import com.example.power.data.room.CardioExercise
import com.example.power.data.room.Exercise
import com.example.power.data.room.ExerciseHolder
import com.example.power.data.room.ExerciseType
import com.example.power.data.room.RepsExercise
import com.example.power.data.room.TimeExercise
import com.example.power.data.room.WeightExercise
import com.example.power.data.room.Workout
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel to validate edit and start workout, saves to the Room database.
 */
class WorkoutEntryViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {

    /**
     * Holds current workout ui state
     */
    var workoutUiState by mutableStateOf(WorkoutUiState())
        private set

    /**
     *  saves whether or not to show the preview of the workout at the moment
     */
    var showPreview by mutableStateOf(true)

    /**
     * Updates the [workoutUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(workoutDetails: WorkoutDetails) {
        workoutUiState =
            WorkoutUiState(workoutDetails = workoutDetails, isEntryValid = validateInput(workoutDetails))
    }

    /**
     * checks if the workout is valid aka:
     * if the name isnt blank, its length is under 50 characters,
     * there are exercises in to and the number of sets is valid
     */
    private fun validateInput(uiState: WorkoutDetails = workoutUiState.workoutDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && name.length < 50 && exercises.isNotEmpty() && validSets()
        }
    }

    /**
     * checks if the number of sets is between 1 and 30 for all exercises
     */
    private fun validSets() : Boolean {
        for (e in workoutUiState.workoutDetails.exercises) {
            val rightSetsNumber = e.exerciseHolder.sets in 1.. 30
            if (!rightSetsNumber) {
                return false
            }
        }
        return true
    }

    /**
     * saves the workouts in the repository
     */
    fun saveWorkout() {
        viewModelScope.launch {
            if (validateInput()) {
                workoutRepository.insertWorkout(workoutUiState.workoutDetails.toWorkout())
            }
        }
    }

    /**
     * updates the workout in the repository
     */
    fun updateWorkout() {
        viewModelScope.launch{
            if (validateInput()) {
                workoutRepository.updateWorkout(workoutUiState.workoutDetails.toWorkout())
            }
        }
    }

    /**
     * remove an exercise from the list of exercises
     */
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

    /**
     * loads exercise by a workout name from the repository
     */
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

    /**
     * adds an exercise holder to the list from the exercises
     */
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

    /**
     * reorder exercises in the list based on the two input positions
     */
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

/**
 * holds an exercise holder and a unique key for lazy lists
 */
data class ExerciseHolderItem(
    var exerciseHolder: ExerciseHolder,
    val uniqueKey: String = UUID.randomUUID().toString()
)

/**
 * turns [ExerciseHolder] to [ExerciseHolderItem]
 */
fun ExerciseHolder.toItem() = ExerciseHolderItem(this)

/**
 * turns [ExerciseHolder] list to [ExerciseHolderItem] list
 */
fun toExerciseHolderItemList(exercises: List<ExerciseHolder>) =
    exercises.map { it -> it.toItem() }

/**
 * turns [ExerciseHolderItem] list to [ExerciseHolder] list
 */
fun toExerciseHolderList(exercises: List<ExerciseHolderItem>) =
    exercises.map { it -> it.exerciseHolder }

/**
 * holds the details of the workout
 */
data class WorkoutDetails(
    var id: Int = 0,
    var name: String = "",
    var exercises: List<ExerciseHolderItem> = emptyList(),
) {

    /**
     * adds an exercise to the list
     */
    fun addExercise(exercise: ExerciseHolder) {
        exercise.position = exercises.size
        exercises = exercises + exercise.toItem()
    }
}

/**
 * Extension function to convert [WorkoutDetails] to [Workout].
 */
fun WorkoutDetails.toWorkout(): Workout = Workout(
    id = id,
    name = name,
    exercises = toExerciseHolderList(exercises),
    numOfExercises = exercises.size,
)

/**
 * Extension function to convert [Workout] to [WorkoutUiState]
 */
fun Workout.toWorkoutUiState(isEntryValid: Boolean = false): WorkoutUiState = WorkoutUiState(
    workoutDetails = this.toWorkoutDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Workout] to [WorkoutDetails]
 */
fun Workout.toWorkoutDetails(): WorkoutDetails = WorkoutDetails(
    id = id,
    name = name,
    exercises = toExerciseHolderItemList(exercises),
)
