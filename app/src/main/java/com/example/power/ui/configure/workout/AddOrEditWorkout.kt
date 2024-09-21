package com.example.power.ui.configure.Plan.workout

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.data.room.Exercise
import com.example.power.data.viewmodels.AppViewModelProvider
import com.example.power.data.viewmodels.workout.ExerciseHolderItem
import com.example.power.data.viewmodels.workout.WorkoutDetails
import com.example.power.data.viewmodels.workout.WorkoutEntryViewModel
import com.example.power.ui.AppTopBar
import com.example.power.ui.configure.components.RightHandNavButton
import kotlinx.coroutines.launch

/**
 * editing a workout page
 */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EditWorkout(
    modifier: Modifier = Modifier,
    workoutName: String?,
    getMore: () -> Unit,
    getExercise: () -> Exercise?,
    onBack: () -> Unit,
    viewModel: WorkoutEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // loads the workouts details for the first time
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


/**
 * add a new workout page
 */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AddWorkout(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    getMore: () -> Unit,
    getExercise: () -> Exercise?,
    viewModel: WorkoutEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // retrieve an exercise that was added
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

/**
 * a page for adding a workout or editing or starting it
 */
@RequiresApi(Build.VERSION_CODES.Q)
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

/**
 * and a drag and drop list of exercises and a place to write the name
 */
@RequiresApi(Build.VERSION_CODES.Q)
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
            // name field
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
