package com.example.power.ui.workout

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.power.R
import com.example.power.data.view_models.workout.ExerciseHolderItem
import com.example.power.data.view_models.workout.WorkoutDetails
import com.example.power.ui.GoodTextField
import com.example.power.ui.home.OutlinedCard
import com.example.power.ui.home.Section

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun ReorderableExerciseHolderlist(
    workoutDetails: WorkoutDetails,
    removeExerciseHolder: (ExerciseHolderItem) -> Unit,
    onValueChange: (WorkoutDetails) -> Unit,
    swapItems: (Int, Int) -> Unit,
    getMore: () -> Unit,
    buttonComposable : @Composable () -> Unit,
    nameComposable: @Composable () -> Unit
) {
    var selectedItem: ExerciseHolderItem? by remember { mutableStateOf(null) }
    var list = workoutDetails.exercises
    LazyColumn {
        item {
            nameComposable()
        }
        item {
            Section(title = R.string.exercises, tailContent = {
                Column(
                    modifier = Modifier.clickable{ getMore() }
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "")
                }
            }) {}
        }
        items(list, key = { it.uniqueKey }) { item ->
            var setsNum by remember { mutableIntStateOf(item.exerciseHolder.sets) }
            ReorderableSwipableItem(
                modifier = Modifier.animateItemPlacement(),
                onDismiss = {
                    removeExerciseHolder(item)
                    list = list.toMutableList() - item
                    onValueChange(workoutDetails.copy(exercises = list))
                }) {
                ExerciseComposable(
                    exerciseName = item.exerciseHolder.exercise.name,
                    setsNum = setsNum,
                    showSwap = selectedItem == null,
                    showCheck = selectedItem != null && selectedItem != item,
                    setVal = {
                        try {
                            if (it.toInt() in 1..30){
                                item.exerciseHolder.sets = it.toInt()
                                setsNum = it.toInt()
                                onValueChange(workoutDetails.copy(exercises = list))
                            }
                        } catch (e: Exception) {}
                    },
                    onClick = {
                        selectedItem = if (selectedItem == null) {
                            item
                        } else {
                            val firstIndex = list.indexOf(selectedItem!!)
                            val secondIndex = list.indexOf(item)
                            if (firstIndex != -1 && secondIndex != -1) {
                                swapItems(firstIndex, secondIndex)
                            }
                            null
                        }
                    },
                )
            }

        }

        item {
            if (workoutDetails.exercises.isEmpty())
                Text(
                    text = "No exercises were added",
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
        }
        item {
            Box(modifier = Modifier
                .fillMaxWidth()
                .animateItemPlacement(),
                contentAlignment = Alignment.Center){
                buttonComposable()
            }
        }
    }
}

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
    onClick: () -> Unit,
    showSwap: Boolean,
    showCheck: Boolean
) {
    OutlinedCard(
        modifier,
        mainContent = {
        Text(text = exerciseName, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.padding(1.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Number Of Sets:  ", style = MaterialTheme.typography.bodyMedium)
            GoodTextField(
                value = "$setsNum",
                onValueChange = { setVal(it) },
                modifier = Modifier.width(50.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    })
    {
        if (showSwap || showCheck)
            IconButton(onClick = {
                onClick()
            }) {
                if (showSwap)
                    Icon(Icons.Filled.SwapVert, contentDescription = "more info")
                else
                    Icon(Icons.Filled.Check, contentDescription = "more info")
            }
    }

}

@Composable
@Preview(showBackground = true)
fun PreviewExerciseHolder() {
    Column() {
        ExerciseComposable(
            exerciseName = "exerciseName",
            setsNum = 11,
            setVal = {},
            onClick = {},
            showSwap = true,
            showCheck = false
        )
        ExerciseComposable(
            exerciseName = "exerciseName",
            setsNum = 11,
            setVal = {},
            onClick = {},
            showSwap = true,
            showCheck = false
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewList() {
    ReorderableExerciseHolderlist(
        workoutDetails = WorkoutDetails(),
        removeExerciseHolder = {},
        onValueChange = {},
        swapItems = { _, _ ->  },
        getMore = { /*TODO*/ },
        buttonComposable = { OutlinedButton(
            modifier = Modifier.padding(top = 15.dp),
            onClick = {}
        ) {
            Text(text = "hi")
        }
        }) {
        
    }
}
