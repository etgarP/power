package com.example.power.ui.home.QuickStartScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.power.data.viewmodels.plan.FilterParams
import com.example.power.ui.components.smallSelectableItem

/**
 * sets the amount of days you want to workout
 */
@Composable
fun WorkoutAmountPage(
    filterState: FilterParams,
    onFilterChange: (FilterParams) -> Unit,
    modifier: Modifier = Modifier,
    setValid: (Boolean) -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        var selected by remember { mutableStateOf(-1) }
        setValid(selected != -1)
        Row {
            // different number of days
            smallSelectableItem(
                modifier = Modifier.weight(1f),
                text = "1 - 2",
                onClick = {
                    selected = 0
                    onFilterChange(filterState.copy(minExercises = 1, maxExercises = 2))
                },
                selected = selected == 0
            )
            smallSelectableItem(
                modifier = Modifier.weight(1f),
                text = "3 - 4",
                onClick = {
                    selected = 1
                    onFilterChange(filterState.copy(minExercises = 3, maxExercises = 4))
                },
                selected = selected == 1
            )
        }
        Row {
            smallSelectableItem(
                modifier = Modifier.weight(1f),
                text = "5 - 6",
                onClick = {
                    selected = 2
                    onFilterChange(filterState.copy(minExercises = 5, maxExercises = 6))
                },
                selected = selected == 2
            )
            smallSelectableItem(
                modifier = Modifier.weight(1f),
                text = "7+",
                onClick = {
                    selected = 3
                    onFilterChange(filterState.copy(minExercises = 7, maxExercises = 100))
                },
                selected = selected == 3
            )
        }

    }
}