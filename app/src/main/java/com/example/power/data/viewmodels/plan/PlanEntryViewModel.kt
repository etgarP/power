package com.example.power.data.viewmodels.plan
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.power.data.repository.PlanRepository
import com.example.power.data.room.Plan
import com.example.power.data.room.Workout
import com.example.power.data.room.planTypeToStringMap
import com.example.power.data.room.stringToPlanTypeMap
import java.util.UUID

/**
 * ViewModel to validate and insert and edit plans in the Room database.
 */
class PlanEntryViewModel(
    private val planRepository: PlanRepository,
) : ViewModel() {

    /**
     * Holds current plan ui state
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

    /**
    * validate the input detail for the plan.
     * checks the name isn't blank, that there are workouts, max 100 weeks, and at least 1 week.
    */
    private fun validateInput(uiState: PlanDetails = planUiState.planDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && name.length < 50 && workouts.isNotEmpty()
                    && weeks <= 100 && weeks > 0
        }
    }

    /**
     * saves the current plan to the repository if it is valid
     */
    suspend fun savePlan() {
        if (validateInput()) {
            val plan = planUiState.planDetails.toPlan()
            if (plan != null) planRepository.insertPlan(plan)
        }
    }

    /**
     * updates the plan in the repository if its valid
     */
    suspend fun updatePlan() {
        if (validateInput()) {
            planUiState.planDetails.toPlan()?.let { planRepository.updatePlan(it) }
        }
    }

    /**
     * removes a workout from the plan
     */
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

    /**
     * loads the plan details from room
     */
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

    /**
     * takes a workout and adds it to the workouts of the plan
     */
    fun addWorkout(workout: Workout) {
        workout.position = planUiState.planDetails.workouts.size
        planUiState.planDetails.workouts = planUiState.planDetails.workouts + workout.toItem()
    }

    /**
     * reorders the two items with two different indexes on the list
     */
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

/**
 * holds a workout,
 * the unique key is useful for displaying the workout in a lazy column
 */
data class WorkoutItem(
    val workout: Workout,
    val uniqueKey: String = UUID.randomUUID().toString()
)

/**
 * makes a workout item
 */
fun Workout.toItem() = WorkoutItem(this)

/**
 * converts a workout list to workout item list
 */
fun toWorkoutItemList(workouts: List<Workout>) =
    workouts.map { it.toItem() }

/**
 * converts a workout item list to workout list
 */
fun toWorkoutList(workouts: List<WorkoutItem>) =
    workouts.map { it.workout }

/**
 * holds a plan with all of its information
 * this object makes it easier to edit the plan through the ui
 */
data class PlanDetails(
    var id: Int = 0,
    var name: String = "",
    var workouts: List<WorkoutItem> = emptyList(),
    var weeks: Int = 0,
    var type: String = "Gym Plan"
)

/**
 * Extension function to convert [PlanDetails] to [Plan].
 */
fun PlanDetails.toPlan(): Plan? {
    val plan = stringToPlanTypeMap[type]?.let {
        Plan(
            id = id,
            name = name,
            workouts = toWorkoutList(workouts),
            weeks = weeks,
            planType = it
        )
    }
    plan?.startWeeks()
    return plan
}


/**
 * Extension function to convert [Plan] to [PlanUiState]
 */
fun Plan.toPlanUiState(isEntryValid: Boolean = false): PlanUiState = PlanUiState(
    planDetails = this.toPlanDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Plan] to [PlanDetails]
 */
fun Plan.toPlanDetails(): PlanDetails = PlanDetails(
    id = id,
    name = name,
    weeks = weeks,
    workouts = toWorkoutItemList(workouts),
    type = planTypeToStringMap[planType] ?: "Gym Plan"
)
