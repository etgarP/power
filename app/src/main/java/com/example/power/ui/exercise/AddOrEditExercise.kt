package com.example.power.ui.exercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.data.view_models.AppViewModelProvider
import com.example.power.data.view_models.exercise.ExerciseDetails
import com.example.power.data.view_models.exercise.ExerciseEntryViewModel
import com.example.power.ui.AppTopBar
import com.example.power.ui.ExerciseScreens
import kotlinx.coroutines.launch
import kotlin.reflect.KSuspendFunction0

val bodyTypes = listOf("Arms", "Core", "Back", "Chest", "Legs", "Shoulders", "Cardio", "Other")

val category = listOf("Weight", "Reps", "Duration", "Cardio")

@Composable
fun EditExercise(
    modifier: Modifier = Modifier,
    exerciseName: String?,
    onBack: () -> Unit,
    viewModel: ExerciseEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
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

@OptIn(ExperimentalMaterial3Api::class)
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
            AppTopBar(enableBack = true, enableMenu = false,
                title = title, backFunction = onBack,
            )
        },
    ) { paddingValues ->
         Column(
             modifier.verticalScroll(rememberScrollState())
                 .fillMaxWidth()
                 .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally) {
            ExerciseInputForm(exerciseDetails = exerciseDetails,
                onValueChange = onValueChange, onBack = onBack
            )
            OutlinedButton(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseInputForm(
    exerciseDetails: ExerciseDetails,
    onValueChange: (ExerciseDetails) -> Unit = {},
    onBack: () -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier.padding(top = 15.dp),
        value = exerciseDetails.name,
        onValueChange = { onValueChange(exerciseDetails.copy(name = it)) },
        label = { Text(text = "Exercise Name") }
    )
    DropMenu(options = bodyTypes, label = "Target body part",
        onValueChange = {bodyType -> onValueChange(exerciseDetails.copy(body = bodyType))},
        value = exerciseDetails.body
    )
    DropMenu(options = category, label = "Category",
        onValueChange = {type -> onValueChange(exerciseDetails.copy(type = type))},
        value = exerciseDetails.type
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropMenu(
    options: List<String>,
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .padding(top = 15.dp),
            readOnly = true,
            value = value,
            onValueChange = {onValueChange(it)},
            label = { Text(text = label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onValueChange(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
@Preview
fun AddExercisePreview() {
    AddExercise(onBack = {})
}

@Composable
fun AddBtn(modifier: Modifier = Modifier, onAdd: () -> Unit) {
    FloatingActionButton(modifier = modifier, onClick = { onAdd() }) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
    }
}

@Composable
fun SaveExerciseBtn(modifier: Modifier = Modifier, onAdd: (String) -> Unit) {
    FloatingActionButton(modifier = modifier, onClick = { onAdd(ExerciseScreens.AddItem.route) }) {
        Icon(imageVector = Icons.Filled.Save, contentDescription = "save")
    }
}
