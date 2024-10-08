package com.example.power.ui.configure

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.power.ui.ExerciseScreens
import com.example.power.ui.PlanScreens
import com.example.power.ui.WorkoutScreens
import com.example.power.ui.configure.Plan.Plans
import com.example.power.ui.configure.Plan.exercise.Exercises
import com.example.power.ui.configure.Plan.workout.Workouts
import com.example.power.ui.components.AddBtn
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Configure(
    modifier: Modifier = Modifier,
    editPlan: (String) -> Unit,
    editExercise: (String) -> Unit,
    editWorkout: (String) -> Unit,
    startWorkout: (String) -> Unit,
    navigate: (String) -> Unit,
    selectedTabIndex: Int,
    setSelectedTabIndex: (Int) -> Unit
) {
    val tabTexts = listOf("Plans", "Workouts", "Exercises")
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val activateSnackBar: (String) -> Unit = { string ->
        scope.launch {
            snackbarHostState.showSnackbar(string)
        }
    }
    Scaffold (
        modifier,
        snackbarHost =  { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            when (selectedTabIndex) {
                0 -> AddBtn(onAdd = { navigate(PlanScreens.AddItem.route)})
                1 -> AddBtn(onAdd = { navigate(WorkoutScreens.AddItem.route)})
                2 -> AddBtn(onAdd = { navigate(ExerciseScreens.AddItem.route)})
            }
        },
    ) {

        Column(modifier = Modifier
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
                        onClick = { setSelectedTabIndex(index) },
                        text = {
                            Text(
                                text = text,
                                color = color
                            )
                        }
                    )
                }
            }
            AnimatedVisibility(
                selectedTabIndex == 0,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Plans(onEdit = editPlan)
            }
            AnimatedVisibility(
                selectedTabIndex == 1,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Workouts(onEdit = editWorkout, onItemClick = startWorkout, showSnack = activateSnackBar)
            }
            AnimatedVisibility(
                selectedTabIndex == 2,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Exercises(onItemClick = editExercise, showSnack = activateSnackBar)
            }
        }
    }

}