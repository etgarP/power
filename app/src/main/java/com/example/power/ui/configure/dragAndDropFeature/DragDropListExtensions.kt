package com.example.power.ui.configure.dragAndDropFeature

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState

/**
 * gets item info for position absolute
 */
fun LazyListState.getVisibleItemInfoFor(absolute: Int): LazyListItemInfo? {
    val firstIndex = this.layoutInfo.visibleItemsInfo.first().index
    val itemIndex = absolute - firstIndex
    return this.layoutInfo.visibleItemsInfo.getOrNull(itemIndex)
}

/**
 * gets the offsetEnd of an item
 */
val LazyListItemInfo.offsetEnd: Int
    get() = this.offset + this.size

/**
 * removes at one index and moves to another
 */
fun <T> MutableList<T>.move(from: Int, to: Int) {
    this.apply {
        if (from == to) return
        val element = this.removeAt(from) ?: return
        this.add(to, element)
    }
}