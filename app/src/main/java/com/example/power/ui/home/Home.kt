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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.R
import com.example.power.data.room.Plan
import com.example.power.data.view_models.AppViewModelProvider
import com.example.power.data.view_models.InfoViewModel
import com.example.power.data.view_models.plan.PlanViewModel
import com.example.power.ui.AppTopBar
import com.example.power.ui.configure.Plan.WorkoutPageForPlan
import com.example.power.ui.configure.Section

val enterSide: EnterTransition = slideIn(tween(100, easing = FastOutSlowInEasing)) {
    IntOffset(+180, 0)
} + fadeIn(animationSpec = tween(220, delayMillis = 90))
val exitSide: ExitTransition = slideOut(tween(100, easing = FastOutSlowInEasing)) {
    IntOffset(+180, 0)
} + fadeOut(animationSpec = tween(delayMillis = 90))
val enter = slideIn(tween(100, easing = FastOutSlowInEasing)) {
    IntOffset(-180, 0)
} + fadeIn(animationSpec = tween(220, delayMillis = 90))
val exit = slideOut(tween(100, easing = FastOutSlowInEasing)) {
    IntOffset(-180, 0)
} + fadeOut(animationSpec = tween(delayMillis = 90))


@Preview(showBackground = true)
@Composable
fun Home(
    modifier: Modifier = Modifier,
    startWorkoutNoPlan: (String) -> Unit = {},
    startWorkout: (String, String) -> Unit = { _: String, _: String -> }
) {
    Column {
        TopArea()
        val infoViewModel: InfoViewModel = viewModel(factory = AppViewModelProvider.Factory)
        AnimatedVisibility(
            infoViewModel.choosingPlan,
            enter = enterSide, exit = exitSide,
        ) {
            PlanQuickStart(
                onBack = { infoViewModel.choosingPlan = false },
                onSelect = { selectedPlan ->
                    infoViewModel.choosingPlan = false
                    infoViewModel.planSelected = 2
                    infoViewModel.updateInfo(infoViewModel.infoUiState.copy(currentPlan = selectedPlan))
                }
            )
        }
        AnimatedVisibility(
            !infoViewModel.choosingPlan, enter = enter, exit = exit
        ) {
            if (infoViewModel.planSelected == 2){
                val plan = infoViewModel.getCurrentPlan()
                if (plan != null)
                    onGoingPlan(deletePlan =
                    {
                        infoViewModel.updateInfo(infoViewModel.infoUiState.copy(currentPlan = null))
                        infoViewModel.planSelected = 1
                    },
                        plan = plan,
                        startWorkout = startWorkout
                    )

            }
            AnimatedVisibility(
                infoViewModel.planSelected == 1, enter = enter, exit = exit
            ) {
                NotYetChosen(
                    moveToQuickStart = { infoViewModel.choosingPlan = true },
                    allPlans = { infoViewModel.planSelected = 3 }
                ) { infoViewModel.planSelected = 4 }
            }

            SwitchToAllPlans(
                switch = infoViewModel.planSelected == 3,
                onSelect = { selectedPlan ->
                    infoViewModel.choosingPlan = false
                    infoViewModel.planSelected = 2
                    infoViewModel.updateInfo(infoViewModel.infoUiState.copy(currentPlan = selectedPlan))
                }
            ) { infoViewModel.planSelected = 1 }
            SwitchToAllWorkouts(
                switch = infoViewModel.planSelected == 4, onClick = startWorkoutNoPlan
            ) { infoViewModel.planSelected = 1 }
        }
    }

}

@Composable
fun TopArea() {
    val dark = isSystemInDarkTheme()
    // Example of usage in code later on:
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(105.dp),
            painter = painterResource(
                id = if (dark) R.drawable.power_logo1_dark else R.drawable.power_logo1
            ),
            contentDescription = "power logo"
        )
    }
    Divider(color = MaterialTheme.colorScheme.outlineVariant)

}

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
                onClick = moveToQuickStart
            )
            Spacer(modifier = Modifier.padding(5.dp))
            FancyLongButton(text = "All Plans", onClick = allPlans)
        }
    }
}

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


@Composable
fun SwitchToAllPlans(
    switch: Boolean,
    onSelect: (Plan) -> Unit = {},
    onBack: () -> Unit
) {
    AnimatedVisibility(
        switch,
        enter = slideIn(tween(100, easing = FastOutSlowInEasing)) {
            IntOffset(+180, 0)
        } + fadeIn(animationSpec = tween(220, delayMillis = 90)),
        exit = slideOut(tween(100, easing = FastOutSlowInEasing)) {
            IntOffset(+180, 0)
        } + fadeOut(animationSpec = tween(delayMillis = 90)),
    ) {
        val viewModel: PlanViewModel = viewModel(factory = AppViewModelProvider.Factory)
        val plans by viewModel.plans.collectAsState()
        chooseFilteredPlan(filterPlans = false, plans = plans, onSelect = onSelect)
        BackHandler {
            onBack()
        }
    }
}

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
//            FancyCardWithButton(
//                title = "Start Moving",
//                description = "Your Custom Workouts",
//                onClick = {},
//            )
//            Spacer(modifier = Modifier.padding(5.dp))
            FancyLongButton(text = "All Workouts", onClick = allWorkouts)
        }
    }
}
@Preview
@Composable
fun FancyLongButton (
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    text: String = "example text",
    warning: Boolean = false
) {
    Button(
        onClick = { onClick() },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
        shape = RoundedCornerShape(9.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (warning) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun FancyCardWithButton(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    Surface(
        shadowElevation = 5.dp,
        shape = RoundedCornerShape(9.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
        ) {
            Column (
                modifier = Modifier.padding(vertical = 14.dp, horizontal = 10.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(text = description)
                Spacer(modifier = Modifier.padding(5.dp))

                Button(onClick = { onClick() }) {
                    Text(text = "Start")
                }
            }
        }

    }
}

