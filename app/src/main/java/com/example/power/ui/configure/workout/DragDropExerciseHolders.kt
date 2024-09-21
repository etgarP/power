package com.example.power.ui.configure.Plan.workout

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Expand
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.power.R
import com.example.power.data.viewmodels.workout.ExerciseHolderItem
import com.example.power.data.viewmodels.workout.WorkoutDetails
import com.example.power.ui.configure.components.DragAndDropList
import com.example.power.ui.configure.components.Section
import com.example.power.ui.configure.workout.ExerciseHolderComposable
import kotlinx.coroutines.delay

/**
 * has the section that says exercises and give an
 * ability to reorder and add new exercises
 */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun DragDropListExercises(
    modifier: Modifier = Modifier,
    workoutDetails: WorkoutDetails,
    removeExerciseHolder: (ExerciseHolderItem) -> Unit,
    onValueChange: (WorkoutDetails) -> Unit,
    swapItems: (Int, Int) -> Unit,
    getMore: () -> Unit,
    isActiveWorkout: Boolean,
    nameComposable: @Composable () -> Unit
) {
    var reorderable by remember { mutableStateOf(!isActiveWorkout) }
    var flipReorderable : () -> Unit = {reorderable = !reorderable}
    Column(modifier) {
        onlyExercises(
            modifier = Modifier.weight(1f),
            workoutDetails = workoutDetails,
            onValueChange = onValueChange,
            swapItems = swapItems,
            removeExerciseHolder = removeExerciseHolder,
            reorderable = reorderable,
            isActiveWorkout = isActiveWorkout,
            topComposable = {
                nameComposable()
                // the top section
                Section(title = R.string.exercises, tailContent = {
                    Column(
                        modifier = Modifier.clickable{ flipReorderable() }
                    ) {
                        // ability to reorder the exercises or see more info
                        if (!reorderable)
                            Row {
                                Text(text = "Reorder ")
                                Icon(imageVector = Icons.Filled.MoveDown, contentDescription = "")
                            }
                        else
                            Row {
                                Text(text = "Expand ")
                                Icon(imageVector = Icons.Filled.Expand, contentDescription = "")
                            }
                    }
                    // area to add an exercise
                    Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                    Column(
                        modifier = Modifier.clickable{ getMore() }
                    ) {
                        Row {
                            Text(text = "Add ")
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "")
                        }

                    }

                }) {}
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
        )
    }
}

/**
 * has the exercises. if its reorderable then puts the exercises
 * in a closes fashion with ability to delete and reorder them
 * otherwise you get all the info about the sets and so on that you can edit.
 */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun onlyExercises(
    modifier: Modifier = Modifier,
    workoutDetails: WorkoutDetails,
    onValueChange: (WorkoutDetails) -> Unit,
    swapItems: (Int, Int) -> Unit,
    reorderable: Boolean,
    isActiveWorkout: Boolean,
    topComposable: @Composable () -> Unit,
    removeExerciseHolder: (ExerciseHolderItem) -> Unit
) {
    var list = workoutDetails.exercises
    if (reorderable) {
        topComposable()
        DragAndDropList(
            modifier = modifier,
            items = list,
            itemKey = {it.uniqueKey},
            onMove = swapItems
        ) { item , isDragging, modifierElement ->
            ExerciseHolderComposable(
                modifier = modifierElement,
                onDismiss = {
                    removeExerciseHolder(item)
                    list = list.toMutableList() - item
                    onValueChange(workoutDetails.copy(exercises = list))
                },
                exerciseHolder = item.exerciseHolder,
                isDraggingThis = isDragging,
                setExerciseHolder = {
                    item.exerciseHolder = it
                    onValueChange(workoutDetails.copy(exercises = list))
                },
                reorderable = reorderable,
                isActiveWorkout = isActiveWorkout,
                breakSet = item.exerciseHolder.breakTime,
                whenBreakChange = { numString ->
                    try {
                        val change = numString.toInt()
                        val num = if (change >= 3600) 3599 else change
                        item.exerciseHolder.breakTime = num
                        onValueChange(workoutDetails.copy(exercises = list))
                    } catch (_: Exception) {}
                }
            )
        }
    } else {
        Column (modifier = Modifier.verticalScroll(rememberScrollState())){
            topComposable()
            val gradualList = remember { mutableStateListOf<ExerciseHolderItem>() }
            LaunchedEffect(Unit) {
                for (item in list) {
                    delay(100)
                    gradualList += item
                }
            }
            for (item in gradualList) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(
                        animationSpec = tween(durationMillis = 500)
                    )
                ) {
                    ExerciseHolderComposable(
                        onDismiss = {
                            removeExerciseHolder(item)
                            list = list.toMutableList() - item
                            onValueChange(workoutDetails.copy(exercises = list))
                        },
                        exerciseHolder = item.exerciseHolder,
                        isDraggingThis = false,
                        setExerciseHolder = {
                            item.exerciseHolder = it
                            onValueChange(workoutDetails.copy(exercises = list))
                        },
                        reorderable = reorderable,
                        isActiveWorkout = isActiveWorkout,
                        breakSet = item.exerciseHolder.breakTime,
                        whenBreakChange = { numString ->
                            try {
                                val change = numString.toInt()
                                val num = if (change >= 3600) 3599 else change
                                item.exerciseHolder.breakTime = num
                                onValueChange(workoutDetails.copy(exercises = list))
                            } catch (_: Exception) {}
                        }
                    )
                }
            }
        }
    }
}