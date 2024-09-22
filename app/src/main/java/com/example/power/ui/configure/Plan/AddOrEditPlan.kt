package com.example.power.ui.workout

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.R
import com.example.power.data.room.Workout
import com.example.power.data.viewmodels.AppViewModelProvider
import com.example.power.data.viewmodels.plan.PlanDetails
import com.example.power.data.viewmodels.plan.PlanEntryViewModel
import com.example.power.data.viewmodels.plan.WorkoutItem
import com.example.power.ui.components.AppTopBar
import com.example.power.ui.components.RightHandNavButton
import com.example.power.ui.components.DropMenu
import com.example.power.ui.components.Section
import com.example.power.ui.components.SwipeableDragAndDropList
import com.example.power.ui.components.TopExpandableTitleCard
import kotlinx.coroutines.launch
import kotlin.reflect.KSuspendFunction0

/**
 * a page for editing a plan
 */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EditPlan(
    modifier: Modifier = Modifier,
    planName: String?,
    getMore: () -> Unit,
    getWorkout: () -> Workout?,
    onBack: () -> Unit,
    viewModel: PlanEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // in the first time opening the page it loads the plan details
    var firstTime by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(planName) {
        if (firstTime) {
            val isPlan = viewModel.loadPlanDetails(planName)
            if (!isPlan) onBack()
        }
        firstTime = false
    }
    // gets a workout if one was added
    val addedWorkout = getWorkout()
    if (addedWorkout != null) viewModel.addWorkout(addedWorkout)
    viewModel.updateUiState(viewModel.planUiState.planDetails)
    EditOrAddPlan(
        modifier = modifier,
        onBack = onBack,
        onValueChange = viewModel::updateUiState,
        planDetails = viewModel.planUiState.planDetails,
        valid = viewModel.planUiState.isEntryValid,
        removeWorkout = viewModel::removeWorkout,
        onDone = viewModel::updatePlan,
        title = "Edit Plan",
        getMore = getMore,
        swapItems = viewModel::reorderList

    )
}

/**
 * a page for adding a plan
 */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AddPlan(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    getMore: () -> Unit,
    getWorkout: () -> Workout?,
    viewModel: PlanEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // gets a workout if one was added
    val addedWorkout = getWorkout()
    if (addedWorkout != null) viewModel.addWorkout(addedWorkout)
    viewModel.updateUiState(viewModel.planUiState.planDetails)
    EditOrAddPlan(
        modifier = modifier,
        onBack = onBack,
        onValueChange = viewModel::updateUiState,
        planDetails = viewModel.planUiState.planDetails,
        valid = viewModel.planUiState.isEntryValid,
        removeWorkout = viewModel::removeWorkout,
        onDone = viewModel::savePlan,
        title = "Add Plan",
        getMore = getMore,
        swapItems = viewModel::reorderList

    )
}

/**
 * holds a scaffold with a top app bad and the rest of the information
 */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EditOrAddPlan(
    modifier: Modifier = Modifier,
    title: String,
    onBack: () -> Unit,
    planDetails: PlanDetails,
    onValueChange: (PlanDetails) -> Unit = {},
    valid: Boolean,
    onDone: KSuspendFunction0<Unit>,
    getMore: () -> Unit,
    removeWorkout: (WorkoutItem) -> Unit,
    swapItems: (Int, Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            AppTopBar(
                enableBack = true,
                title = title,
                backFunction = onBack,
                endIcon = {
                    RightHandNavButton(
                        onClick = {
                            coroutineScope.launch {
                                onDone()
                                onBack()
                            }
                        },
                        valid = valid,
                    )
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier
                .fillMaxWidth()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally) {
            PlanInputForm(
                planDetails = planDetails,
                onValueChange = onValueChange,
                getMore = getMore,
                removeWorkout = removeWorkout,
                swapItems = swapItems,
            )

        }
    }
}

/**
 * the input part of the add or edit a plan
 * defines the different fields:
 * name, number of weeks, types of plan
 * and leaves errors if not filled correctly
 * also in that case does not let the plan get saved
 */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PlanInputForm(
    planDetails: PlanDetails,
    onValueChange: (PlanDetails) -> Unit = {},
    getMore: () -> Unit,
    removeWorkout: (WorkoutItem) -> Unit,
    swapItems: (Int, Int) -> Unit,
) {
    val typesOfPlan = listOf("Gym Plan", "Body-Weight Plan", "Dumbbells Plan")
    // holds all the workouts and the fields
    ReorderableWorkoutlist(
        planDetails = planDetails,
        removeExerciseHolder = removeWorkout,
        swapItems = swapItems
    ) {
        Column() {
            Spacer(modifier = Modifier.padding(5.dp))
            // plan name
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                value = planDetails.name,
                onValueChange = { onValueChange(planDetails.copy(name = it)) },
                label = { Text(text = "Plan Name") }
            )
            if (planDetails.name == "") {
                Spacer(modifier = Modifier.padding(5.dp))
                Text(
                    text = "Must add a name",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
            Spacer(modifier = Modifier.padding(5.dp))
            // number of weeks
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                value = when (planDetails.weeks) {
                    0 -> ""
                    else -> planDetails.weeks.toString()
                },
                onValueChange = {
                    var weeks = 0
                    weeks = try {
                        it.toInt()
                    } catch (_: Exception) {
                        0
                    }
                    onValueChange(
                        planDetails.copy(weeks = weeks)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text(text = "Number of weeks") }
            )
            if (planDetails.weeks > 100 || planDetails.weeks < 1) {
                Spacer(modifier = Modifier.padding(5.dp))
                Text(
                    text = "0 < weeks < 101",
                    modifier = Modifier
                        .padding(horizontal = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            // type of plans
            DropMenu(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(),
                options = typesOfPlan,
                label = "Type Of Plan",
                onValueChange = { type -> onValueChange(planDetails.copy(type = type)) },
                value = planDetails.type
            )
            // section for the workouts
            Section(title = R.string.workouts, tailContent = {
                Column(
                    modifier = Modifier.clickable { getMore() }
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "")
                }
            }) {}
            if (planDetails.workouts.isEmpty())
                Text(
                    text = "No workouts were added",
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 20.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
        }

    }
}

/**
 * holds the fields and the workouts
 */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ReorderableWorkoutlist(
    modifier: Modifier = Modifier,
    planDetails: PlanDetails,
    removeExerciseHolder: (WorkoutItem) -> Unit,
    swapItems: (Int, Int) -> Unit,
    fields: @Composable () -> Unit
) {

    Column {
        fields()
        var list = planDetails.workouts
        SwipeableDragAndDropList(
            modifier = modifier,
            items = planDetails.workouts,
            itemKey = { it.uniqueKey },
            onMove = swapItems,
            onRemove = { item ->
                removeExerciseHolder(item)
                list = list.toMutableList() - item
            }
        ) { workoutItem, isDragging ->
            WorkoutCard(
                workout = workoutItem.workout,
                isDragging = isDragging
            )
        }
    }
}

/**
 * a card holding all exercises in a workout with its names and num of sets
 * its also expendable
 */
@Composable
fun WorkoutCard(
    modifier: Modifier = Modifier,
    workout: Workout,
    isDragging: Boolean,
) {
    TopExpandableTitleCard(
        modifier = modifier,
        title = workout.name,
        isDragging = isDragging
    ) {
        for (exercise in workout.exercises) {
            Text(text = "${exercise.sets} X ${exercise.exercise.name}",
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}