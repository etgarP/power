package com.example.power.ui.workout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.R
import com.example.power.data.room.Workout
import com.example.power.data.view_models.AppViewModelProvider
import com.example.power.data.view_models.plan.PlanDetails
import com.example.power.data.view_models.plan.PlanEntryViewModel
import com.example.power.data.view_models.plan.WorkoutItem
import com.example.power.ui.AppTopBar
import com.example.power.ui.configure.Plan.exercise.DropMenu
import com.example.power.ui.configure.Plan.workout.ReorderableSwipableItem
import com.example.power.ui.home.Section
import com.example.power.ui.home.TitleCard
import com.example.power.ui.home.performHapticFeedback
import com.example.power.ui.rememberDragDropListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.reflect.KSuspendFunction0

@Composable
fun EditPlan(
    modifier: Modifier = Modifier,
    planName: String?,
    getMore: () -> Unit,
    getWorkout: () -> Workout?,
    onBack: () -> Unit,
    viewModel: PlanEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var firstTime by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(planName) {
        if (firstTime) {
            val isPlan = viewModel.loadPlanDetails(planName)
            if (!isPlan) onBack()
            viewModel.updateWorkouts()
        }
        firstTime = false

    }
    val addedWorkout = getWorkout()
    if (addedWorkout != null) viewModel.addWorkout(addedWorkout)
    viewModel.updateUiState(viewModel.planUiState.planDetails)
    EditOrAddPlan(
        modifier = modifier,
        onBack = onBack,
        onValueChange = viewModel::updateUiState,
        planDetails = viewModel.planUiState.planDetails,
        valid = viewModel.planUiState.isEntryValid,
        buttonText = "Update",
        removeWorkout = viewModel::removeWorkout,
        onDone = viewModel::updatePlan,
        title = "Edit Plan",
        getMore = getMore,
        swapItems = viewModel::reorderList

    )
}

@Composable
fun AddPlan(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    getMore: () -> Unit,
    getWorkout: () -> Workout?,
    viewModel: PlanEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val addedWorkout = getWorkout()
    if (addedWorkout != null) viewModel.addWorkout(addedWorkout)
    viewModel.updateUiState(viewModel.planUiState.planDetails)
    EditOrAddPlan(
        modifier = modifier,
        onBack = onBack,
        onValueChange = viewModel::updateUiState,
        planDetails = viewModel.planUiState.planDetails,
        valid = viewModel.planUiState.isEntryValid,
        buttonText = "Save",
        removeWorkout = viewModel::removeWorkout,
        onDone = viewModel::savePlan,
        title = "Add Plan",
        getMore = getMore,
        swapItems = viewModel::reorderList

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOrAddPlan(
    modifier: Modifier = Modifier,
    title: String,
    onBack: () -> Unit,
    planDetails: PlanDetails,
    onValueChange: (PlanDetails) -> Unit = {},
    valid: Boolean,
    buttonText: String,
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
                enableToolTip = true,
                toolTipMessage = "Swipe a workout to the right or left to delete it",
                bringUpSnack = { message ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                },
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
                buttonComposable = {
                    Button(
                        enabled = valid,
                        modifier = Modifier.padding(top = 15.dp),
                        onClick = {
                            coroutineScope.launch {
                                onDone()
                                onBack()
                            }
                        }
                    ) {
                        Text(text = buttonText)
                    }
                }
            )

        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanInputForm(
    planDetails: PlanDetails,
    onValueChange: (PlanDetails) -> Unit = {},
    getMore: () -> Unit,
    removeWorkout: (WorkoutItem) -> Unit,
    swapItems: (Int, Int) -> Unit,
    buttonComposable: @Composable () -> Unit
) {
    val typesOfPlan = listOf<String>("Cardio Plan","Gym Plan",
        "Body-Weight Plan", "Dumbbells Plan", "Mixed Plan")
    ReorderableWorkoutlist(
        planDetails = planDetails,
        onValueChange = onValueChange,
        removeExerciseHolder = removeWorkout,
        swapItems = swapItems,
        getMore = getMore,
        buttonComposable = buttonComposable,
        fields = {
            Column() {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    value = planDetails.name,
                    onValueChange = { onValueChange(planDetails.copy(name = it)) },
                    label = { Text(text = "Plan Name") }
                )
                DropMenu(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth(),
                    options = typesOfPlan,
                    label = "Type Of Plan",
                    onValueChange = { type -> onValueChange(planDetails.copy(type = type)) },
                    value = planDetails.type
                )
            }

        }
    )
}

@Composable
fun WorkoutsHolder(
    modifier: Modifier = Modifier,
    planDetails: PlanDetails,
    removeExerciseHolder: (WorkoutItem) -> Unit,
    onValueChange: (PlanDetails) -> Unit,
    swapItems: (Int, Int) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var overScrollJob by remember { mutableStateOf<Job?>(null) }
    val dragDropListState = rememberDragDropListState(onMove = swapItems)
    var list = planDetails.workouts
    LazyColumn (
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDrag = { change, offset ->
                        change.consume()
                        dragDropListState.onDrag(offset = offset)
                        if (overScrollJob?.isActive == true)
                            return@detectDragGesturesAfterLongPress
                        dragDropListState
                            .checkForOverScroll()
                            .takeIf { it != 0f }
                            ?.let {
                                overScrollJob = scope.launch {
                                    dragDropListState.lazyListState.scrollBy(it)
                                }
                            } ?: kotlin.run { overScrollJob?.cancel() }
                    },
                    onDragStart = {offset ->
                        performHapticFeedback(context)
                        dragDropListState.onDragStart(offset)
                    },
                    onDragEnd = {
                        dragDropListState.onDragInterrupted()
                    },
                    onDragCancel = {
                        dragDropListState.onDragInterrupted()
                    }
                )
            },
        state = dragDropListState.lazyListState
    ) {
        items(list, key = { it.uniqueKey }) { item ->
            var isDragging = dragDropListState.currentIndexOfDraggedItem == item.workout.position
            ReorderableSwipableItem(
                modifier = Modifier
                    .composed {
                        val offsetOrNull = dragDropListState.elementDisplacement.takeIf {
                            item.workout.position == dragDropListState.currentIndexOfDraggedItem
                        }
                        Modifier.graphicsLayer {
                            translationY = offsetOrNull ?: 0f
                        }
                    },
                onDismiss = {
                    removeExerciseHolder(item)
                    list = list.toMutableList() - item
                    onValueChange(planDetails.copy(workouts = list))
                }) {
                WorkoutCard(
                    workout = item.workout,
                    isDragging = isDragging
                )
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ReorderableWorkoutlist(
    planDetails: PlanDetails,
    removeExerciseHolder: (WorkoutItem) -> Unit,
    onValueChange: (PlanDetails) -> Unit,
    swapItems: (Int, Int) -> Unit,
    getMore: () -> Unit,
    buttonComposable : @Composable () -> Unit,
    fields: @Composable () -> Unit
) {

    Column {
        fields()
        Section(title = R.string.workouts, tailContent = {
            Column(
                modifier = Modifier.clickable{ getMore() }
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "")
            }
        }) {}
        WorkoutsHolder(
            planDetails = planDetails,
            removeExerciseHolder = removeExerciseHolder,
            onValueChange = onValueChange,
            swapItems = swapItems
        )
        if (planDetails.workouts.isEmpty())
            Text(
                text = "No workouts were added",
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .padding(bottom = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        Box(modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ){
            buttonComposable()
        }
    }
}

@Composable
fun WorkoutCard(
    modifier: Modifier = Modifier,
    workout: Workout,
    isDragging: Boolean,
) {
    TitleCard(
        title = workout.name,
        isDragging = isDragging
    ) {
        for (exercise in workout.exercises) {
            Text(text = "${exercise.sets} X ${exercise.exercise.name}",
                style = MaterialTheme.typography.bodyMedium)
        }
    }

}