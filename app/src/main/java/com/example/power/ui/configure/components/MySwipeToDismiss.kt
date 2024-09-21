package com.example.power.ui.configure.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * shortens the code needs to build a swipable item
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipableItem(
    modifier: Modifier,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    // a state for dismissing
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

/**
 * a swipe to dismiss where the background is red and theres a delete icon on both
 * directions on dismiss
 */
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
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f, label = ""
            )
            Box(modifier = modifier) {
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