package com.example.power.ui.workout

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.R
import com.example.power.data.room.Workout
import com.example.power.data.view_models.AppViewModelProvider
import com.example.power.data.view_models.plan.PlanDetails
import com.example.power.data.view_models.plan.PlanEntryViewModel
import com.example.power.data.view_models.plan.WorkoutItem
import com.example.power.ui.AppTopBar
import com.example.power.ui.exercise.DropMenu
import com.example.power.ui.home.OutlinedCard
import com.example.power.ui.home.Section
import com.example.power.ui.home.TitleCard
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
                toolTipMessage = "Swipe an exercise to the right or left to delete it",
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
                    OutlinedButton(
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
                DropMenu(options = typesOfPlan, label = "Type Of Plan",
                    onValueChange = { type -> onValueChange(planDetails.copy(type = type)) },
                    value = planDetails.type
                )
            }

        }
    )
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
    var selectedItem: WorkoutItem? by remember { mutableStateOf(null) }
    var list = planDetails.workouts
    LazyColumn {
        item {
            fields()
        }
        item {
            Section(title = R.string.exercises, tailContent = {
                Column(
                    modifier = Modifier.clickable{ getMore() }
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "")
                }
            }) {}
        }
        items(list, key = { it.uniqueKey }) { item ->
            ReorderableSwipableItem(
                modifier = Modifier.animateItemPlacement(),
                onDismiss = {
                    removeExerciseHolder(item)
                    list = list.toMutableList() - item
                    onValueChange(planDetails.copy(workouts = list))
                }) {
                WorkoutHolder(
                    workout = item.workout,
                    onClick =
                    {
                        selectedItem = if (selectedItem == null) {
                            item
                        } else {
                            val firstIndex = list.indexOf(selectedItem!!)
                            val secondIndex = list.indexOf(item)
                            if (firstIndex != -1 && secondIndex != -1) {
                                swapItems(firstIndex, secondIndex)
                            }
                            null
                        }
                    },
                    showSwap = selectedItem == null,
                    showCheck = selectedItem != null && selectedItem != item,
                )
            }

        }

        item {
            if (planDetails.workouts.isEmpty())
                Text(
                    text = "No workouts were added",
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
        }
        item {
            Box(modifier = Modifier
                .fillMaxWidth()
                .animateItemPlacement(),
                contentAlignment = Alignment.Center){
                buttonComposable()
            }
        }
    }
}

@Composable
fun WorkoutHolder(
    modifier: Modifier = Modifier,
    workout: Workout,
    onClick: () -> Unit,
    showSwap: Boolean,
    showCheck: Boolean
) {
    TitleCard(modifier, title = "${workout.name}") {
        for (exercise in workout.exercises) {
            Text(text = "${exercise.sets} X ${exercise.exercise.name}", style = MaterialTheme.typography.bodyMedium)
        }
    }
    OutlinedCard(
        modifier,
        mainContent = {
            Text(text = workout.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.padding(1.dp))
            Column() {
                for (exercise in workout.exercises) {
                    Text(text = "${exercise.sets} X ${exercise.exercise.name}",
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        })
    {
        if (showSwap || showCheck)
            IconButton(onClick = {
                onClick()
            }) {
                if (showSwap)
                    Icon(Icons.Filled.SwapVert, contentDescription = "more info")
                else
                    Icon(Icons.Filled.Check, contentDescription = "more info")
            }
    }

}