package com.example.power.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.power.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun PlanQuickStart() {
    var progress by remember { mutableStateOf(0f) }
    val currentProgress: Float by animateFloatAsState(progress)
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    Column {
        ProgressNavBar(
            currentProgress = currentProgress,
            onBack = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage -1)
                }
            }
        )
        PlanQuickStartPager(
            pagerState = pagerState,
            setProgressCount = { page ->
                loadProgress(
                    updateProgress = {
                        progress = it
                    },
                    screenPosition = page,
                    totalPages = 4
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
                modifier = Modifier.padding(10.dp),
                style = MaterialTheme.typography.titleLarge,
                text = title
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
                enabled = valid
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
fun WorkoutAmount(
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
                text = "1",
                onClick = { selected = 0 },
                selected = selected == 0
            )
            smallClickableItem(
                modifier = Modifier.weight(1f),
                text = "2",
                onClick = { selected = 1 },
                selected = selected == 1
            )
            smallClickableItem(
                modifier = Modifier.weight(1f),
                text = "3",
                onClick = { selected = 2 },
                selected = selected == 2
            )
        }
        Row {
            smallClickableItem(
                modifier = Modifier.weight(1f),
                text = "4",
                onClick = { selected = 3 },
                selected = selected == 3
            )
            smallClickableItem(
                modifier = Modifier.weight(1f),
                text = "5",
                onClick = { selected = 4 },
                selected = selected == 4
            )
            smallClickableItem(
                modifier = Modifier.weight(1f),
                text = "6+",
                onClick = { selected = 5 },
                selected = selected == 5
            )
        }

    }
}
@Composable
fun PageLevel(
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
            onClick = { selected = 0 },
            selected = selected == 0
        )
        ItemWithPicture(
            text = "Home (No Equipment)",
            imageId = R.drawable.home_workout,
            onClick = { selected = 1 },
            selected = selected == 1
        )
        ItemWithPicture(
            text = "Home (Dumbbells)",
            onClick = { selected = 2 },
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
            modifier = Modifier.width(60.dp),
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

// 1. what type of environment do you have access to,
// 2. what level are you
// 3. how many days a week do you plan to workout
// 4. Pick your Plan
@Composable
@OptIn(ExperimentalFoundationApi::class)
fun PlanQuickStartPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    setProgressCount: (Int) -> Unit
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
            title = if (page == 2) "Where do you plan to workout?"
            else if (page == 1) "What is your experience level?"
            else if (page == 0) "how many workout do you plan to do?"
            else "",
            valid = valid
        ) {
            if (page == 2)
                PageWorkoutEnvironment() {
                    valid = it
                }
            else if (page == 1) {
                PageLevel() {
                    valid = it
                }
            }
            else if (page == 0) {
                WorkoutAmount() {
                    valid = it
                }
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