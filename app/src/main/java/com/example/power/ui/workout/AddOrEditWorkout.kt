package com.example.power.ui.workout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.power.data.room.Exercise
import com.example.power.data.view_models.AppViewModelProvider
import com.example.power.data.view_models.workout.ExerciseHolderItem
import com.example.power.data.view_models.workout.WorkoutDetails
import com.example.power.data.view_models.workout.WorkoutEntryViewModel
import com.example.power.ui.AppTopBar
import kotlinx.coroutines.launch
import kotlin.reflect.KSuspendFunction0

@Composable
fun EditWorkout(
    modifier: Modifier = Modifier,
    workoutName: String?,
    getMore: () -> Unit,
    getExercise: () -> Exercise?,
    onBack: () -> Unit,
    viewModel: WorkoutEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var firstTime by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(workoutName) {
        if (firstTime) {
            val isWorkoutExist = viewModel.loadWorkoutDetails(workoutName)
            if (!isWorkoutExist) onBack()
        }
        firstTime = false
    }
    val addedExercise = getExercise()
    if (addedExercise != null) viewModel.addExerciseHolder(addedExercise)
    viewModel.updateUiState(viewModel.workoutUiState.workoutDetails)
    EditOrAddWorkout(
        modifier = modifier,
        onBack = onBack,
        onValueChange = viewModel::updateUiState,
        workoutDetails = viewModel.workoutUiState.workoutDetails,
        valid = viewModel.workoutUiState.isEntryValid,
        buttonText = "Update",
        removeExerciseHolder = viewModel::removeExercise,
        onDone = viewModel::updateWorkout,
        title = "Edit Workout",
        getMore = getMore,
        swapItems = viewModel::reorderList

    )
}

@Composable
fun AddWorkout(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    getMore: () -> Unit,
    getExercise: () -> Exercise?,
    viewModel: WorkoutEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val addedExercise = getExercise()
    if (addedExercise != null) viewModel.addExerciseHolder(addedExercise)
    viewModel.updateUiState(viewModel.workoutUiState.workoutDetails)
    EditOrAddWorkout(
        modifier = modifier,
        onBack = onBack,
        onValueChange = viewModel::updateUiState,
        workoutDetails = viewModel.workoutUiState.workoutDetails,
        valid = viewModel.workoutUiState.isEntryValid,
        buttonText = "Save",
        removeExerciseHolder = viewModel::removeExercise,
        onDone = viewModel::saveWorkout,
        title = "Add Workout",
        getMore = getMore,
        swapItems = viewModel::reorderList

    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOrAddWorkout(
    modifier: Modifier = Modifier,
    title: String,
    onBack: () -> Unit,
    workoutDetails: WorkoutDetails,
    onValueChange: (WorkoutDetails) -> Unit = {},
    valid: Boolean,
    buttonText: String,
    onDone: KSuspendFunction0<Unit>,
    getMore: () -> Unit,
    removeExerciseHolder: (ExerciseHolderItem) -> Unit,
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
            WorkoutInputForm(
                workoutDetails = workoutDetails,
                onValueChange = onValueChange,
                getMore = getMore,
                removeExerciseHolder = removeExerciseHolder,
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
fun WorkoutInputForm(
    workoutDetails: WorkoutDetails,
    onValueChange: (WorkoutDetails) -> Unit = {},
    getMore: () -> Unit,
    removeExerciseHolder: (ExerciseHolderItem) -> Unit,
    swapItems: (Int, Int) -> Unit,
    buttonComposable: @Composable () -> Unit
) {
    DragDropListExercises(
        workoutDetails = workoutDetails,
        onValueChange = onValueChange,
        removeExerciseHolder = removeExerciseHolder,
        swapItems = swapItems,
        getMore = getMore,
        buttonComposable = buttonComposable,
        nameComposable = {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                value = workoutDetails.name,
                onValueChange = { onValueChange(workoutDetails.copy(name = it)) },
                label = { Text(text = "Workout Name") }
            )
        }
    )
}
