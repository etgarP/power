package com.example.power.ui.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.power.data.viewmodels.plan.PlanDetails
import com.example.power.data.viewmodels.plan.WorkoutItem
import com.example.power.ui.configure.dragAndDropFeature.rememberDragDropListState
import com.example.power.ui.configure.performHapticFeedback
import com.example.power.ui.workout.WorkoutCard
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * a swipeable drag and drop list. can put whatever kind of object to be displayed in it
 * input:
 * items: the list of items,
 * item key: the unique key of the item,
 * on move: what to do when the object is moved like changing the list
 * on remove: what to when the item is removed like removing it from the list
 * item content: the actual content of the item
 */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun <T> SwipeableDragAndDropList(
    modifier: Modifier = Modifier,
    items: List<T>,
    itemKey: (T) -> Any,
    onMove: (Int, Int) -> Unit,
    onRemove: (T) -> Unit,
    itemContent: @Composable (T, Boolean) -> Unit // Custom item composable with dragging state
) {
    val context = LocalContext.current
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
                        if (overScrollJob?.isActive == true) return@detectDragGesturesAfterLongPress
                        dragDropListState
                            .checkForOverScroll()
                            .takeIf { it != 0f }
                            ?.let {
                                overScrollJob = scope.launch {
                                    dragDropListState.lazyListState.scrollBy(it)
                                }
                            } ?: kotlin.run { overScrollJob?.cancel() }
                    },
                    onDragStart = { offset ->
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
        items(items, key = { itemKey(it) }) { item ->
            val isDragging = dragDropListState.currentIndexOfDraggedItem == items.indexOf(item)
            SwipableItem (
                modifier = Modifier
                    .composed {
                        val offsetOrNull = dragDropListState.elementDisplacement.takeIf {
                            items.indexOf(item) == dragDropListState.currentIndexOfDraggedItem
                        }
                        Modifier.graphicsLayer {
                            translationY = offsetOrNull ?: 0f
                        }
                    }
                    .padding(vertical = 4.dp),
                onDismiss = {
                    onRemove(item)
                }
            ) {
                // Pass the item to the custom content composable
                itemContent(item, isDragging)
            }
        }
    }
}

/**
 * a drag and drop list (not swipe-able). can put whatever kind of object to be displayed in it
 * input:
 * items: the list of items,
 * item key: the unique key of the item,
 * on move: what to do when the object is moved like changing the list
 * item content: the actual content of the item
 */
@SuppressLint("UnnecessaryComposedModifier")
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun <T> DragAndDropList(
    modifier: Modifier = Modifier,
    items: List<T>,
    itemKey: (T) -> Any,
    onMove: (Int, Int) -> Unit,
    itemContent: @Composable (T, Boolean, Modifier) -> Unit // Custom item composable with dragging state
) {
    val context = LocalContext.current
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
                        if (overScrollJob?.isActive == true) return@detectDragGesturesAfterLongPress
                        dragDropListState
                            .checkForOverScroll()
                            .takeIf { it != 0f }
                            ?.let {
                                overScrollJob = scope.launch {
                                    dragDropListState.lazyListState.scrollBy(it)
                                }
                            } ?: kotlin.run { overScrollJob?.cancel() }
                    },
                    onDragStart = { offset ->
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
        items(items, key = { itemKey(it) }) { item ->
            val isDragging = dragDropListState.currentIndexOfDraggedItem == items.indexOf(item)
            val modifierElement = Modifier.composed {
                val offsetOrNull = dragDropListState.elementDisplacement.takeIf { isDragging }
                Modifier.graphicsLayer {
                    translationY = offsetOrNull ?: 0f
                }
            }
            itemContent(item, isDragging, modifierElement)
        }
    }
}
