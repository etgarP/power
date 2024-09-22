package com.example.power.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.R
import com.example.power.data.room.Plan
import com.example.power.data.viewmodels.AppViewModelProvider
import com.example.power.data.viewmodels.InfoViewModel
import com.example.power.data.viewmodels.plan.PlanViewModel
import com.example.power.ui.components.AppTopBar
import com.example.power.ui.components.FancyCardWithButton
import com.example.power.ui.components.FancyLongButton
import com.example.power.ui.components.Section
import com.example.power.ui.configure.Plan.WorkoutPageForPlan
import com.example.power.ui.home.QuickStartScreens.chooseFilteredPlanPage

val enterSide: EnterTransition = slideIn(tween(100, easing = FastOutSlowInEasing)) {
    IntOffset(+180, 0)
} + fadeIn(animationSpec = tween(220, delayMillis = 90))
val exitSide: ExitTransition = slideOut(tween(100, easing = FastOutSlowInEasing)) {
    IntOffset(+180, 0)
} + fadeOut(animationSpec = tween(delayMillis = 90))
val enter = slideIn(tween(100, easing = FastOutSlowInEasing)) {
    IntOffset(-180, 0)
} + fadeIn(animationSpec = tween(90))
val exit = slideOut(tween(100, easing = FastOutSlowInEasing)) {
    IntOffset(-180, 0)
} + fadeOut(animationSpec = tween(delayMillis = 90))


/**
 * home page for the app.
 * before choosing a plan:
 * allows to start a workout, or choose a plan through quick start or through list.
 * after choosing a plan:
 * puts you in the the plan screen where you can select the next plan and see your progress.
 */
@Preview(showBackground = true)
@Composable
fun Home(
    modifier: Modifier = Modifier,
    startWorkoutNoPlan: (String) -> Unit = {},
    startWorkout: (String, String) -> Unit = { _: String, _: String -> }
) {
    Column {
        val infoViewModel: InfoViewModel = viewModel(factory = AppViewModelProvider.Factory)
        val selected = infoViewModel.planSelected
        AnimatedVisibility(
            selected == 2, enter = enter, exit = exit
        ) {
            val plan = infoViewModel.getCurrentPlan()
            // if a plan was chosen puts you in the plan page
            if (plan != null)
                Surface (){
                    onGoingPlan(deletePlan =
                    {
                        infoViewModel.updateInfo(infoViewModel.infoUiState.copy(currentPlan = null))
                        infoViewModel.planSelected = 1
                    },
                        plan = plan,
                        startWorkout = startWorkout
                    )
                    TopArea()
                }
        }
        // in no plan was choosen puts you in the no plan page
        AnimatedVisibility(
            selected == 1, enter = enter, exit = exit
        ) {
            Column {
                TopArea()
                NotYetChosen(
                    moveToQuickStart = { infoViewModel.planSelected = 5 },
                    allPlans = { infoViewModel.planSelected = 3 }
                ) { infoViewModel.planSelected = 4 }
            }
        }
        // if youre in quick select
        SwitchToQuickSelect(
            switch = selected == 5,
            onBack = { infoViewModel.planSelected = 1 },
            onSelect = { selectedPlan ->
                infoViewModel.planSelected = 2
                infoViewModel.updateInfo(infoViewModel.infoUiState.copy(currentPlan = selectedPlan))
            }
        )
        // in all plans screen
        SwitchToAllPlans(
            switch = selected == 3,
            onSelect = { selectedPlan ->
                infoViewModel.planSelected = 2
                infoViewModel.updateInfo(infoViewModel.infoUiState.copy(currentPlan = selectedPlan))
            }
        ) { infoViewModel.planSelected = 1 }
        // in all workouts screen
        SwitchToAllWorkouts(
            switch = infoViewModel.planSelected == 4, onClick = startWorkoutNoPlan
        ) { infoViewModel.planSelected = 1 }
    }
}

/**
 * displays the logo at the top
 */
@Composable
fun TopArea() {
    Column {
        val dark = isSystemInDarkTheme()
        // Example of usage in code later on:
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.70f)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier
                    .size(100.dp),
                painter = painterResource(
                    id = if (dark) R.drawable.power_logo1_dark else R.drawable.power_logo1
                ),
                contentDescription = "power logo"
            )
        }
    }

}

/**
 * if you havent picked a plan yet it shows you the plan selection area and the workout section
 */
@Preview(showBackground = true)
@Composable
fun NotYetChosen(
    moveToQuickStart: () -> Unit = {},
    allPlans: () -> Unit = {},
    allWorkouts: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PlansSection(
            moveToQuickStart = moveToQuickStart,
            allPlans = allPlans
        )
        WorkoutSection(allWorkouts)
    }
}

/**
 * lets you pick a workout from all workouts
 */
@Composable
fun WorkoutSection(allWorkouts: () -> Unit) {
    Section(
        title = R.string.workouts,
        style = MaterialTheme.typography.titleLarge
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ){
            FancyLongButton(text = "All Workouts", onClick = allWorkouts)
        }
    }
}

/**
 * the plan selection area. for the not selected plan page.
 * has a quick start butoon to find a plan and a button to access all plans
 */
@Composable
fun PlansSection(
    moveToQuickStart: () -> Unit,
    allPlans: () -> Unit,
) {
    Section(
        title = R.string.plans,
        style = MaterialTheme.typography.titleLarge,
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ){
            FancyCardWithButton(
                title = "Quick Start",
                description = "Based on your preferences",
                onClick = moveToQuickStart,
                btnText = "Start"
            )
            Spacer(modifier = Modifier.padding(5.dp))
            FancyLongButton(text = "All Plans", onClick = allPlans)
        }
    }
}

/**
 * opens a screen that lets you choose from all workouts
 */
@Composable
fun SwitchToAllWorkouts(
    modifier: Modifier = Modifier,
    switch: Boolean,
    onClick: (String) -> Unit,
    onBack: () -> Unit,
) {
    AnimatedVisibility(
        switch, enter = enterSide, exit = exitSide,
    ) {
        Scaffold(
            topBar = {
                AppTopBar(enableBack = true,
                    title = "Choose Workout", backFunction = onBack)
            },
        ) { paddingValues ->
            WorkoutPageForPlan(
                modifier = modifier.padding(paddingValues),
                onItemClick = { workout -> if (workout != null) onClick(workout.name) },
            )
        }
        BackHandler {
            onBack()
        }
    }
}


/**
 * opens a screen with all plans
 */
@Composable
fun SwitchToAllPlans(
    switch: Boolean,
    onSelect: (Plan) -> Unit = {},
    onBack: () -> Unit
) {
    AnimatedVisibility(
        switch, enter = enterSide, exit = exitSide
    ) {
        val viewModel: PlanViewModel = viewModel(factory = AppViewModelProvider.Factory)
        val plans by viewModel.plans.collectAsState()
        Column {
            chooseFilteredPlanPage(filterPlans = false, plans = plans, onSelect = onSelect)
        }
        BackHandler {
            onBack()
        }
    }
}

/**
 * quick select start screen
 */
@Composable
fun SwitchToQuickSelect(
    switch: Boolean,
    onSelect: (Plan) -> Unit = {},
    onBack: () -> Unit
) {
    AnimatedVisibility(
        switch, enter = enterSide, exit = exitSide
    ) {
        PlanQuickStart(
            onBack = onBack,
            onSelect = onSelect
        )
    }
}

