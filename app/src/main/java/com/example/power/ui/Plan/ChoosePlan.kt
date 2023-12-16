package com.example.power.ui.Plan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.data.room.Workout
import com.example.power.data.view_models.AppViewModelProvider
import com.example.power.data.view_models.workout.WorkoutViewModel
import com.example.power.ui.AppTopBar
import com.example.power.ui.SearchItem
import com.example.power.ui.workout.ExerciseComposable


@Preview
@Composable
fun preview() {
    ChoosePlan(onClick = {}, onBack = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoosePlan(
    modifier: Modifier = Modifier,
    onClick: (Workout?) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(enableBack = true, enableMenu = false,
                title = "Choose Workout", backFunction = onBack)
        },
    ) { paddingValues ->
        WorkoutPageForPlan(
            modifier = modifier.padding(paddingValues),
            onItemClick = onClick,
        )
    }
}

@Composable
fun WorkoutPageForPlan(
    modifier: Modifier = Modifier,
    onItemClick: (Workout?) -> Unit
) {
    val workoutViewModel: WorkoutViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val searchText by workoutViewModel.searchText.collectAsState()
    val workouts by workoutViewModel.workouts.collectAsState()
    Column(modifier = modifier.fillMaxHeight()) {
        SearchItem(searchVal = searchText, setVal = workoutViewModel::onSearchTextChange)
        LazyColumn() {
            items(workouts) { workout ->
                val passesSearch = workout.doesMatchSearchQuery(searchText)
                AnimatedVisibility(visible = passesSearch) {
                    ExerciseComposable(
                        exerciseName = workout.name,
                        numOfExercises = workout.numOfExercises,
                        onEdit = { onItemClick(workout) },
                        onDelete = { },
                        showMore = false
                    )
                }
            }
        }
        if (workouts.isEmpty())
            Text(
                text = "No Workouts to pick",
                modifier = Modifier.fillMaxWidth().padding(15.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
    }
}
