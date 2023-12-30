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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.power.data.view_models.AppViewModelProvider
import com.example.power.data.view_models.workout.WorkoutViewModel
import com.example.power.ui.SearchItem
import com.example.power.ui.configure.Plan.exercise.ButtomSheetItem
import com.example.power.ui.configure.Plan.exercise.DissmissSheet
import com.example.power.ui.configure.Plan.exercise.GeneralHolder
import com.example.power.ui.configure.Plan.exercise.MyAlertDialog
import kotlinx.coroutines.launch

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
        SearchItem(searchVal = searchText, setVal = workoutViewModel::onSearchTextChange)
        LazyColumn() {
            items(workouts) { workout ->
                val passesSearch = workout.doesMatchSearchQuery(searchText)
                AnimatedVisibility(visible = passesSearch) {
                    ExerciseComposable(
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseComposable(
    modifier: Modifier = Modifier,
    exerciseName: String,
    numOfExercises: Int,
    onEdit: () -> Unit,
    onItemClick: () -> Unit,
    onDelete: (String) -> Unit,
    showMore: Boolean = true
) {
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
    var showBottomSheet by remember { mutableStateOf(false) }
    BottomSheetEditAndDelete(
        onEdit = onEdit,
        type = "Workout",
        setOpenAlertDialog = {openAlertDialog = true},
        setShowBottomSheet = { showBottomSheet = it },
        showBottomSheet = showBottomSheet
    )
    GeneralHolder(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetEditAndDelete(
    onEdit : () -> Unit,
    type: String,
    setOpenAlertDialog : () -> Unit,
    setShowBottomSheet : (Boolean) -> Unit,
    showBottomSheet: Boolean
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                setShowBottomSheet(false)
            },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(bottom = 40.dp)) {
                ButtomSheetItem(
                    imageVector = Icons.Filled.Edit,
                    text = "Edit $type"
                ) {
                    DissmissSheet(scope, sheetState) { setShowBottomSheet(it) }
                    onEdit()
                }
                ButtomSheetItem(
                    imageVector = Icons.Filled.Delete,
                    text = "Delete $type",
                ) {
                    DissmissSheet(scope, sheetState) { setShowBottomSheet(it) }
                    setOpenAlertDialog()
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 700, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WorkoutPreview() {
    Workouts(onEdit = {}, showSnack = {}, onItemClick = {})
}