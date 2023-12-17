package com.example.power.ui.home

import android.content.res.Configuration
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import com.example.power.R
import com.example.power.ui.Plan.Plans
import com.example.power.ui.exercise.Exercises
import com.example.power.ui.workout.Workouts


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home(modifier: Modifier = Modifier) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTexts = listOf<String>("Plans", "Workouts", "Exercises")
    val pagerState = rememberPagerState() { tabTexts.size }
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if(!pagerState.isScrollInProgress)
            selectedTabIndex = pagerState.currentPage
    }
    Column(modifier = Modifier.fillMaxSize()) {
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
                    Plans(onItemClick = {})
                } else if(index == 1) {
                    Workouts(onItemClick = {}, showSnack = {})
                } else if (index == 2) {
                    Exercises(onItemClick = {}, showSnack = {})
                }
            }
        }
    }
}

@Composable
fun HapticFeedbackExample() {
    var count by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            // Perform some action
            count++
            // Trigger haptic feedback
            performHapticFeedback(context)
        }) {
            Text("Click me: $count")
        }
    }
}

fun performHapticFeedback(context: android.content.Context) {
    val vibrator = getSystemService(context, Vibrator::class.java)
    if (vibrator?.hasVibrator() == true) {
        // Use a lower amplitude for a lighter vibration
        val amplitude = 40
        val effect = VibrationEffect.createOneShot(10, VibrationEffect.EFFECT_DOUBLE_CLICK)
        vibrator.vibrate(effect)
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
fun WorkoutCardPreview(
    modifier: Modifier = Modifier
) {
    TitleCard(title = "workout", content = {
        Text(text = "3 X dumbbell deadlift")
        Text(text = "3 X dumbbell deadlift")
        Text(text = "3 X dumbbell deadlift")
        Text(text = "3 X dumbbell deadlift")
    }, isDragging = false)
}

@Composable
fun OutlinedCard(
    modifier: Modifier = Modifier,
    changeBackgroundColor: Boolean = false,
    mainContent: @Composable () -> Unit,
    trailingContent: @Composable () -> Unit,
) {
    val colorPressed = MaterialTheme.colorScheme.surfaceVariant
    val colorNotPressed = MaterialTheme.colorScheme.surface
    val animatedColor by animateColorAsState(
        if (changeBackgroundColor) colorPressed else colorNotPressed,
        label = "color"
    )
    Card(
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        border = BorderStroke(1.dp,MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = animatedColor)
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

@Composable
fun TitleCard(
    modifier: Modifier = Modifier,
    title: String,
    isDragging: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorPressed = MaterialTheme.colorScheme.surfaceVariant
    val colorNotPressed = MaterialTheme.colorScheme.surface
    val animatedColor by animateColorAsState(
        if (isDragging) colorPressed else colorNotPressed,
        label = "color"
    )
    Card(
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp),
        border = BorderStroke(1.dp,MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = animatedColor)
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
                Text(text = title, style = MaterialTheme.typography.titleMedium)
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




@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CardPreview() {
    PlanCard(title = "test", numOfWeeks = 12, numOfDays = 3)
}
