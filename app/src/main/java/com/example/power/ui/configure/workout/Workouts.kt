package com.example.power.ui.configure.Plan.workout

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.data.viewmodels.AppViewModelProvider
import com.example.power.data.viewmodels.workout.WorkoutViewModel
import com.example.power.ui.components.BottomSheetEditAndDelete
import com.example.power.ui.components.CollapsedInfo
import com.example.power.ui.components.MyAlertDialog
import com.example.power.ui.components.SearchItem
import kotlinx.coroutines.launch

/**
 * holds all the workouts and the ability to edit lunch and search them
 */
@Composable
fun Workouts(
    modifier: Modifier = Modifier,
    onEdit: (String) -> Unit,
    onItemClick: (String) -> Unit,
    showSnack: (String) -> Unit
) {
    Column {
        WorkoutsPage(onEdit = onEdit, onItemClick = onItemClick, modifier = modifier, showSnack = showSnack)
    }
}

/**
 * holds all the workouts and the ability to edit lunch and search them
 */
@Composable
fun WorkoutsPage(
    modifier: Modifier = Modifier,
    onEdit: (String) -> Unit,
    onItemClick: (String) -> Unit,
    showSnack: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val workoutViewModel: WorkoutViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val searchText by workoutViewModel.searchText.collectAsState()
    val workouts by workoutViewModel.workouts.collectAsState()
    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier.heightIn(10.dp))
        // search bar
        SearchItem(searchVal = searchText, setVal = workoutViewModel::onSearchTextChange)
        // the workouts
        LazyColumn() {
            items(workouts) { workout ->
                val passesSearch = workout.doesMatchSearchQuery(searchText)
                AnimatedVisibility(visible = passesSearch) {
                    workoutComposable(
                        exerciseName = workout.name,
                        numOfExercises = workout.numOfExercises,
                        onEdit = { onEdit(workout.name) },
                        onItemClick = { onItemClick(workout.name) },
                        onDelete = {exerciseName ->
                            scope.launch {
                                val plans = workoutViewModel.getPlans()
                                val removed = workoutViewModel.onDelete(exerciseName, plans)
                                if (!removed) {
                                    showSnack("It cannot be removed - it's used by a plan")
                                }
                            }
                       },
                    )
                }
            }
        }
        if (workouts.isEmpty())
            Text(
                text = "Click + to add a workout",
                modifier = Modifier.fillMaxWidth().padding(15.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
    }
}

/**
 * a single collapsed workout has the ability to delete and edit it through the button
 * or lunch the workout by clicking it
 */
@Composable
fun workoutComposable(
    modifier: Modifier = Modifier,
    exerciseName: String,
    numOfExercises: Int,
    onEdit: () -> Unit,
    onItemClick: () -> Unit,
    onDelete: (String) -> Unit,
    showMore: Boolean = true
) {
    // deletion alert dialog
    var openAlertDialog by remember { mutableStateOf(false) }
    when {
        openAlertDialog -> {
            MyAlertDialog(
                onDismissRequest = { openAlertDialog = false },
                onConfirmation = {
                    onDelete(exerciseName)
                    openAlertDialog = false
                },
                dialogTitle = "Delete $exerciseName",
                dialogText = "This will delete this Workout permanently.",
                icon = Icons.Filled.Delete
            )
        }
    }
    // sheet for more options (edit and delete)
    var showBottomSheet by remember { mutableStateOf(false) }
    BottomSheetEditAndDelete(
        onEdit = onEdit,
        type = "Workout",
        setOpenAlertDialog = {openAlertDialog = true},
        setShowBottomSheet = { showBottomSheet = it },
        showBottomSheet = showBottomSheet
    )
    // the information with the ability to show more options if showMore is true
    CollapsedInfo(
        modifier = modifier,
        itemName = exerciseName,
        secondaryInfo = "$numOfExercises exercises",
        onItemClick = onItemClick
    ) {
        if(showMore)
            IconButton(onClick = { showBottomSheet = true }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "show more options")
            }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 700, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WorkoutPreview() {
    Workouts(onEdit = {}, showSnack = {}, onItemClick = {})
}