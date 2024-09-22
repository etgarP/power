package com.example.power.ui.configure.Plan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.power.data.viewmodels.AppViewModelProvider
import com.example.power.data.viewmodels.workout.WorkoutViewModel
import com.example.power.ui.components.AppTopBar
import com.example.power.ui.configure.Plan.workout.workoutComposable
import com.example.power.ui.components.SearchItem


@Preview
@Composable
fun preview() {
    ChooseWorkoutForPlan(onClick = {}, onBack = {})
}

/**
 * a page to choose a workout for the plan
 */
@Composable
fun ChooseWorkoutForPlan(
    modifier: Modifier = Modifier,
    onClick: (Workout?) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(enableBack = true,
                title = "Choose Workout", backFunction = onBack)
        },
    ) { paddingValues ->
        WorkoutPageForPlan(
            modifier = modifier.padding(paddingValues),
            onItemClick = onClick,
        )
    }
}

/**
 * page to choose workout, on click returns
 */
@Composable
fun WorkoutPageForPlan(
    modifier: Modifier = Modifier,
    onItemClick: (Workout?) -> Unit
) {
    val workoutViewModel: WorkoutViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val searchText by workoutViewModel.searchText.collectAsState()
    val workouts by workoutViewModel.workouts.collectAsState()
    Column(modifier = modifier.fillMaxHeight()) {
        // search
        SearchItem(searchVal = searchText, setVal = workoutViewModel::onSearchTextChange)
        // the workouts
        LazyColumn() {
            items(workouts) { workout ->
                val passesSearch = workout.doesMatchSearchQuery(searchText)
                AnimatedVisibility(visible = passesSearch) {
                    workoutComposable(
                        exerciseName = workout.name,
                        numOfExercises = workout.numOfExercises,
                        onEdit = { },
                        onItemClick = { onItemClick(workout) },
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
