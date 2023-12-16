package com.example.power.ui.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.data.room.Exercise
import com.example.power.data.room.bodyTypeMap
import com.example.power.data.room.exerciseTypeMap
import com.example.power.data.view_models.AppViewModelProvider
import com.example.power.data.view_models.exercise.ExerciseViewModel
import com.example.power.ui.AppTopBar
import com.example.power.ui.SearchItem
import com.example.power.ui.exercise.ExerciseFilterRow
import com.example.power.ui.exercise.ExerciseHolder
import kotlinx.coroutines.launch

@Preview
@Composable
fun preview() {
    ChooseExercise(onClick = {}, onBack = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseExercise(
    modifier: Modifier = Modifier,
    onClick: (Exercise?) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(enableBack = true, enableMenu = false,
                title = "Choose Exercise", backFunction = onBack)
        },
    ) { paddingValues ->
        ExercisePageForWorkout(
            modifier = modifier.padding(paddingValues),
            onItemClick = onClick,
            showDelete = false,
        )
    }
}

@Composable
fun ExercisePageForWorkout(
    modifier: Modifier = Modifier,
    onItemClick: (Exercise?) -> Unit,
    showDelete: Boolean
) {
    val exercisesViewModel: ExerciseViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val searchText by exercisesViewModel.searchText.collectAsState()
    val exercises by exercisesViewModel.exercises.collectAsState()
    var typeSelected by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("") }
    var partSelected by remember { mutableStateOf(false) }
    var selectedPart by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val onSelectType: (Boolean, String) -> Unit = { checked, itemName ->
        typeSelected = checked
        selectedType = itemName
        if (!checked){
            partSelected = false
            selectedPart = ""
        }
    }
    val onSelectBody: (Boolean, String) -> Unit = { checked, itemName ->
        partSelected = checked
        selectedPart = itemName
    }

    val typeActive: (String) -> Boolean  = { body -> selectedType == body || !typeSelected }
    val bodyActive: (String) -> Boolean =
        { type -> (selectedPart == type || !partSelected) && typeSelected }
    Column(modifier = modifier) {
        SearchItem(searchVal = searchText, setVal = exercisesViewModel::onSearchTextChange)
        ExerciseFilterRow(typeActive = typeActive, bodyActive = bodyActive,
            onSelectBody = onSelectBody, onSelectType = onSelectType)
        LazyColumn() {
            items(exercises) { exercise ->
                val bodyType: String? = bodyTypeMap[exercise.body] // Assuming `bodyTypeMap` is the map you defined earlier
                val type: String? = exerciseTypeMap[exercise.type]
                val rightType = !typeSelected || selectedType == type
                val rightBody = !partSelected || selectedPart == bodyType
                val passesSearch = exercise.doesMatchSearchQuery(searchText)
                AnimatedVisibility(visible = rightType && rightBody && passesSearch) {
                    if (bodyType != null && type != null) {
                        ExerciseHolder(
                            showMore = showDelete,
                            exerciseName = exercise.name,
                            bodyPart = bodyType,
                            onEdit = {
                                coroutineScope.launch {
                                    val exercise = exercisesViewModel.getExercise(exercise.name)
                                    onItemClick(exercise)
                                }
                            },
                            onDelete = { })
                    }
                }
            }
        }
    }
    if (exercises.isEmpty())
        Text(
            text = "No Exercises to pick",
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
}