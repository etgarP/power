package com.example.power.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.power.ui.configure.Plan.exercise.CollapsedExercise

/**
 * a pop up to use when you need to tell an important message to the user
 * and have them replay with a yes or no.
 * this implementation makes dealing with the function simpler and easier
 */
@Composable
fun MyAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
    textYes: String = "Confirm",
    textNo: String = "Dismiss",
) {
    AlertDialog(
        icon = {
            Icon(imageVector = icon, contentDescription = "alert Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(textYes)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(textNo)
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewExerciseHolder() {
    CollapsedExercise(
        exerciseName = "dumbbell",
        bodyPart = "chest",
        onEdit = {},
        onDelete = {},
        showMore = true
    )
}