package com.example.power.data.view_models.plan
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.power.data.repository.PlanRepository
import com.example.power.data.repository.WorkoutRepository
import com.example.power.data.room.Exercise
import com.example.power.data.room.Plan
import com.example.power.data.room.Workout
import com.example.power.data.room.stringToPlanTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * ViewModel to validate and insert exercises in the Room database.
 */
class PlanEntryViewModel(
    private val planRepository: PlanRepository,
    private val workoutsRepository: WorkoutRepository
) : ViewModel() {

    /**
     * Holds current exercise ui state
     */
    var planUiState by mutableStateOf(PlanUiState())
        private set

    /**
     * Updates the [planUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(workoutDetails: PlanDetails) {
        planUiState =
            PlanUiState(planDetails = workoutDetails, isEntryValid = validateInput(workoutDetails))
    }
    private fun validateInput(uiState: PlanDetails = planUiState.planDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && name.length < 50 && workouts.isNotEmpty()
        }
    }
    suspend fun savePlan() {
        if (validateInput()) {
            val plan = planUiState.planDetails.toWorkout()
            if (plan != null) planRepository.insertPlan(plan)
        }
    }
    suspend fun updatePlan() {
        if (validateInput()) {
            planUiState.planDetails.toWorkout()?.let { planRepository.updatePlan(it) }
        }
    }

    fun removeWorkout(workoutItem: WorkoutItem) {
        val position = workoutItem.workout.position
        val list = planUiState.planDetails.workouts
        val newList = list.toMutableList().apply {
            // Remove the exercise
            remove(workoutItem)
            // Update positions
            forEach { e ->
                if (e.workout.position > position) {
                    e.workout.position--
                }
            }
        }
        updateUiState(planUiState.planDetails.copy(workouts = newList))
    }
    suspend fun updateWorkouts() {
        withContext(Dispatchers.IO) {
            for (workout in planUiState.planDetails.workouts) {
                val w = workoutsRepository.getWorkout(workout.workout.id)
                if (w != null) {
                    workout.workout.exercises = w.exercises
                }
            }
            updateUiState(planUiState.planDetails.copy())
            updatePlan()
        }
    }
    suspend fun loadPlanDetails(planName: String?) : Boolean {
        if (planName != null) {
            val plan = planRepository.getPlanByName(planName)
            if (plan != null) {
                planUiState = plan.toPlanUiState()
                return true
            }
        }
        return false
    }

    fun addWorkout(workout: Workout) {
        workout.position = planUiState.planDetails.workouts.size
        planUiState.planDetails.workouts = planUiState.planDetails.workouts + workout.toItem()
    }
    fun reorderList(firstIndex: Int, secondIndex: Int) {
        val list = planUiState.planDetails.workouts
        val newList = list.toMutableList().apply {
            // Swap the positions of the selected items based on IDs
            val temp = this[firstIndex].workout.position
            this[firstIndex].workout.position = this[secondIndex].workout.position
            this[secondIndex].workout.position = temp
        }.sortedBy { it.workout.position }
        updateUiState(planUiState.planDetails.copy(
            workouts = newList,
        ))
    }
}

/**
 * Represents Ui State for an exercise.
 */
data class PlanUiState(
    val planDetails: PlanDetails = PlanDetails(),
    val isEntryValid: Boolean = false,
)

data class WorkoutItem(
    val workout: Workout,
    val uniqueKey: String = UUID.randomUUID().toString()
)

fun Workout.toItem() = WorkoutItem(this)
fun toWorkoutItemList(workouts: List<Workout>) =
    workouts.map { it -> it.toItem() }
fun toWorkoutList(workouts: List<WorkoutItem>) =
    workouts.map { it -> it.workout }

data class PlanDetails(
    var id: Int = 0,
    var name: String = "",
    var workouts: List<WorkoutItem> = emptyList(),
    var type: String = "Mixed Plan"
)

/**
 * Extension function to convert [PlanDetails] to [Exercise]. If the value of [PlanDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [ItemDetails.quantity] is not a valid [Int], then the quantity will be set to 0
 */
fun PlanDetails.toWorkout(): Plan? {
    val plan = stringToPlanTypeMap[type]?.let {
        Plan(
            id = id,
            name = name,
            workouts = toWorkoutList(workouts),
            weeks = workouts.size,
            planType = it
        )
    }
    plan?.startWeeks()
    return plan
}


/**
 * Extension function to convert [Item] to [ItemUiState]
 */
fun Plan.toPlanUiState(isEntryValid: Boolean = false): PlanUiState = PlanUiState(
    planDetails = this.toPlanDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Item] to [ItemDetails]
 */
fun Plan.toPlanDetails(): PlanDetails = PlanDetails(
    id = id,
    name = name,
    workouts = toWorkoutItemList(workouts),
)
