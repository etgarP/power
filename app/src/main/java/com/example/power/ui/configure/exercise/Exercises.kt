package com.example.power.ui.configure.Plan.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.data.room.bodyTypeMap
import com.example.power.data.room.exerciseTypeMap
import com.example.power.data.viewmodels.AppViewModelProvider
import com.example.power.data.viewmodels.exercise.ExerciseViewModel
import com.example.power.ui.components.BottomSheetEditAndDelete
import com.example.power.ui.components.CollapsedInfo
import com.example.power.ui.components.MyAlertDialog
import com.example.power.ui.components.SearchItem
import kotlinx.coroutines.launch

/**
 * a page to display the exercises and allows you to search for them and filter,
 * edit, add more and delete them
 */
@Composable
fun Exercises(
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit,
    showSnack: (String) -> Unit
) {
    val exercisesViewModel: ExerciseViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val searchText by exercisesViewModel.searchText.collectAsState()
    val exercises by exercisesViewModel.exercises.collectAsState()
    val workouts by exercisesViewModel.workouts.collectAsState()
    val scope = rememberCoroutineScope()
    var typeSelected by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("") }
    var partSelected by remember { mutableStateOf(false) }
    var selectedPart by remember { mutableStateOf("") }

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
    // main area
    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier.heightIn(10.dp))
        // the search bar
        SearchItem(searchVal = searchText, setVal = exercisesViewModel::onSearchTextChange)
        // the exercise chips
        ExerciseFilterRow(typeActive = typeActive, bodyActive = bodyActive,
            onSelectBody = onSelectBody, onSelectType = onSelectType)
        // all the exercises
        LazyColumn() {
            items(exercises) { exercise ->
                val bodyType: String? = bodyTypeMap[exercise.body]
                val type: String? = exerciseTypeMap[exercise.type]
                val rightType = !typeSelected || selectedType == type
                val rightBody = !partSelected || selectedPart == bodyType
                val passesSearch = exercise.doesMatchSearchQuery(searchText)
                AnimatedVisibility(visible = rightType && rightBody && passesSearch) {
                    if (bodyType != null && type != null) {
                        CollapsedExercise(
                            showMore = true,
                            exerciseName = exercise.name,
                            bodyPart = bodyType,
                            onEdit = { onItemClick(exercise.name) },
                            onDelete = { exerciseName ->
                                scope.launch {
                                    val removed = exercisesViewModel.onDelete(exerciseName, workouts)
                                    if (!removed) {
                                        showSnack("It cannot be removed - it's used by a workout")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
        // if theres no exercises add a message explaining how to add one
        if (exercises.isEmpty())
            Text(
                text = "Click + to add an exercise",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
    }
}

/**
 * shows a collapsed exercise that has:
 * information about the exercise
 * able to delete through the alert dialog
 * able to edit by pressing the three dots or the component
 */
@Composable
fun CollapsedExercise(
    modifier: Modifier = Modifier,
    exerciseName: String,
    bodyPart: String,
    onEdit: () -> Unit = {},
    onDelete: (String) -> Unit = {},
    showMore: Boolean = false
) {
    // opens alert dialog when summoned
    var openAlertDialog by remember { mutableStateOf(false) }
    when {
        openAlertDialog -> {
            MyAlertDialog(
                onDismissRequest = { openAlertDialog = false },
                onConfirmation = {
                    onDelete(exerciseName)
                    openAlertDialog = false
                },
                dialogTitle = "Delete $exerciseName",
                dialogText = "This will delete this exercise permanently.",
                icon = Icons.Filled.Delete
            )
        }
    }
    // opens bottom sheet if button was clicked
    var showBottomSheet by remember { mutableStateOf(false) }
    BottomSheetEditAndDelete(
        onEdit = onEdit,
        type = "Exercise",
        setOpenAlertDialog = { openAlertDialog = true },
        setShowBottomSheet = { showBottomSheet = it },
        showBottomSheet = showBottomSheet
    )
    // shows the info
    CollapsedInfo(
        modifier,
        itemName = exerciseName,
        secondaryInfo = bodyPart,
        onItemClick = onEdit,
    ) {
        // if three dots enabled can open bottom sheet which also enables alert dialog
        if (showMore)
            IconButton(onClick = { showBottomSheet = true }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "show more options")
            }
    }
}