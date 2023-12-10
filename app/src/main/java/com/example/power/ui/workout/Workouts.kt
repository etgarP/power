package com.example.power.ui.workout

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.data.view_models.AppViewModelProvider
import com.example.power.data.view_models.workout.WorkoutViewModel
import com.example.power.ui.SearchItem
import com.example.power.ui.exercise.GeneralHolder
import com.example.power.ui.exercise.MyAlertDialog

@Composable
fun Workouts(
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    Column {
        WorkoutsPage(onItemClick = onItemClick, modifier = modifier)
    }
}

@Composable
fun WorkoutsPage(
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    val workoutViewModel: WorkoutViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val searchText by workoutViewModel.searchText.collectAsState()
    val workouts by workoutViewModel.workouts.collectAsState()
    Column(modifier = modifier.fillMaxHeight()) {
        Spacer(modifier.heightIn(10.dp))
        SearchItem(searchVal = searchText, setVal = workoutViewModel::onSearchTextChange)
        LazyColumn() {
            items(workouts) { workout ->
                val passesSearch = workout.doesMatchSearchQuery(searchText)
                AnimatedVisibility(visible = passesSearch) {
                    ExerciseComposable(
                        exerciseName = workout.name,
                        numOfExercises = workout.numOfExercises,
                        onItemClick = { onItemClick(workout.name) },
                        onDelete = { exerciseName -> workoutViewModel.onDelete(exerciseName) },
                        showDelete = false
                    )
                }
            }
        }
        if (workouts.isEmpty())
            Text(
                text = "Click + to add a workout",
                modifier = Modifier.fillMaxWidth().padding(15.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
    }
}


@Composable
fun ExerciseComposable(
    modifier: Modifier = Modifier,
    exerciseName: String,
    numOfExercises: Int,
    onItemClick: () -> Unit,
    onDelete: (String) -> Unit,
    showDelete: Boolean = true
) {
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
                dialogText = "This will delete this Workout permanently.",
                icon = Icons.Filled.Delete
            )
        }
    }
    GeneralHolder(
        modifier = modifier,
        itemName = exerciseName,
        secondaryInfo = "$numOfExercises exercises",
        onItemClick = onItemClick,
    ) {
        if(showDelete)
            IconButton(onClick = { openAlertDialog = true }) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Delete")
            }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 700, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WorkoutPreview() {
    Workouts(onItemClick = {})
}