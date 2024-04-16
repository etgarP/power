package com.example.power.ui.configure

import android.content.res.Configuration
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.power.R
import java.text.SimpleDateFormat
import java.util.Date

@RequiresApi(Build.VERSION_CODES.Q)
fun performHapticFeedback(context: android.content.Context) {
    val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)
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
    BottomTitleCard(title = "workout", content = {
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
    padding: Boolean = true,
    mainContent: @Composable () -> Unit,
    trailingContent: @Composable () -> Unit,
) {
    val colorPressed = MaterialTheme.colorScheme.surfaceVariant
    val colorNotPressed = MaterialTheme.colorScheme.surface
    val animatedColor by animateColorAsState(
        if (changeBackgroundColor) colorPressed else colorNotPressed,
        label = "color"
    )
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .widthIn(0.dp, 350.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = CardDefaults.cardColors(containerColor = animatedColor)
        ) {
            Row(
                if (padding) Modifier.padding(10.dp) else Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier
                    .weight(1f)
                ) {
                    mainContent()
                }
                trailingContent()
            }
        }
    }
}

@Composable
fun TopBigTitleCard(
    modifier: Modifier = Modifier,
    title: String,
    isDragging: Boolean = false,
    onClick: () -> Unit = {},
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = animatedColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .padding()
                    .weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                Column() {
                    content()
                }
            }
            Button(
                modifier = Modifier.padding(bottom = 10.dp),
                onClick = onClick
            ) {
                Text(text = "Choose This Plan")
            }
        }
    }
}

@Preview
@Composable
fun planSelect() {
    TopBigTitleCard(title = "plan name") {
        Column() {
            Spacer(Modifier.padding(10.dp))
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = "Gym Plan",
                fontSize = 18.sp
            )
            Spacer(Modifier.padding(10.dp))
            HorizontalDivider()
            Spacer(Modifier.padding(10.dp))
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = "7 Weeks",
                fontSize = 22.sp
            )
            Spacer(Modifier.padding(10.dp))
            HorizontalDivider()
            Spacer(Modifier.padding(4.dp))
            Row {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .weight(1f),
                    text = "5 Workouts Per Week:",
                    fontSize = 22.sp
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Filled.ExpandMore,
                        contentDescription = "see more"
                    )
                }
            }
            Spacer(Modifier.padding(4.dp))
            HorizontalDivider()
        }

    }
}

@Composable
fun BottomTitleCard(
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
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
            HorizontalDivider()
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
    style: TextStyle = MaterialTheme.typography.titleMedium,
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
                style = style,
            )
            tailContent()
        }

        content()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)@Composable
fun PlanCard(
    modifier: Modifier = Modifier,
    title: String = "Title",
    numOfWeeks: Int = -1,
    numOfDays: Int = -1
) {
    OutlinedCard(mainContent = {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(text = "$numOfWeeks weeks", style = MaterialTheme.typography.bodyMedium)
        Text(text = "$numOfDays days a week", style = MaterialTheme.typography.bodyMedium)
    }) {
        IconButton(onClick = { TODO() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "more info")
        }
    }
}

fun formatDate(date: Date): String {
    // Create a date formatter with the desired format
    val formatter = SimpleDateFormat("dd MMMM yyyy")

    // Format the date as a string
    return formatter.format(date)
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)@Composable
fun WorkoutHistoryCard(
    modifier: Modifier = Modifier,
    title: String = "Title",
    date: Date = Date(),
    onClick: () -> Unit = {}
) {
    OutlinedCard(mainContent = {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(text = "Completed in: ${formatDate(date)}", style = MaterialTheme.typography.bodyMedium)
    }) {
        IconButton(onClick = onClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "more info")
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)@Composable
fun PlanHistoryCard(
    modifier: Modifier = Modifier,
    title: String = "Title",
    date: Date = Date()
) {
    OutlinedCard(mainContent = {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(text = "Completed in: ${formatDate(date)}", style = MaterialTheme.typography.bodyMedium)
    }) {}
}





@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CardPreview() {
    PlanCard(title = "test", numOfWeeks = 12, numOfDays = 3)
}
