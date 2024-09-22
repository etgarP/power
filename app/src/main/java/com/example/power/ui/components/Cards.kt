package com.example.power.ui.components

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.power.R
import com.example.power.ui.configure.formatDate
import java.util.Date

/**
 * an outlined card that can change a background in an animated fashion
 * color based on input (useful for drag and drop)
 */
@Composable
fun OutlinedCard(
    modifier: Modifier = Modifier,
    changeBackgroundColor: Boolean = false,
    padding: Boolean = true,
    mainContent: @Composable () -> Unit,
    trailingContent: @Composable () -> Unit = {  },
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
                    // main area content for example title and text (takes most of the area)
                    mainContent()
                }
                // for example an icon on the side
                trailingContent()
            }
        }
    }
}

/**
 * a big title card with a button
 */
@Composable
fun TopBigTitleCard(
    modifier: Modifier = Modifier,
    title: String,
    btnText: String,
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
                // the title goes here
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
                    // the content goes here
                    content()
                }
            }
            // the btn goes here
            Button(
                modifier = Modifier.padding(bottom = 10.dp),
                onClick = onClick
            ) {
                Text(text = btnText)
            }
        }
    }
}

/**
 * title is at the tip, the card is expendable
 */
@Composable
fun TopExpandableTitleCard(
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
    var expanded by mutableStateOf(false)
    Card(
        modifier = modifier.padding(horizontal = 8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = animatedColor)
    ) {
        Column(modifier = Modifier.padding(vertical = 5.dp) ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // the title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                // btn to texpend
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (!expanded) Icons.Filled.ExpandMore
                        else Icons.Filled.ExpandLess,
                        contentDescription = "see more"
                    )
                }
            }
            // the expended part
            Column(modifier = Modifier.animateContentSize()) {
                if (expanded) {
                    HorizontalDivider()
                    Column(modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp)){
                        content()
                    }
                }
            }
        }
    }
}

/**
 * a section area with an ability to put something stuck to the right of the title
 * and content below the title
 */
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


/**
 * shows the completed workout and its date of completion and has a button to go into that workouts
 */
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

/**
 * shows the completed plan and its date of completion
 */
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

// just a preview
@Preview
@Composable
fun TopTitleCardPreview() {
    TopExpandableTitleCard(title = "title") {
        Text(text = "CONTENT")
    }
}

// just preview
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

// just preview
@Preview
@Composable
fun WorkoutCardPreview(
    modifier: Modifier = Modifier
) {
    TopExpandableTitleCard(title = "workout", content = {
        Text(text = "3 X dumbbell deadlift")
        Text(text = "3 X dumbbell deadlift")
        Text(text = "3 X dumbbell deadlift")
        Text(text = "3 X dumbbell deadlift")
    }, isDragging = false)
}