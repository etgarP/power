package com.example.power.ui.configure.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.power.ui.configure.Plan.exercise.AddExercise

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