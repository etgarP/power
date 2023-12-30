package com.example.power.ui.configure

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.power.ui.ExerciseScreens
import com.example.power.ui.PlanScreens
import com.example.power.ui.WorkoutScreens
import com.example.power.ui.configure.Plan.Plans
import com.example.power.ui.configure.Plan.exercise.AddBtn
import com.example.power.ui.configure.Plan.exercise.Exercises
import com.example.power.ui.configure.Plan.workout.Workouts
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Configure(
    modifier: Modifier = Modifier,
    editPlan: (String) -> Unit,
    editExercise: (String) -> Unit,
    editWorkout: (String) -> Unit,
    startWorkout: (String) -> Unit,
    navigate: (String) -> Unit,
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabTexts = listOf("Plans", "Workouts", "Exercises")
//    val pagerState = rememberPagerState() { tabTexts.size }
//    LaunchedEffect(selectedTabIndex) {
//        pagerState.animateScrollToPage(selectedTabIndex)
//    }
//    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
//        if(!pagerState.isScrollInProgress)
//            selectedTabIndex = pagerState.currentPage
//    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val activateSnackBar: (String) -> Unit = { string ->
        scope.launch {
            snackbarHostState.showSnackbar(string)
        }
    }
    Scaffold (
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
            SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
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

//            HorizontalPager(
//                state = pagerState,
//                modifier = modifier.weight(1f)
//            ) { index ->
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    if(index == 0) {
//                        Plans(onEdit = editPlan)
//                    } else if(index == 1) {
//                        Workouts(onEdit = editWorkout, onItemClick = startWorkout, showSnack = activateSnackBar)
//                    } else if (index == 2) {
//                        Exercises(onItemClick = editExercise, showSnack = activateSnackBar)
//                    }
//                }
//            }
        }
    }

}