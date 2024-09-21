package com.example.power.ui.configure.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.power.ui.AppTopBar

/**
 * a navigation bar with a save btn on the right
 */
@Composable
fun RightHandNavButton(
    onClick: () -> Unit,
    valid: Boolean = true,
    isActiveWorkout: Boolean = false
) {
    IconButton(onClick = {
        if (valid)
            onClick()
    }) {
        Icon(
            imageVector = if (isActiveWorkout) Icons.Filled.Done else Icons.Filled.Save,
            contentDescription = "Save/Update",
            tint = if (valid) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error
        )
    }
}

@Preview
@Composable
fun RightHandNavButtonPreview() {
    AppTopBar(title = "title", backFunction = { }) {
        RightHandNavButton(onClick = { })
    }
}