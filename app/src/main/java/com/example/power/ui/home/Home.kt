package com.example.power.ui.home

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.power.R

@Composable
fun Home(modifier: Modifier = Modifier) {
    Column() {
        ChoosePlanSection()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChoosePlanSection(modifier: Modifier = Modifier) {
    Section(
        modifier = modifier,
        title = R.string.workouts_per_week
    ) {
        Column {
            WorkoutCard()
            WorkoutCard()
            WorkoutCard()
            WorkoutCard()
            WorkoutCard()
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SelectPlanSection(modifier: Modifier = Modifier) {
    Section(
        modifier = modifier,
        title = R.string.select_a_plan
    ) {
        Column {
            PlanCard(title = "plan 1", numOfWeeks = 5, numOfDays = 3)
            PlanCard(title = "plan 2", numOfWeeks = 12, numOfDays = 3)
            PlanCard(title = "plan 3", numOfWeeks = 13, numOfDays = 3)
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChoosePlanBtn(modifier: Modifier = Modifier) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = { /*TODO*/ }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Check, contentDescription = "choose this plan")
            Text(text = "Confirm choice", modifier = Modifier.padding(horizontal = 10.dp))
        }
    }
}

@Composable
fun WorkoutCard(modifier: Modifier = Modifier) {
    TitleCard(title = "workout") {
        Text(text = "3 X dumbbell deadlift")
        Text(text = "3 X dumbbell deadlift")
        Text(text = "3 X dumbbell deadlift")
        Text(text = "3 X dumbbell deadlift")
    }
}

@Composable
fun TitleCard(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        border = BorderStroke(1.dp,MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(vertical = 5.dp) ) {
            Row(
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column() {
                    content()
                }

            }
            Divider()
            Row(
                modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "title", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun Section(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    tailContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column(modifier) {
        Row(modifier = Modifier
            .paddingFromBaseline(top = 40.dp, bottom = 16.dp)
            .padding(horizontal = 16.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(title),
                style = MaterialTheme.typography.titleMedium,

            )
            tailContent()
        }

        content()
    }
}

@Composable
fun PlanCard(
    modifier: Modifier = Modifier,
    title: String,
    numOfWeeks: Int,
    numOfDays: Int
) {
    OutlinedCard(mainContent = {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(text = "$numOfWeeks weeks", style = MaterialTheme.typography.bodyMedium)
        Text(text = "$numOfDays days a week", style = MaterialTheme.typography.bodyMedium)
    }) {
        IconButton(onClick = { TODO() }) {
            Icon(Icons.Filled.ArrowForward, contentDescription = "more info")
        }
    }
}

@Composable
fun OutlinedCard(
    modifier: Modifier = Modifier,
    mainContent: @Composable () -> Unit,
    trailingContent: @Composable () -> Unit
) {
    Card(
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        border = BorderStroke(1.dp,MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
            ) {
                mainContent()
            }
            trailingContent()
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CardPreview() {
    PlanCard(title = "test", numOfWeeks = 12, numOfDays = 3)
}
