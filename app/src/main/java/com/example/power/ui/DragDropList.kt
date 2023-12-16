package com.example.power.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.power.R
import com.example.power.data.view_models.workout.ExerciseHolderItem
import com.example.power.data.view_models.workout.WorkoutDetails
import com.example.power.ui.home.Section
import com.example.power.ui.workout.ExerciseComposable
import com.example.power.ui.workout.ReorderableSwipableItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

val reorderItem = mutableListOf(
    "Item 1",
    "Item 2",
    "Item 3",
    "Item 4",
    "Item 5",
    "Item 6",
    "Item 7",
    "Item 8",
    "Item 9",
    "Item 10",
    "Item 11",
    "Item 12",
    "Item 13",
    "Item 14",
    "Item 15",
    "Item 16",
    "Item 17",
    "Item 18",
    "Item 19",
    "Item 20"
)
//@Preview(showBackground = true)
//@Composable
//fun DragPreview(modifier: Modifier = Modifier) {
//    var reorderItems by remember { mutableStateOf(reorderItem) }
//    DragDropListExample(
//        items = reorderItems,
//        onMove = { from, to ->
//            if (from != to) {
//                val element = reorderItem.removeAt(from)
//                reorderItems = reorderItems.toMutableList().apply {
//                    this.add(to, element)
//                }
//            }
//
//        }
//    )
//}


@Composable
fun DragDropListExample(
    items: List<String>,
    onMove: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var overScrollJob by remember { mutableStateOf<Job?>(null) }
    val dragDropListState = rememberDragDropListState(onMove = onMove)

    LazyColumn(
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
                    onDragStart = { offset -> dragDropListState.onDragStart(offset) },
                    onDragEnd = { dragDropListState.onDragInterrupted() },
                    onDragCancel = { dragDropListState.onDragInterrupted() }
                )
            },
        state = dragDropListState.lazyListState
    ) {
        itemsIndexed(items) { index, item ->
            Column(
                modifier = Modifier
                    .composed {
                        val offsetOrNull = dragDropListState.elementDisplacement.takeIf {
                            index == dragDropListState.currentIndexOfDraggedItem
                        }
                        Modifier.graphicsLayer {
                            translationY = offsetOrNull ?: 0f
                        }
                    }
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = item,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(10.dp))
            }


        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun onlyExercises(
    modifier: Modifier = Modifier,
    workoutDetails: WorkoutDetails,
    removeExerciseHolder: (ExerciseHolderItem) -> Unit,
    onValueChange: (WorkoutDetails) -> Unit,
    swapItems: (Int, Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var overScrollJob by remember { mutableStateOf<Job?>(null) }
    val dragDropListState = rememberDragDropListState(onMove = swapItems)
    var selectedItem: ExerciseHolderItem? by remember { mutableStateOf(null) }
    var list = workoutDetails.exercises
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
                    onDragStart = {
                        offset -> dragDropListState.onDragStart(offset)
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
            var setsNum by remember { mutableIntStateOf(item.exerciseHolder.sets) }
            ReorderableSwipableItem(
                modifier = Modifier
                    .composed {
                        val offsetOrNull = dragDropListState.elementDisplacement.takeIf {
                            item.exerciseHolder.position == dragDropListState.currentIndexOfDraggedItem
                        }
                        Modifier.graphicsLayer {
                            translationY = offsetOrNull ?: 0f
                        }
                    },
                onDismiss = {
                    removeExerciseHolder(item)
                    list = list.toMutableList() - item
                    onValueChange(workoutDetails.copy(exercises = list))
                }) {
                ExerciseComposable(
                    modifier = Modifier,
                    exerciseName = item.exerciseHolder.exercise.name,
                    setsNum = setsNum,
                    setVal = {
                        try {
                            item.exerciseHolder.sets = it.toInt()
                            setsNum = it.toInt()
                            onValueChange(workoutDetails.copy(exercises = list))
                        } catch (e: Exception) {
                            item.exerciseHolder.sets = 0
                            setsNum = 0
                            onValueChange(workoutDetails.copy(exercises = list))
                        }
                    },
                    isDragging = isDragging
                )
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragDropListExercises(
    modifier: Modifier = Modifier,
    workoutDetails: WorkoutDetails,
    removeExerciseHolder: (ExerciseHolderItem) -> Unit,
    onValueChange: (WorkoutDetails) -> Unit,
    swapItems: (Int, Int) -> Unit,
    getMore: () -> Unit,
    buttonComposable : @Composable () -> Unit,
    nameComposable: @Composable () -> Unit
) {
    Column(modifier) {
        nameComposable()
        Section(title = R.string.exercises, tailContent = {
            Column(
                modifier = Modifier.clickable{ getMore() }
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "")
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
        onlyExercises(
            modifier = Modifier.weight(1f),
            workoutDetails = workoutDetails,
            removeExerciseHolder = removeExerciseHolder,
            onValueChange = onValueChange,
            swapItems = swapItems,
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
            contentAlignment = Alignment.Center){
            buttonComposable()
        }
    }
}
