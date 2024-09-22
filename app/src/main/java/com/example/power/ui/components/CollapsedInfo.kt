package com.example.power.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.power.R
import com.example.power.ui.configure.Plan.exercise.Exercises
import com.example.power.ui.configure.getRandomNumber


@Preview(showBackground = true, widthDp = 400, heightDp = 700,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ExercisesPreview() {
    Exercises(onItemClick = {}, showSnack = {})
}

/**
 * shows the first letter in a circle and to the right a title,
 * secondary info and more text if nessesary
 */
@Composable
fun CollapsedInfo(
    modifier: Modifier = Modifier,
    itemName: String,
    secondaryInfo: String,
    onItemClick: () -> Unit,
    moreText: String = "",
    isMoreText: Boolean = false,
    endComposable: @Composable () -> Unit
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .clickable { onItemClick() }) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val firstLetter = itemName.firstOrNull().toString()
            CircleWithLetter(letter = firstLetter)
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .weight(1f)
            ) {
                Text(text = itemName, style = MaterialTheme.typography.titleMedium)
                Text(text = secondaryInfo, style = MaterialTheme.typography.bodyMedium)
                if (isMoreText)
                    Text(text = moreText, style = MaterialTheme.typography.bodyMedium)
            }
            endComposable()
        }

        HorizontalDivider(Modifier.padding(start = 70.dp))
    }
}


/**
 * returns a circle with a letter in it
 */
@Composable
fun CircleWithLetter(
    modifier: Modifier = Modifier,
    letter: String,
) {
    val colors: List<Color> = listOf(
        colorResource(R.color.light_color_1),
        colorResource(R.color.light_color_2),
        colorResource(R.color.light_color_3),
        colorResource(R.color.light_color_4),
        colorResource(R.color.light_color_5),
        colorResource(R.color.light_color_6),
    )
    val color = colors[getRandomNumber(letter)]
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(color)
    ) {
        Text(text = letter, Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.surface,
            fontSize = 25.sp
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewCircleWithLetter() {
    CircleWithLetter(letter = "h")
}


