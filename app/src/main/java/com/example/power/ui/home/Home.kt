package com.example.power.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.R
import com.example.power.data.view_models.AppViewModelProvider
import com.example.power.data.view_models.InfoViewModel
import com.example.power.ui.configure.Section

@Preview(showBackground = true)
@Composable
fun Home(modifier: Modifier = Modifier) {
    val infoViewModel: InfoViewModel = viewModel(factory = AppViewModelProvider.Factory)
    AnimatedVisibility(
        infoViewModel.choosingPlan,
        enter = slideIn(tween(100, easing = FastOutSlowInEasing)) {
            IntOffset(+180, 0)
        } + fadeIn(animationSpec = tween(220, delayMillis = 90)),
        exit = slideOut(tween(100, easing = FastOutSlowInEasing)) {
            IntOffset(+180, 0)
        } + fadeOut(animationSpec = tween(delayMillis = 90)),
    ) {
        PlanQuickStart(
            onBack = { infoViewModel.choosingPlan = false }
        )
    }
    AnimatedVisibility(
        !infoViewModel.choosingPlan,
        enter = slideIn(tween(100, easing = FastOutSlowInEasing)) {
            IntOffset(-180, 0)
        } + fadeIn(animationSpec = tween(220, delayMillis = 90)),
        exit = slideOut(tween(100, easing = FastOutSlowInEasing)) {
            IntOffset(-180, 0)
        } + fadeOut(animationSpec = tween(delayMillis = 90)),
    ) {
        if (infoViewModel.planSelected)
            choosenPlan()
        else
            NotYetChosen(
                moveToQuickStart = { infoViewModel.choosingPlan = true }

            )
    }

}

@Composable
fun choosenPlan() {

}

@Composable
fun NotYetChosen(
    moveToQuickStart: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        PlansSection(
            moveToQuickStart = moveToQuickStart
        )
        WorkoutSection()
    }
}

@Composable
fun PlansSection(
    moveToQuickStart: () -> Unit
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
            FancyLongButton(text = "All Plans")
        }
    }
}

@Composable
fun WorkoutSection() {
    Section(
        title = R.string.workouts,
        style = MaterialTheme.typography.titleLarge
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ){
            FancyCardWithButton(
                title = "Start Moving",
                description = "Your Custome Workouts",
                onClick = {},
            )
            Spacer(modifier = Modifier.padding(5.dp))
            FancyLongButton(text = "All Workouts")
        }
    }
}
@Preview
@Composable
fun FancyLongButton (
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    text: String = "example text",
) {
    Button(
        onClick = { onClick() },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
        shape = RoundedCornerShape(9.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
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

