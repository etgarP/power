package com.example.power.ui.components

import android.content.res.Configuration
import android.view.MotionEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.power.ui.configure.Plan.exercise.AddExercise
import com.example.power.ui.home.darken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@Preview
fun AddExercisePreview() {
    AddExercise(onBack = {})
}

/**
 * a simple floating add button
 */
@Composable
fun AddBtn(modifier: Modifier = Modifier, onAdd: () -> Unit) {
    FloatingActionButton(
        modifier = modifier,
        onClick = { onAdd() },
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
    }
}

/**
 * a choose plan button
 */
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

/**
 * a better looking outlined button
 */
@Composable
fun GoodOutlinedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    textColor: Color = MaterialTheme.colorScheme.primary,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    endComposable: @Composable () -> Unit = {}
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        border = BorderStroke(
            ButtonDefaults.OutlinedBorderSize,
            borderColor
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = backgroundColor
        )
    ) {
        Text(text = text, color = textColor)
        endComposable()
    }

}

/**
 * a long button that looks good
 */
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
        elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
        shape = RoundedCornerShape(9.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
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

/**
 * a card with title description and button
 */
@Composable
fun FancyCardWithButton(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onClick: () -> Unit,
    btnText: String
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
            Column(
                modifier = Modifier.padding(vertical = 14.dp, horizontal = 10.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(text = description)
                Spacer(modifier = Modifier.padding(5.dp))

                Button(onClick = { onClick() }) {
                    Text(text = btnText)
                }
            }
        }

    }
}

@Preview
@Composable
fun FancyCardWithBtnPreview() {
    FancyCardWithButton(Modifier, "title", "description", {}, "Click")
}

/**
 * a raised button that gets lowered when held and lifts when let go
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TraditionalButton(
    modifier: Modifier = Modifier,
    clicked: Boolean,
    color: Color,
    click: () -> Unit,
    unClick: () -> Unit,
    icon: ImageVector,
    onClick: () -> Unit,
    active: Boolean,
) {
    Surface(modifier) {
        Button(
            // bottom darker btn
            modifier = if (clicked) Modifier
                .height(60.dp)//different height for if clicked and released
                .width(85.dp)
            else Modifier
                .height(65.dp)
                .width(85.dp),
            onClick = {},
            enabled = active,
            shape = RoundedCornerShape(100),
            border = BorderStroke( // Set button border
                width = 7.dp,
                color = MaterialTheme.colorScheme.primary.darken(0.1f)
            ),
        ) {
            Icon(imageVector = Icons.Filled.Check, contentDescription = "")
        }
        val scope = rememberCoroutineScope()
        Button(
            // top brighter btn
            modifier = Modifier
                .height(60.dp)
                .width(85.dp)
                .pointerInteropFilter { motionEvent ->
                    when (motionEvent.action) {// recognizing being held down
                        MotionEvent.ACTION_DOWN -> {
                            click()
                            scope.launch {
                                delay(1500)
                                unClick()
                            }
                        }

                        MotionEvent.ACTION_UP -> {
                            unClick()
                            onClick()
                        }
                    }
                    true
                },
            shape = RoundedCornerShape(100),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = color),
            onClick = onClick,
            contentPadding = PaddingValues(0.dp),
        ) {
            Icon( // icon in the middle
                modifier = Modifier.size(40.dp),
                imageVector = icon,
                contentDescription = ""
            )
        }
    }
}