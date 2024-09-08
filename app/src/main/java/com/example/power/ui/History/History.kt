package com.example.power.ui.History

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.data.room.HistoryItem
import com.example.power.data.view_models.AppViewModelProvider
import com.example.power.data.view_models.InfoViewModel
import com.example.power.ui.configure.PlanHistoryCard
import com.example.power.ui.configure.WorkoutHistoryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History1(
    modifier: Modifier = Modifier,
    toWorkout: (String) -> Unit
) {
    val infoViewModel: InfoViewModel = viewModel(factory = AppViewModelProvider.Factory)
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTexts = listOf("Workouts", "Plans")
    Scaffold {
        Column(modifier = modifier
            .fillMaxSize()
            .padding(it)) {
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                tabTexts.forEachIndexed { index, text ->
                    val color by animateColorAsState(
                        targetValue =
                        if (index == selectedTabIndex) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface,
                        label = "tab row color"
                    )
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = text,
                                color = color
                            )
                        }
                    )
                }
            }
            val plansCompleted = infoViewModel.infoUiState.planHistory
            val workoutsCompleted = infoViewModel.infoUiState.workoutHistory
            Spacer(modifier = Modifier.padding(5.dp))
            AnimatedVisibility(
                selectedTabIndex == 1,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                PlanHistory(plansCompleted)
            }
            AnimatedVisibility(
                selectedTabIndex == 0,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                WorkoutHistory(workoutsCompleted, toWorkout)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(
    modifier: Modifier = Modifier,
    toWorkout: (String) -> Unit
) {
    val infoViewModel: InfoViewModel = viewModel(factory = AppViewModelProvider.Factory)
    var selectedTabIndex by remember{ mutableStateOf(0) }
    val tabTexts = listOf("Workouts", "Plans")
    Scaffold (
        Modifier
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)
        ) {
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                tabTexts.forEachIndexed { index, text ->
                    val color by animateColorAsState(
                        targetValue =
                        if (index == selectedTabIndex) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface,
                        label = "tab row color"
                    )
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = text,
                                color = color
                            )
                        }
                    )
                }
            }
            val plansCompleted = infoViewModel.infoUiState.planHistory
            val workoutsCompleted = infoViewModel.infoUiState.workoutHistory
            Spacer(modifier = Modifier.padding(5.dp))
            AnimatedVisibility(
                selectedTabIndex == 0,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                WorkoutHistory(completedList = workoutsCompleted, toWorkout)
            }
            AnimatedVisibility(
                selectedTabIndex == 1,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                PlanHistory(completedList = plansCompleted)
            }
        }
    }

}

@Composable
fun PlanHistory(
    completedList: List<HistoryItem>,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (completedList.isEmpty())
            Text(
                text = "No plans were finished",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        else {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "${completedList.size} plans completed",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(2.dp))
        }
        LazyColumn(reverseLayout = true) {
            items(completedList) { completed ->
                PlanHistoryCard(
                    title = completed.name,
                    date = completed.date,
                )
            }
        }
    }
}
@Composable
fun WorkoutHistory(
    completedList: List<HistoryItem>,
    onClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (completedList.isEmpty())
            Text(
                text = "No workouts were finished",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        else {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "${completedList.size} workouts completed",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(2.dp))
        }
            
        LazyColumn(reverseLayout = true) {
            items(completedList) { completed ->
                WorkoutHistoryCard(
                    title = completed.name,
                    date = completed.date,
                    onClick = {onClick(completed.name)}
                )
            }
        }
    }
}