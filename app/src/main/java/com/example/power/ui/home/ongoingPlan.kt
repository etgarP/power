package com.example.power.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.data.room.Plan
import com.example.power.data.room.Week
import com.example.power.data.viewmodels.AppViewModelProvider
import com.example.power.data.viewmodels.InfoViewModel
import com.example.power.ui.components.FancyLongButton
import com.example.power.ui.components.MyAlertDialog
import com.example.power.ui.components.TraditionalButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/**
 * returns how many weeks passed since an initial date
 */
fun weeksPassed(initialDate: Date): Int {
    val currentDate = Date()
    val millisecondsPassed = currentDate.time - initialDate.time
    val daysPassed = TimeUnit.MILLISECONDS.toDays(millisecondsPassed).toInt()
    return daysPassed / 7
}

/**
 * returns the place where the btns should be in
 * they go right to the left and back like an arching road
 */
fun getAlignment(index: Int, direction: Boolean) : Double {
    return if (direction) index % 4 - 1.5
    else -(index % 4 - 1.5)
}

/**
 * shows the progress of the current workout
 * allows to get right in to the next workout for your plan right from the home screen
 * splits the workouts by week and shows current week
 * upon finished plan adds plan to history
 */
@Composable
fun onGoingPlan(
    infoViewModel: InfoViewModel = viewModel(factory = AppViewModelProvider.Factory),
    deletePlan: () -> Unit,
    plan: Plan,
    startWorkout: (String, String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var scrollToPosition  by remember { mutableStateOf(0F) }
    var start by rememberSaveable { mutableStateOf(true) }
    if (start) {
        coroutineScope.launch {
            start = false
            delay(50)
            scrollState.animateScrollTo(scrollToPosition.roundToInt())
        }
    }
    val date = infoViewModel.infoUiState.date
    val workouts = infoViewModel.infoUiState.currentPlan?.workouts
    Column (
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Spacer(modifier = Modifier.padding(28.dp))
        var direction = true
        var start = 0
        var lastWeek: Week = Week(1,1)
        // goes over every week in the plan
        for ((index, week) in plan.weeksList.withIndex()) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates -> // scrolls to current week
                        if (index == weeksPassed(date))
                            scrollToPosition = coordinates.positionInRoot().y - 250
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val weekNum = "Week ${index+1}"
                val done = week.numOfWorkoutsDone == week.totalNumOfWorkouts
                // all completed weeks
                WeekTitle(weekNum, index, date)
                for(i in 0 until week.numOfWorkoutsDone) {
                    val position = getAlignment(start, direction)
                    if (position == 1.5 || position == -1.5 && start > 3) {
                        direction = !direction
                        start++
                    }
                    start++
                    alignedWorkout(
                        done = true,
                        active = false,
                        alignment = position,
                    )
                }
                // the rest of the week
                for(i in 0 until week.totalNumOfWorkouts - week.numOfWorkoutsDone) {
                    val position = getAlignment(start, direction)
                    if (position == 1.5 || position == -1.5 && start > 2)  {
                        direction = !direction
                        start++
                    }
                    start++
                    val workoutName = workouts?.get(i+week.numOfWorkoutsDone)?.name
                    val lastWeekDone = lastWeek.numOfWorkoutsDone == lastWeek.totalNumOfWorkouts
                    // the current week
                    if (i == 0 && (index == weeksPassed(date) || lastWeekDone)) {
                        alignedWorkout(
                            done = false,
                            active = true,
                            alignment = position,
                            onClick = {
                                if (workoutName != null)
                                    startWorkout(workoutName, index.toString())
                            }
                        )
                    }
                    else // the next weeks
                        alignedWorkout(
                            done = false,
                            active = false,
                            alignment = position,
                            onClick = {
                                if (workoutName != null)
                                    startWorkout(workoutName, index.toString())
                            }
                        )
                }
            }
            lastWeek = week
        }
        val endWeek = plan.weeksList[plan.weeks - 1]
        val finished = endWeek.totalNumOfWorkouts == endWeek.numOfWorkoutsDone
        SwitchPlan(
            finished = finished,
            deletePlan = deletePlan
        ) { infoViewModel.addFinishedPlan(plan.name) }
    }
}

/**
 * the title of the screen
 * also says if its the current week
 */
@Composable
fun WeekTitle(weekNum: String, index: Int, date: Date) {
    Surface (
        modifier = Modifier.fillMaxWidth().padding(10.dp),
    ) {
        LinedText {
            val status = if (index == weeksPassed(date)) " - This Week" else ""
            Text(
                fontSize = 20.sp,
                text = buildAnnotatedString {
                    append(weekNum)
                    if (index == weeksPassed(date))
                        // write " - this week" in brighter color
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, color = MaterialTheme.colorScheme.onSurface)) {
                            append(status)
                        }
                },
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

/**
 * text with a line on both sides
 */
@Composable
fun LinedText(content: @Composable () -> Unit) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Line to the left of the text
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 2.dp
        )
        Spacer(modifier = Modifier.padding(5.dp))
        // The text content
        content()
        Spacer(modifier = Modifier.padding(5.dp))
        // Line to the right of the text
        HorizontalDivider(
            modifier = Modifier
                .weight(1f),
            thickness = 2.dp
        )
    }
}

/**
 * a button to switch a plan or finish a plan
 * it depends on if the plan is finished or not
 * starts an alert dialon on click
 */
@Composable
fun SwitchPlan(finished: Boolean, deletePlan: () -> Unit, onComplete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center)
    {
        var openAlertDialog by remember { mutableStateOf(false) }
        when {
            openAlertDialog -> {
                MyAlertDialog(
                    onDismissRequest = { openAlertDialog = false },
                    onConfirmation = {
                        if (finished) onComplete()
                        deletePlan()
                        openAlertDialog = false
                    },
                    dialogTitle =
                    if (finished) "Congratulations on completing your plan!"
                    else "Switch Plan",
                    dialogText = if (finished) "This will return you to the home page"
                    else "You will lose your progress.",
                    icon = if (finished) Icons.Filled.Star
                            else Icons.Filled.SwapHoriz
                )
            }
        }
        FancyLongButton(
            modifier = Modifier.padding(10.dp),
            warning = !finished,
            onClick = { openAlertDialog = true },
            text = if (finished) "Complete Plan" else "Switch plan"
        )
    }
}

/**
 * makes a color a bit darder
 */
fun Color.darken(amount: Float): Color {
    return Color(red = red - amount, green = green - amount, blue = blue - amount, alpha = alpha)
}

/**
 * puts the workout button in an aligned manner to look like a path going right and left
 */
@Composable
fun alignedWorkout(
    modifier: Modifier = Modifier,
    done: Boolean = false,
    onClick: () -> Unit = {},
    active: Boolean = true,
    alignment: Double
) {
    val alignedModifier = when (alignment) {
        -1.5 -> modifier.padding(end = 90.dp)
        -0.5 -> modifier.padding(end = 30.dp)
        0.5 -> modifier.padding(start = 30.dp)
        1.5 -> modifier.padding(start = 90.dp)
        else -> modifier
    }
    Column (
        modifier = alignedModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        todoWorkout(done = done, active = active, onClick = onClick)
    }
}

/**
 * a button for workout
 * clicked when the workout was done (dark blue), marked v
 * unclicked and pressable when the workout wasnt done (light blue), marked start
 * unclicked and grey when the, marked star
 */
@Preview(showBackground = true)
@Composable
fun todoWorkout(
    modifier: Modifier = Modifier,
    done: Boolean = false,
    onClick: () -> Unit = {},
    active: Boolean = true
) {
    var clicked by remember { mutableStateOf(false) }
    val color by animateColorAsState(
        if (clicked) MaterialTheme.colorScheme.primary.darken(0.1f)
        else MaterialTheme.colorScheme.primary, label = ""
    )
    Surface(modifier.padding(13.dp)) {
        if (active && !done) // to do next
            Column {
                if(clicked) Spacer(modifier = Modifier.padding(vertical = 2.5.dp))
                TraditionalButton(
                    clicked = clicked,
                    color = color,
                    click = { clicked = true },
                    unClick = { clicked = false },
                    icon = Icons.Filled.Star,
                    onClick = onClick,
                    active = true
                )
            }
        else {
            if (done) // done  workout
                Button(
                    modifier = Modifier
                        .height(65.dp)
                        .width(80.dp),
                    onClick = onClick,
                    enabled = active,
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        imageVector = Icons.Filled.Check,
                        contentDescription = ""
                    )
                }
            else // not done or available
                Button(
                    modifier = Modifier
                        .height(65.dp)
                        .width(80.dp),
                    onClick = onClick,
                    enabled = active,
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        imageVector = Icons.Filled.Star,
                        contentDescription = ""
                    )
                }
        }
    }
}