package com.example.power.ui.configure.Plan.exercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.data.viewmodels.AppViewModelProvider
import com.example.power.data.viewmodels.exercise.ExerciseDetails
import com.example.power.data.viewmodels.exercise.ExerciseEntryViewModel
import com.example.power.ui.AppTopBar
import com.example.power.ui.configure.components.DropMenuOutlined
import kotlinx.coroutines.launch
import kotlin.reflect.KSuspendFunction0

val bodyTypes = listOf("Arms", "Core", "Back", "Chest", "Legs", "Shoulders", "Cardio", "Other")

val category = listOf("Weight", "Reps", "Duration", "Cardio")

/**
 * the screen for editing exercises
 */
@Composable
fun EditExercise(
    modifier: Modifier = Modifier,
    exerciseName: String?,
    onBack: () -> Unit,
    viewModel: ExerciseEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    // gets the workout details, if not found returns
    LaunchedEffect(exerciseName) {
        val isExerciseExist = viewModel.loadExerciseDetails(exerciseName)
        if (!isExerciseExist) onBack()
    }
    EditOrAddExercise(
        modifier = modifier,
        onBack = onBack,
        onValueChange = viewModel::updateUiState,
        exerciseDetails = viewModel.exerciseUiState.exerciseDetails,
        valid = viewModel.exerciseUiState.isEntryValid,
        buttonText = "Update",
        onDone = viewModel::updateExercise,
        title = "Edit Exercise"
    )
}

/**
 * the screen for adding an exercise
 */
@Composable
fun AddExercise(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: ExerciseEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    EditOrAddExercise(
        modifier = modifier,
        onBack = onBack,
        onValueChange = viewModel::updateUiState,
        exerciseDetails = viewModel.exerciseUiState.exerciseDetails,
        valid = viewModel.exerciseUiState.isEntryValid,
        buttonText = "Save",
        onDone = viewModel::saveExercise,
        title = "Add Exercise"
    )
}

/**
 * the form for adding or editing
 */
@Composable
fun EditOrAddExercise(
    modifier: Modifier = Modifier,
    title: String,
    onBack: () -> Unit,
    exerciseDetails: ExerciseDetails,
    onValueChange: (ExerciseDetails) -> Unit = {},
    valid: Boolean,
    buttonText: String,
    onDone: KSuspendFunction0<Unit>
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        modifier = modifier,
        topBar = {
            // top bar
            AppTopBar(enableBack = true, title = title, backFunction = onBack)
        },
    ) { paddingValues ->
         Column(
             modifier
                 .verticalScroll(rememberScrollState())
                 .fillMaxWidth()
                 .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally) {
             // the input form
            ExerciseInputForm(
                modifier = Modifier,
                exerciseDetails = exerciseDetails,
                onValueChange = onValueChange, onBack = onBack
            )
             // button for saving
            Button(
                enabled = valid,
                modifier = Modifier.padding(top = 15.dp),
                onClick = {
                    coroutineScope.launch {
                        onDone()
                        onBack()
                    }
                },
            ) {
                Text(text = buttonText)
            }
        }
    }
}

/**
 * the exercise input form
 */
@Composable
fun ExerciseInputForm(
    modifier: Modifier = Modifier,
    exerciseDetails: ExerciseDetails,
    onValueChange: (ExerciseDetails) -> Unit = {},
    onBack: () -> Unit,
) {
    Column(modifier) {
        // form for entering exercise
        OutlinedTextField(
            modifier = Modifier.padding(top = 15.dp),
            value = exerciseDetails.name,
            onValueChange = { onValueChange(exerciseDetails.copy(name = it)) },
            label = { Text(text = "Exercise Name") }
        )
        // form for entering body type
        DropMenuOutlined(options = bodyTypes, label = "Target body part",
            onValueChange = {bodyType -> onValueChange(exerciseDetails.copy(body = bodyType))},
            value = exerciseDetails.body
        )
        // form for entering category of exercise
        DropMenuOutlined(options = category, label = "Category",
            onValueChange = {type -> onValueChange(exerciseDetails.copy(type = type))},
            value = exerciseDetails.type
        )
    }

}

