package com.example.power.ui.configure.Plan.workout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.data.room.Exercise
import com.example.power.data.viewmodels.AppViewModelProvider
import com.example.power.data.viewmodels.workout.ExerciseHolderItem
import com.example.power.data.viewmodels.workout.WorkoutDetails
import com.example.power.data.viewmodels.workout.WorkoutEntryViewModel
import com.example.power.ui.AppTopBar
import kotlinx.coroutines.launch

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
        title = "Edit Workout",
        onBack = onBack,
        workoutDetails = viewModel.workoutUiState.workoutDetails,
        onValueChange = viewModel::updateUiState,
        valid = viewModel.workoutUiState.isEntryValid,
        onDone = viewModel::updateWorkout,
        getMore = getMore,
        removeExerciseHolder = viewModel::removeExercise,
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
        title = "Add Workout",
        onBack = onBack,
        workoutDetails = viewModel.workoutUiState.workoutDetails,
        onValueChange = viewModel::updateUiState,
        valid = viewModel.workoutUiState.isEntryValid,
        onDone = viewModel::saveWorkout,
        getMore = getMore,
        removeExerciseHolder = viewModel::removeExercise,
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
    onDone: () -> Unit,
    getMore: () -> Unit,
    removeExerciseHolder: (ExerciseHolderItem) -> Unit,
    swapItems: (Int, Int) -> Unit,
    isActiveWorkout: Boolean = false,
    onDoneActive: () -> Unit = {},
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
                                if (!isActiveWorkout)
                                    onBack()
                                else onDoneActive()
                            }
                        },
                        valid = valid,
                        isActiveWorkout = isActiveWorkout
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
            WorkoutInputForm(
                workoutDetails = workoutDetails,
                onValueChange = onValueChange,
                getMore = getMore,
                removeExerciseHolder = removeExerciseHolder,
                swapItems = swapItems,
                isActiveWorkout = isActiveWorkout
            )

        }
    }
}



@Composable
fun RightHandNavButton(
    onClick: () -> Unit,
    valid: Boolean = true,
    isActiveWorkout: Boolean = false
) {
    IconButton(onClick = {
        if (valid)
            onClick()
    }) {
        Icon(
            imageVector = if (isActiveWorkout) Icons.Filled.Done else Icons.Filled.Save,
            contentDescription = "Save/Update",
            tint = if (valid) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
        )
    }
}
@Preview
@Composable
fun RightHandNavButtonPreview() {
    AppTopBar(title = "title", backFunction = {  }) {
        RightHandNavButton(onClick = {  })
    }
}




@Composable
fun WorkoutInputForm(
    workoutDetails: WorkoutDetails,
    onValueChange: (WorkoutDetails) -> Unit = {},
    getMore: () -> Unit,
    removeExerciseHolder: (ExerciseHolderItem) -> Unit,
    swapItems: (Int, Int) -> Unit,
    isActiveWorkout: Boolean
) {
    DragDropListExercises(
        workoutDetails = workoutDetails,
        onValueChange = onValueChange,
        removeExerciseHolder = removeExerciseHolder,
        swapItems = swapItems,
        getMore = getMore,
        isActiveWorkout = isActiveWorkout,
        nameComposable = {
            if (!isActiveWorkout) {
                Spacer(modifier = Modifier.padding(5.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    value = workoutDetails.name,
                    onValueChange = { onValueChange(workoutDetails.copy(name = it)) },
                    label = { Text(text = "Workout Name") }
                )
            }
//            if (isActiveWorkout) {
//                val value =
//                    if (workoutDetails.secsBreak == 0) ""
//                    else workoutDetails.secsBreak.toString()
//                Spacer(modifier = Modifier.padding(5.dp))
//                TextField(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 10.dp),
//                    value = value,
//                    onValueChange = {
//                        try {
//                            val change = it.toInt()
//                            val num = if (change >= 3600) 3599 else change
//                            onValueChange(workoutDetails.copy(secsBreak = num))
//                        } catch (e: Exception) {
//                            onValueChange(workoutDetails.copy(secsBreak = 0))
//                        }
//                    },
//                    label = { Text(text = "Break-time (seconds)") },
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                )
//            }
            if (workoutDetails.name == "") {
                Spacer(modifier = Modifier.padding(5.dp))
                Text(
                    text = "Must add a name",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }
    )
    Spacer(modifier = Modifier.padding(50.dp))
}
