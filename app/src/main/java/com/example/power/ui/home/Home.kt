package com.example.power.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import com.example.power.R
import com.example.power.ui.configure.Section

@Preview(showBackground = true)
@Composable
fun Home(modifier: Modifier = Modifier) {
//    var inQuickStart by remember {mutable}
    PlanQuickStart()
}

@Composable
fun NotYetChosen() {
    Column {
        PlansSection()
        WorkoutSection()
    }
}

@Composable
fun PlansSection() {
    Section(
        title = R.string.plans,
        style = MaterialTheme.typography.titleLarge
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ){
            FancyCardWithButton(
                title = "Quick Start",
                description = "Based on your preferences"
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

                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Start")
                }
            }
        }

    }
}

