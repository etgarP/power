package com.example.power.ui.configure.workout

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.data.room.Exercise
import com.example.power.data.room.bodyTypeMap
import com.example.power.data.viewmodels.AppViewModelProvider
import com.example.power.data.viewmodels.InfoViewModel
import com.example.power.data.viewmodels.workout.ExerciseHolderItem
import com.example.power.data.viewmodels.workout.WorkoutEntryViewModel
import com.example.power.ui.AppTopBar
import com.example.power.ui.configure.Plan.exercise.ExerciseHolder
import com.example.power.ui.configure.Plan.exercise.MyAlertDialog
import com.example.power.ui.configure.Plan.workout.EditOrAddWorkout
import kotlinx.coroutines.launch

@Composable
fun OnGoingWorkoutFromPlan(
    modifier: Modifier = Modifier,
    index: Int?,
    workoutName: String?,
    getMore: () -> Unit,
    getExercise: () -> Exercise?,
    onBack: () -> Unit,
    viewModel: InfoViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    if (index == null) onBack()
    else
        OnGoingWorkout(
            workoutName = workoutName,
            getMore = getMore,
            getExercise = getExercise,
            onBack = onBack
        ) {
            viewModel.completeWorkout(index)
        }
}

@Composable
fun OnGoingWorkout(
    modifier: Modifier = Modifier,
    workoutName: String?,
    getMore: () -> Unit,
    getExercise: () -> Exercise?,
    onBack: () -> Unit,
    viewModel: WorkoutEntryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    addToPlan: () -> Unit = {}
) {
    BackHandler {
        onBack()
    }
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
    AnimatedVisibility (
        viewModel.showPreview,

        exit = slideOut(tween(100, easing = FastOutSlowInEasing)) {
            IntOffset(-180, 0)
        },
    ) {
        val wName = viewModel.workoutUiState.workoutDetails.name
        val exercises = viewModel.workoutUiState.workoutDetails.exercises
        WorkoutPreviewPage(
            workoutName = wName,
            exercises = exercises,
            onStart = { viewModel.showPreview = false },
            onBack = onBack
        )
    }
    AnimatedVisibility(
        !viewModel.showPreview,
        enter = slideIn(tween(100, easing = FastOutSlowInEasing)) {
            IntOffset(+180, 0)
        } + fadeIn(animationSpec = tween(220, delayMillis = 90)),
    ) {
        var openAlertDialog by remember { mutableStateOf(false) }
        when {
            openAlertDialog -> {
                MyAlertDialog(
                    onDismissRequest = { openAlertDialog = false },
                    onConfirmation = {
                        onBack()
                        openAlertDialog = false
                    },
                    dialogTitle = "Cancel Workout",
                    dialogText = "Are you sure you want to leave this workout?",
                    icon = Icons.Filled.Cancel
                )
            }
        }
        BackHandler {
            openAlertDialog = true
        }
        val corutine = rememberCoroutineScope()
        val infoViewModel: InfoViewModel = viewModel(factory = AppViewModelProvider.Factory)
        if (!viewModel.showPreview) {
            viewModel.updateUiState(viewModel.workoutUiState.workoutDetails)
            EditOrAddWorkout(
                modifier = modifier,
                onBack = { openAlertDialog = true },
                onValueChange = viewModel::updateUiState,
                workoutDetails = viewModel.workoutUiState.workoutDetails,
                valid = viewModel.workoutUiState.isEntryValid,
                removeExerciseHolder = viewModel::removeExercise,
                onDone = {
                    corutine.launch {
                        viewModel.updateWorkout()
                    }
                    addToPlan()
                    if (workoutName != null)
                        infoViewModel.addFinishedWorkout(workoutName)
                 },
                title = viewModel.workoutUiState.workoutDetails.name,
                getMore = getMore,
                swapItems = viewModel::reorderList,
                isActiveWorkout = true,
            ) {
                onBack()
            }
        }
    }
}
@Composable
fun WorkoutPreviewPage(
    modifier: Modifier = Modifier,
    workoutName: String,
    exercises: List<ExerciseHolderItem>,
    onStart: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(title = workoutName, backFunction = onBack, enableBack = true)
        }
    ) {
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            LazyColumn (modifier = Modifier.weight(1f)) {
                items(exercises, key = { it.exerciseHolder.position }) { exercise ->
                    bodyTypeMap[exercise.exerciseHolder.exercise.body]?.let {
                        ExerciseHolder(
                            exerciseName = exercise.exerciseHolder.exercise.name,
                            bodyPart = it
                        )
                    }
                }
            }
            ExtendedFloatingActionButton(
                onClick = { onStart() },
                modifier = Modifier.padding(25.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text(text = "Start", style = MaterialTheme.typography.bodyLarge)
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Start Workout")
            }
        }
    }

}

