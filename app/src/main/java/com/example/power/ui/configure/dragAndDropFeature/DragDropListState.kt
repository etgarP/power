package com.example.power.ui.configure.dragAndDropFeature

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.Job

/**
 * returns a remember of the drag and drop list state
 */
@Composable
fun rememberDragDropListState(
    lazyListState: LazyListState = rememberLazyListState(),
    onMove: (Int, Int) -> Unit
): DragDropListState {
    return remember { DragDropListState(lazyListState = lazyListState, onMove = onMove) }
}

/**
 * saves a state of the drag and drop list
 */
class DragDropListState (
    // a state of the lazy list
    val lazyListState: LazyListState,
    // can enter what to do when an item was moved a position
    private val onMove: (Int, Int) -> Unit
) {

    /**
     * saved the distance the dragged item was dragged, starts at 0f
     */
    var draggedDistance by mutableStateOf(0f)

    /**
     * saves the lazy list item info of the dragged element when it was initially dragged
     */
    var initiallyDraggedElement by mutableStateOf<LazyListItemInfo?>(null)

    /**
     * saves the current index of the dragged item
     */
    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)

    /**
     * the offset and end offset at the start
     */
    val initialOffsets: Pair<Int, Int>?
        get() = initiallyDraggedElement?.let {
            Pair(it.offset, it.offsetEnd)
        }
    //
    val elementDisplacement: Float?
        get() = currentIndexOfDraggedItem
            ?.let {
                lazyListState.getVisibleItemInfoFor(absolute = it)
            }?.let {
                item -> (initiallyDraggedElement?.offset ?: 0f).toFloat() +
                    draggedDistance - item.offset
            }

    /**
     * gets the current info of the dragged item
     */
    val currentElement: LazyListItemInfo?
        get() = currentIndexOfDraggedItem?.let {
            lazyListState.getVisibleItemInfoFor(absolute = it)
        }
    //
    val overScrollJob by mutableStateOf<Job?>(null)

    /**
     * on drag start gets the lazy list item info of the object
     * and sets the index and initially dragged element
     */
    fun onDragStart(offset: Offset) {
        lazyListState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                offset.y.toInt() in item.offset..(item.offset + item.size)
            }?.also {
                currentIndexOfDraggedItem = it.index
                initiallyDraggedElement = it
            }
    }

    /**
     * when the drag gets stopped resets the parameters and stops the job
     */
    fun onDragInterrupted() {
        draggedDistance = 0f
        currentIndexOfDraggedItem = null
        initiallyDraggedElement = null
        overScrollJob?.cancel()
    }

    /**
     * on drag gets the object that we passed if we passed it
     * changes the index and invokes the onMove method
     */
    fun onDrag(offset: Offset) {
        draggedDistance += offset.y
        initialOffsets?.let { (topOffset, bottomOffset) ->
            val startOffset = topOffset + draggedDistance
            val endOffset = bottomOffset + draggedDistance
            currentElement?.let { hovered ->
                lazyListState.layoutInfo.visibleItemsInfo
                    .filterNot { item ->
                        item.offsetEnd < startOffset || item.offset > endOffset || hovered.index == item.index
                    }
                    .firstOrNull { item ->
                        val delta = startOffset - hovered.offset
                        when {
                            delta > 0 -> (endOffset > item.offsetEnd)
                            else -> (startOffset < item.offset)
                        }
                    }?.also { item ->
                        currentIndexOfDraggedItem?.let { current ->
                            onMove.invoke(current, item.index)
                        }
                        currentIndexOfDraggedItem = item.index
                    }
            }
        }
    }

    fun checkForOverScroll() : Float {
        return initiallyDraggedElement?.let {
            val startOffset = it.offset + draggedDistance
            val endOffset = it.offsetEnd + draggedDistance
            return@let when {
                draggedDistance > 0 -> (endOffset - lazyListState.layoutInfo.viewportEndOffset).takeIf { diff ->
                    diff > 0
                }
                draggedDistance < 0 -> (startOffset - lazyListState.layoutInfo.viewportStartOffset).takeIf { diff ->
                    diff < 0
                }
                else -> null
            }
        }?: 0f
    }
}