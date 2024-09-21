package com.example.power.ui.home

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.R
import com.example.power.data.room.Plan
import com.example.power.data.room.PlanType
import com.example.power.data.room.Workout
import com.example.power.data.room.planTypeToStringMap
import com.example.power.data.viewmodels.AppViewModelProvider
import com.example.power.data.viewmodels.plan.FilterParams
import com.example.power.data.viewmodels.plan.PlanViewModel
import com.example.power.ui.configure.components.TopBigTitleCard
import com.example.power.ui.workout.WorkoutCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun PlanQuickStart(
    onBack: () -> Unit = {},
    onSelect: (Plan) -> Unit = {}
) {
    val viewModel: PlanViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val plans by viewModel.plans.collectAsState()
    var progress by remember { mutableStateOf(0f) }
    val currentProgress: Float by animateFloatAsState(progress, label = "")
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    BackHandler {
        scope.launch {
            if (pagerState.currentPage == 0) onBack()
            else pagerState.animateScrollToPage(pagerState.currentPage -1)
        }
    }
    Column {
        ProgressNavBar(
            currentProgress = currentProgress,
            onBack = {
                scope.launch {
                    if (pagerState.currentPage == 0) onBack()
                    else pagerState.animateScrollToPage(pagerState.currentPage -1)
                }
            }
        )
        PlanQuickStartPager(
            pagerState = pagerState,
            plans = plans,
            onSelect = onSelect,
            filterState = viewModel.filterParamsState,
            onFilterChange = viewModel::updateFilterParams,
            setProgressCount = { page ->
                loadProgress(
                    updateProgress = {
                        progress = it
                    },
                    screenPosition = page,
                    totalPages = 3
                )
            }
        )
    }
}

@Composable
fun generalPageEnvironment(
    modifier: Modifier = Modifier,
    title: String,
    buttonText: String,
    valid: Boolean,
    onContinue: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(30.dp),
                style = MaterialTheme.typography.titleLarge,
                text = title,
                textAlign = TextAlign.Center
            )
        }
        Column {
            content()
        }
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                modifier = Modifier.padding(20.dp),
                onClick = { onContinue() },
                enabled = valid,
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
fun WorkoutAmount(
    filterState: FilterParams,
    onFilterChange: (FilterParams) -> Unit,
    modifier: Modifier = Modifier,
    setValid: (Boolean) -> Unit
) {
    Column (
        modifier = modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ){
        var selected by remember { mutableStateOf(-1) }
        setValid(selected != -1)
        Row {
            smallClickableItem(
                modifier = Modifier.weight(1f),
                text = "1 - 2",
                onClick = {
                    selected = 0
                    onFilterChange(filterState.copy(minExercises = 1, maxExercises = 2))
                },
                selected = selected == 0
            )
            smallClickableItem(
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
            smallClickableItem(
                modifier = Modifier.weight(1f),
                text = "5 - 6",
                onClick = {
                    selected = 2
                    onFilterChange(filterState.copy(minExercises = 5, maxExercises = 6))
                },
                selected = selected == 2
            )
            smallClickableItem(
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
@Composable
fun PageLevel(
    filterState: FilterParams,
    onFilterChange: (FilterParams) -> Unit,
    modifier: Modifier = Modifier,
    setValid: (Boolean) -> Unit
) {
    Column (
        modifier = modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        var selected by remember { mutableStateOf(-1) }
        setValid(selected != -1)
        ItemWithPicture(
            text = "Beginner",
            enableImage = false,
            imageId = R.drawable.easy,
            onClick = { selected = 0 },
            selected = selected == 0
        )
        ItemWithPicture(
            text = "Intermediate",
            enableImage = false,
            imageId = R.drawable.home_workout,
            onClick = { selected = 1 },
            selected = selected == 1
        )
        ItemWithPicture(
            text = "Advanced",
            enableImage = false,
            onClick = { selected = 2 },
            selected = selected == 2,
            imageId = R.drawable.dumbbell
        )
    }
}

@Composable
fun PageWorkoutEnvironment(
    filterState: FilterParams,
    onFilterChange: (FilterParams) -> Unit,
    modifier: Modifier = Modifier,
    setValid: (Boolean) -> Unit
) {
    Column (
        modifier = modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ){
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

@Composable
fun smallClickableItem(
    modifier: Modifier = Modifier,
    selected: Boolean = true,
    text: String = "",
    onClick: () -> Unit = {}
) {
    val color by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.surface, label = ""
    )
    Row(modifier, horizontalArrangement = Arrangement.Center) {
        Button(
            modifier = Modifier.width(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = color),
            contentPadding = PaddingValues(0.dp),
            onClick = { onClick() },
            shape = RoundedCornerShape(15.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(color)
            ) {
                Text(
                    modifier = Modifier
                        .padding(vertical = 20.dp, horizontal = 15.dp)
                        .weight(1f),
                    text = text,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

}
@Composable
fun ItemWithPicture(
    modifier: Modifier = Modifier,
    selected: Boolean = true,
    enableImage: Boolean = true,
    text: String = "",
    @DrawableRes imageId: Int,
    onClick: () -> Unit = {}
) {
    val color by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.surface, label = ""
    )
    Button(
        modifier = modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(0.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(15.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(color)
        ) {
            if (enableImage)
                Image(
                    modifier = Modifier
                        .height(70.dp)
                        .width(90.dp),
                    painter = painterResource(id = imageId),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                )
            Text(
                modifier = Modifier
                    .padding(vertical = 20.dp, horizontal = 15.dp)
                    .weight(1f),
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            if (selected)
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            else
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
        }
    }
}


@Composable
fun PlanHolderForChoose(
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
        TopBigTitleCard(
            modifier,
            title = planName,
            isDragging = false,
            onClick = { onSelect(plan) },
            btnText = "Choose This Plan"
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Spacer(Modifier.padding(10.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    text = it,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.padding(10.dp))
                HorizontalDivider()
                Spacer(Modifier.padding(10.dp))
                Text(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    text = "$numOfWeeks Weeks",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.padding(10.dp))
                HorizontalDivider()
                Spacer(Modifier.padding(4.dp))
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .weight(1f),
                        text = if (numOfWorkouts > 1) "$numOfWorkouts Workouts Per Week"
                            else "$numOfWorkouts Workout Per Week",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    IconButton(onClick = { expanded = !expanded }) {
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
                        for(workout in workouts){
                            WorkoutCard(modifier = Modifier.padding(bottom = 10.dp), workout = workout, isDragging = false)
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun chooseFilteredPlan(
    modifier: Modifier = Modifier,
    filterState: FilterParams = FilterParams(),
    plans: List<Plan>,
    onSelect: (Plan) -> Unit,
    filterPlans: Boolean = true
) {
    Column(
        modifier = modifier.fillMaxSize(),

    ) {
        Text(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "Pick A Plan",
            style = MaterialTheme.typography.titleLarge
        )
        val filteredList = if (filterPlans) plans.filter { plan ->
            plan.matchesFilter(
                minPerWeek = filterState.minExercises,
                maxPerWeek = filterState.maxExercises,
                planType = filterState.planType
            )
        } else plans
        val pagerState = rememberPagerState(pageCount = { filteredList.size })
        if (filteredList.isNotEmpty())
            HorizontalPager(
                modifier = modifier.fillMaxSize(),
                state = pagerState,
                contentPadding = PaddingValues(
                    horizontal = 32.dp
                ),
            ) { i ->
                val padding by animateDpAsState(
                    if (pagerState.currentPage == i) 0.dp
                    else 10.dp, label = "resize plan select when unfocused"
                )
                PlanHolderForChoose(
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



// 1. what type of environment do you have access to,
// 2. what level are you
// 3. how many days a week do you plan to workout
// 4. Pick your Plan
@Composable
@OptIn(ExperimentalFoundationApi::class)
fun PlanQuickStartPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    setProgressCount: (Int) -> Unit,
    filterState: FilterParams,
    onFilterChange: (FilterParams) -> Unit,
    plans: List<Plan>,
    onSelect: (Plan) -> Unit,
) {
    // Display 10 items
    val scope = rememberCoroutineScope()
    LaunchedEffect(pagerState.currentPage) {
        setProgressCount(pagerState.currentPage+1)
    }
    HorizontalPager(
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = false,
        state = pagerState
    ) { page ->
        var valid by remember{ mutableStateOf(false) }
        generalPageEnvironment(
            buttonText = "Continue",
            onContinue = {
                scope.launch {
                    pagerState.animateScrollToPage(page = page + 1)
                }
            },
            title = if (page == 0) "Where do you plan to workout?"
//            else if (page == 1) "What is your experience level?"
            else if (page == 1) "how many workout do you plan to do?"
            else "",
            valid = valid
        ) {
            if (page == 0)
                PageWorkoutEnvironment(
                    filterState = filterState,
                    onFilterChange = onFilterChange,
                ) {
                    valid = it
                }
//            else if (page == 1) {
//                PageLevel(
//                    filterState = filterState,
//                    onFilterChange = onFilterChange,
//                ) {
//                    valid = it
//                }
//            }
            else if (page == 1) {
                WorkoutAmount(
                    filterState = filterState,
                    onFilterChange = onFilterChange,
                ) {
                    valid = it
                }
            }
            else if (page == 2) {
                chooseFilteredPlan(
                    plans = plans,
                    filterState = filterState,
                    onSelect = onSelect
                )
            }
        }
    }
}
@Composable
fun ProgressNavBar(
    modifier: Modifier = Modifier,
    currentProgress: Float,
    onBack: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =  modifier.fillMaxWidth()
    ){
        IconButton(onClick = { onBack () }) {
            Icon(imageVector = Icons.Filled.ArrowBackIosNew, contentDescription = "back")
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            LinearProgressIndicator(
                progress = { currentProgress },
                modifier = Modifier
                    .width(100.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(16.dp)),
            )
        }
        Spacer(modifier = Modifier.padding(horizontal = 23.dp))
    }


}


/** Iterate the progress value */
fun loadProgress(updateProgress: (Float) -> Unit, screenPosition: Int, totalPages: Int) {
    updateProgress(screenPosition.toFloat() / totalPages)
}