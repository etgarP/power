package com.example.power.ui.home.QuickStartScreens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.power.data.room.Plan
import com.example.power.data.room.PlanType
import com.example.power.data.room.Workout
import com.example.power.data.room.planTypeToStringMap
import com.example.power.data.viewmodels.plan.FilterParams
import com.example.power.ui.components.TopBigTitleCard
import com.example.power.ui.workout.WorkoutCard

/**
 * the page that shows the plans that were filtered
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun chooseFilteredPlanPage(
    modifier: Modifier = Modifier,
    filterState: FilterParams = FilterParams(),
    plans: List<Plan>,
    onSelect: (Plan) -> Unit,
    filterPlans: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxSize(),

        ) {
        Text( // title
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "Pick A Plan",
            style = MaterialTheme.typography.titleLarge
        )
        val filteredList = if (filterPlans) plans.filter { plan -> // filtering the plans
            plan.matchesFilter(
                minPerWeek = filterState.minExercises,
                maxPerWeek = filterState.maxExercises,
                planType = filterState.planType
            )
        } else plans
        val pagerState = rememberPagerState(pageCount = { filteredList.size })
        if (filteredList.isNotEmpty())
            HorizontalPager( // a pager for flipping through the plans
                modifier = modifier.fillMaxSize(),
                state = pagerState,
                contentPadding = PaddingValues(
                    horizontal = 32.dp
                ),
            ) { i ->
                val padding by animateDpAsState( // animation the flip
                    if (pagerState.currentPage == i) 0.dp
                    else 10.dp, label = "resize plan select when unfocused"
                )
                OnePlanToChoose( // a page to choose a plan
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .padding(vertical = padding),
                    plan = filteredList[i],
                    onSelect = onSelect,
                    planName = filteredList[i].name,
                    numOfWorkouts = filteredList[i].workouts.size,
                    typeOfPlan = filteredList[i].planType,
                    workouts = filteredList[i].workouts,
                    numOfWeeks = filteredList[i].weeks
                )
            }
        else {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "There are no matching plans",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )
        }
        Spacer(modifier.heightIn(10.dp))
    }
}

/**
 *
 */
@Composable
fun OnePlanToChoose(
    modifier: Modifier = Modifier,
    plan: Plan,
    planName: String,
    numOfWorkouts: Int,
    typeOfPlan: PlanType,
    workouts: List<Workout>,
    numOfWeeks: Int,
    onSelect: (Plan) -> Unit,
) {
    var expanded by mutableStateOf(false)
    planTypeToStringMap[typeOfPlan]?.let {
        TopBigTitleCard( // a title to choose a plan
            modifier,
            title = planName,
            isDragging = false,
            onClick = { onSelect(plan) },
            btnText = "Choose This Plan" // the title
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Spacer(Modifier.padding(10.dp))
                Text( // the plan type
                    modifier = Modifier.padding(horizontal = 15.dp),
                    text = it,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.padding(10.dp))
                HorizontalDivider()
                Spacer(Modifier.padding(10.dp))
                Text( // num of weeks in the plan
                    modifier = Modifier.padding(horizontal = 15.dp),
                    text = "$numOfWeeks Weeks",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.padding(10.dp))
                HorizontalDivider()
                Spacer(Modifier.padding(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text( // number of workouts per week
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .weight(1f),
                        text = if (numOfWorkouts > 1) "$numOfWorkouts Workouts Per Week"
                        else "$numOfWorkouts Workout Per Week",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    IconButton(onClick = { expanded = !expanded }) { // option to see more workouts
                        Icon(
                            imageVector = if (!expanded) Icons.Filled.ExpandMore
                            else Icons.Filled.ExpandLess,
                            contentDescription = "see more"
                        )
                    }
                }
                Spacer(Modifier.padding(4.dp))
                Column(modifier = Modifier.animateContentSize()) {
                    if (expanded) {
                        for (workout in workouts) { // workout details
                            WorkoutCard(
                                modifier = Modifier.padding(bottom = 10.dp),
                                workout = workout,
                                isDragging = false
                            )
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}