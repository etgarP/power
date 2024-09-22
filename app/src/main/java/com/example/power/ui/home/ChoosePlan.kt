package com.example.power.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.R
import com.example.power.data.room.Plan
import com.example.power.data.viewmodels.AppViewModelProvider
import com.example.power.data.viewmodels.plan.FilterParams
import com.example.power.data.viewmodels.plan.PlanViewModel
import com.example.power.ui.components.ItemWithPicture
import com.example.power.ui.components.ProgressNavBar
import com.example.power.ui.home.QuickStartScreens.PageWorkoutEnvironment
import com.example.power.ui.home.QuickStartScreens.WorkoutAmountPage
import com.example.power.ui.home.QuickStartScreens.chooseFilteredPlanPage
import kotlinx.coroutines.launch

/**
 * a quick start page for choosing a plan.
 *
 */
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

// currently not implemented
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

/**
 * a pager that goes through all of the questions
 * and the final page is the filtered plans
 */
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
    // when the page changes updates the current page
    val scope = rememberCoroutineScope()
    LaunchedEffect(pagerState.currentPage) {
        setProgressCount(pagerState.currentPage+1)
    }
    // pager for the different pages of the questioner
    HorizontalPager(
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = false,
        state = pagerState
    ) { page ->
        var valid by remember{ mutableStateOf(false) }
        ContentWrapper(
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
                // choosing which types of workout
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
                // how many days to workout
                WorkoutAmountPage(
                    filterState = filterState,
                    onFilterChange = onFilterChange,
                ) {
                    valid = it
                }
            }
            else if (page == 2) {
                // shows the matching plans
                chooseFilteredPlanPage(
                    plans = plans,
                    filterState = filterState,
                    onSelect = onSelect
                )
            }
        }
    }
}


/** Iterate the progress value */
fun loadProgress(updateProgress: (Float) -> Unit, screenPosition: Int, totalPages: Int) {
    updateProgress(screenPosition.toFloat() / totalPages)
}

/**
 * Holds the title for the page and a button on the bottom
 */
@Composable
fun ContentWrapper(
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