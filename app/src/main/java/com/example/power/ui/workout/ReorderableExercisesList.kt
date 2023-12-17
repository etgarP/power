package com.example.power.ui.workout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.power.ui.GoodTextField
import com.example.power.ui.home.OutlinedCard

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReorderableSwipableItem(
    modifier: Modifier,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                onDismiss()
            }
            true
        }
    )
    MySwipeToDismiss(
        dismissState = dismissState,
        modifier = modifier
            .padding(vertical = 1.dp)
    ) {
        content()
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MySwipeToDismiss(
    modifier: Modifier = Modifier,
    dismissState: DismissState,
    content: @Composable () -> Unit
) {
    SwipeToDismiss(
        state = dismissState,
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color = Color.Red
            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Delete
                DismissDirection.EndToStart -> Icons.Default.Delete
            }
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
            )
            Box(modifier = Modifier.padding(4.dp)) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(horizontal = 20.dp),
                    contentAlignment = alignment
                ) {
                    Icon(
                        icon,
                        contentDescription = "Localized description",
                        modifier = Modifier.scale(scale)
                    )
                }
            }

        },
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun ExerciseComposable(
    modifier: Modifier = Modifier,
    exerciseName: String,
    setsNum: Int,
    setVal: (String) -> Unit,
    isDragging : Boolean
) {
    OutlinedCard(
        modifier = modifier,
        changeBackgroundColor = isDragging,
        mainContent = {
            Text(text = exerciseName, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.padding(1.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Number Of Sets:  ", style = MaterialTheme.typography.bodyMedium)
                GoodTextField(
                    value = "$setsNum",
                    onValueChange = { setVal(it) },
                    modifier = Modifier.width(50.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        })
    {}
}

@Composable
@Preview(showBackground = true)
fun PreviewExerciseHolder() {
    Column() {
        ExerciseComposable(
            exerciseName = "exerciseName",
            setsNum = 11,
            setVal = {},
            isDragging = false
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewList() {

}