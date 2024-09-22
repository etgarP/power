package com.example.power.ui.home.QuickStartScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.power.R
import com.example.power.data.room.PlanType
import com.example.power.data.viewmodels.plan.FilterParams
import com.example.power.ui.components.ItemWithPicture

/**
 * choosing the type of workout environment
 * between gyn home and home dumbbells
 */
@Composable
fun PageWorkoutEnvironment(
    filterState: FilterParams,
    onFilterChange: (FilterParams) -> Unit,
    modifier: Modifier = Modifier,
    setValid: (Boolean) -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        var selected by remember { mutableStateOf(-1) }
        setValid(selected != -1)
        ItemWithPicture(
            text = "Gym",
            imageId = R.drawable.gym,
            onClick = {
                selected = 0
                onFilterChange(filterState.copy(planType = PlanType.GYM))
            },
            selected = selected == 0
        )
        ItemWithPicture(
            text = "Home (No Equipment)",
            imageId = R.drawable.home_workout,
            onClick = {
                selected = 1
                onFilterChange(filterState.copy(planType = PlanType.BODYWEIGHT))
            },
            selected = selected == 1
        )
        ItemWithPicture(
            text = "Home (Dumbbells)",
            onClick = {
                selected = 2
                onFilterChange(filterState.copy(planType = PlanType.DUMBBELLS))
            },
            selected = selected == 2,
            imageId = R.drawable.dumbbell
        )
    }
}