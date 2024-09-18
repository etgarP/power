package com.example.power.ui.configure.Plan.workout

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.power.R
import com.example.power.data.viewmodels.workout.ExerciseHolderItem
import com.example.power.data.viewmodels.workout.WorkoutDetails
import com.example.power.ui.configure.Section
import com.example.power.ui.configure.performHapticFeedback
import com.example.power.ui.configure.workout.ExerciseHolderComposable
import com.example.power.ui.rememberDragDropListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var overScrollJob by remember { mutableStateOf<Job?>(null) }
    val dragDropListState = rememberDragDropListState(onMove = swapItems)
    var selectedItem: ExerciseHolderItem? by remember { mutableStateOf(null) }
    var list = workoutDetails.exercises
    if (reorderable) {
        topComposable()
        LazyColumn (
            modifier = modifier
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDrag = { change, offset ->
                            change.consume()
                            dragDropListState.onDrag(offset = offset)
                            if (overScrollJob?.isActive == true)
                                return@detectDragGesturesAfterLongPress
                            dragDropListState
                                .checkForOverScroll()
                                .takeIf { it != 0f }
                                ?.let {
                                    overScrollJob = scope.launch {
                                        dragDropListState.lazyListState.scrollBy(it)
                                    }
                                } ?: kotlin.run { overScrollJob?.cancel() }
                        },
                        onDragStart = {offset ->
                            performHapticFeedback(context)
                            dragDropListState.onDragStart(offset)
                        },
                        onDragEnd = {
                            dragDropListState.onDragInterrupted()
                        },
                        onDragCancel = {
                            dragDropListState.onDragInterrupted()
                        }
                    )
                },
            state = dragDropListState.lazyListState
        ) {

            items(list, key = { it.uniqueKey }) { item ->
                var isDragging = dragDropListState.currentIndexOfDraggedItem == item.exerciseHolder.position

                ExerciseHolderComposable(
                    modifier =
                    if (reorderable)
                        Modifier
                            .composed {
                                val offsetOrNull = dragDropListState.elementDisplacement.takeIf {
                                    item.exerciseHolder.position == dragDropListState.currentIndexOfDraggedItem
                                }
                                Modifier.graphicsLayer {
                                    translationY = offsetOrNull ?: 0f
                                }
                            }
                    else Modifier,
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
                        } catch (e: Exception) {
//                        onValueChange(workoutDetails.copy())
                        }
                    }
                )
            }
        }
    } else
        Column (modifier = Modifier.verticalScroll(rememberScrollState())){
            topComposable()
            val gradualList = remember { mutableStateListOf<ExerciseHolderItem>() }
            LaunchedEffect(Unit) {
                for (item in list) {
                    delay(100) // Wait for 1000 milliseconds (1 second)
                    gradualList += item
                }
            }
            for (item in gradualList) {
                AnimatedVisibility(
                    visible = true, // Initially set to true to trigger the animation
                    enter = fadeIn(
                        // Define the fade-in animation parameters
                        animationSpec = tween(durationMillis = 500) // Adjust duration as needed
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
                            } catch (e: Exception) {
//                        onValueChange(workoutDetails.copy())
                            }
                        }
                    )
                }

            }
        }
}

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
                Section(title = R.string.exercises, tailContent = {
                    Column(
                        modifier = Modifier.clickable{ flipReorderable() }
                    ) {
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
