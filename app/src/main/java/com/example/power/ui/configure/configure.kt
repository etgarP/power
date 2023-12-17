package com.example.power.ui.configure

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.power.ui.ExerciseScreens
import com.example.power.ui.PlanScreens
import com.example.power.ui.WorkoutScreens
import com.example.power.ui.configure.Plan.Plans
import com.example.power.ui.configure.Plan.exercise.AddBtn
import com.example.power.ui.configure.Plan.exercise.Exercises
import com.example.power.ui.configure.Plan.workout.Workouts

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Configure(
    modifier: Modifier = Modifier,
    editPlan: (String) -> Unit,
    editExercise: (String) -> Unit,
    editWorkout: (String) -> Unit,
    showSnack: (String) -> Unit,
    navigate: (String) -> Unit,
) {

    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabTexts = listOf("Plans", "Workouts", "Exercises")
    val pagerState = rememberPagerState() { tabTexts.size }
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if(!pagerState.isScrollInProgress)
            selectedTabIndex = pagerState.currentPage
    }
    Scaffold (
        floatingActionButton = {
            when (selectedTabIndex) {
                0 -> AddBtn(onAdd = { navigate(PlanScreens.AddItem.route)})
                1 -> AddBtn(onAdd = { navigate(WorkoutScreens.AddItem.route)})
                2 -> AddBtn(onAdd = { navigate(ExerciseScreens.AddItem.route)})
            }
        },
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(it)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabTexts.forEachIndexed { index, text ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(text = text)
                        }
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = modifier.weight(1f)
            ) { index ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if(index == 0) {
                        Plans(onItemClick = editPlan)
                    } else if(index == 1) {
                        Workouts(onItemClick = editWorkout, showSnack = showSnack)
                    } else if (index == 2) {
                        Exercises(onItemClick = editExercise, showSnack = showSnack)
                    }
                }
            }
        }
    }

}