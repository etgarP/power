package com.example.power.ui.configure.Plan.exercise
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChip(
    text: String,
    onSelect: (Boolean, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf(false) }

    FilterChip(
        modifier = modifier,
        onClick = {
            selected = !selected
            onSelect(selected, text)
                  },
        label = {
            Text(text)
        },
        selected = selected,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseFilterRow(
    modifier: Modifier = Modifier,
    typeActive: (String) -> Boolean,
    bodyActive: (String) -> Boolean,
    onSelectBody: (Boolean, String) -> Unit,
    onSelectType: (Boolean, String) -> Unit,
) {
    Row(modifier.horizontalScroll(rememberScrollState())) {

        /*
            the first category selection with cardio, weights, reps, and duration.
            it has a state to see if it should be shown, which is when either it was selected
            or when nothing is selected
        */
        Spacer(modifier = Modifier.width(4.dp))
        val isActive1 by rememberUpdatedState(typeActive("Cardio"))
        AnimatedVisibility(visible = isActive1) {
            FilterChip(
                modifier= Modifier.padding(horizontal = 4.dp),
                text = "Cardio",
                onSelect = onSelectType
            )
        }
        val isActive2 by rememberUpdatedState(typeActive("Weight"))
        AnimatedVisibility(visible = isActive2) {
            FilterChip(
                modifier= Modifier.padding(horizontal = 4.dp),
                text = "Weight",
                onSelect = onSelectType
            )
        }
        val isActive3 by rememberUpdatedState(typeActive("Reps"))
        AnimatedVisibility(visible = isActive3) {
            FilterChip(
                modifier= Modifier.padding(horizontal = 4.dp),
                text = "Reps",
                onSelect = onSelectType
            )
        }
        val isActive4 by rememberUpdatedState(typeActive("Duration"))
        AnimatedVisibility(visible = isActive4) {
            FilterChip(
                modifier= Modifier.padding(horizontal = 4.dp),
                text = "Duration",
                onSelect = onSelectType
            )
        }

        /*
            the second category selection, similar to before, but only shows if the
            first category is selected.
            categories: ("Core", "Arms", "Back", "Chest", "Legs", "Shoulders", "Cardio", "Other")
         */

        val isActiveA by rememberUpdatedState(bodyActive("Core"))
        AnimatedVisibility(visible = isActiveA) {
            FilterChip(
                modifier= Modifier.padding(horizontal = 4.dp),
                text = "Core",
                onSelect = onSelectBody
            )
        }
        val isActiveB by rememberUpdatedState(bodyActive("Arms"))
        AnimatedVisibility(visible = isActiveB) {
            FilterChip(
                modifier= Modifier.padding(horizontal = 4.dp),
                text = "Arms",
                onSelect = onSelectBody
            )
        }
        val isActiveC by rememberUpdatedState(bodyActive("Back"))
        AnimatedVisibility(visible = isActiveC) {
            FilterChip(
                modifier= Modifier.padding(horizontal = 4.dp),
                text = "Back",
                onSelect = onSelectBody
            )
        }
        val isActiveD by rememberUpdatedState(bodyActive("Chest"))
        AnimatedVisibility(visible = isActiveD) {
            FilterChip(
                modifier= Modifier.padding(horizontal = 4.dp),
                text = "Chest",
                onSelect = onSelectBody
            )
        }
        val isActiveE by rememberUpdatedState(bodyActive("Legs"))
        AnimatedVisibility(visible = isActiveE) {
            FilterChip(
                modifier= Modifier.padding(horizontal = 4.dp),
                text = "Legs",
                onSelect = onSelectBody
            )
        }
        val isActiveF by rememberUpdatedState(bodyActive("Shoulders"))
        AnimatedVisibility(visible = isActiveF) {
            FilterChip(
                modifier= Modifier.padding(horizontal = 4.dp),
                text = "Shoulders",
                onSelect = onSelectBody
            )
        }
        val isActiveG by rememberUpdatedState(bodyActive("Cardio"))
        AnimatedVisibility(visible = isActiveG) {
            FilterChip(
                modifier= Modifier.padding(horizontal = 4.dp),
                text = "Cardio",
                onSelect = onSelectBody
            )
        }
        val isActiveH by rememberUpdatedState(bodyActive("Other"))
        AnimatedVisibility(visible = isActiveH) {
            FilterChip(
                modifier= Modifier.padding(horizontal = 4.dp),
                text = "Other",
                onSelect = onSelectBody
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
    }
}
