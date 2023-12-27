package com.example.power.ui.configure.Plan.exercise

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.data.room.bodyTypeMap
import com.example.power.data.room.exerciseTypeMap
import com.example.power.data.view_models.AppViewModelProvider
import com.example.power.data.view_models.exercise.ExerciseViewModel
import com.example.power.ui.SearchItem
import com.example.power.ui.configure.Plan.workout.BottomSheetEditAndDelete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun Exercises(
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit,
    showSnack: (String) -> Unit
) {
    val exercisesViewModel: ExerciseViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val searchText by exercisesViewModel.searchText.collectAsState()
    val exercises by exercisesViewModel.exercises.collectAsState()
    val workouts by exercisesViewModel.workouts.collectAsState()
    val scope = rememberCoroutineScope()
    var typeSelected by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("") }
    var partSelected by remember { mutableStateOf(false) }
    var selectedPart by remember { mutableStateOf("") }

    val onSelectType: (Boolean, String) -> Unit = { checked, itemName ->
        typeSelected = checked
        selectedType = itemName
        if (!checked){
            partSelected = false
            selectedPart = ""
        }
    }
    val onSelectBody: (Boolean, String) -> Unit = { checked, itemName ->
        partSelected = checked
        selectedPart = itemName
    }

    val typeActive: (String) -> Boolean  = { body -> selectedType == body || !typeSelected }
    val bodyActive: (String) -> Boolean =
        { type -> (selectedPart == type || !partSelected) && typeSelected }
    Column(modifier = modifier.fillMaxHeight()) {
        Spacer(modifier.heightIn(10.dp))
        SearchItem(searchVal = searchText, setVal = exercisesViewModel::onSearchTextChange)
        ExerciseFilterRow(typeActive = typeActive, bodyActive = bodyActive,
            onSelectBody = onSelectBody, onSelectType = onSelectType)
        LazyColumn() {
            items(exercises) { exercise ->
                val bodyType: String? = bodyTypeMap[exercise.body]
                val type: String? = exerciseTypeMap[exercise.type]
                val rightType = !typeSelected || selectedType == type
                val rightBody = !partSelected || selectedPart == bodyType
                val passesSearch = exercise.doesMatchSearchQuery(searchText)
                AnimatedVisibility(visible = rightType && rightBody && passesSearch) {
                    if (bodyType != null && type != null) {
                        ExerciseHolder(
                            showMore = true,
                            exerciseName = exercise.name,
                            bodyPart = bodyType,
                            onEdit = { onItemClick(exercise.name) },
                            onDelete = { exerciseName ->
                                scope.launch {
                                    val removed = exercisesViewModel.onDelete(exerciseName, workouts)
                                    if (!removed) {
                                        showSnack("It cannot be removed - it's used by a workout")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
        if (exercises.isEmpty())
            Text(
                text = "Click + to add an exercise",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 700,
    uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ExercisesPreview() {
    Exercises(onItemClick = {}, showSnack = {})
}

@Composable
fun CircleWithLetter(
    modifier: Modifier = Modifier,
    letter: String,
) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(text = letter, Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 25.sp
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewCircleWithLetter() {
    CircleWithLetter(letter = "h")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseHolder(
    modifier: Modifier = Modifier,
    exerciseName: String,
    bodyPart: String,
    onEdit: () -> Unit = {},
    onDelete: (String) -> Unit = {},
    showMore: Boolean = false
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
                dialogText = "This will delete this exercise permanently.",
                icon = Icons.Filled.Delete
            )
        }
    }
    var showBottomSheet by remember { mutableStateOf(false) }
    BottomSheetEditAndDelete(
        onEdit = onEdit,
        type = "Exercise",
        setOpenAlertDialog = {openAlertDialog = true},
        setShowBottomSheet = { showBottomSheet = it },
        showBottomSheet = showBottomSheet
    )
    GeneralHolder(
        modifier,
        itemName = exerciseName,
        secondaryInfo = bodyPart,
        onItemClick = onEdit,
    ) {
        if (showMore)
            IconButton(onClick = { showBottomSheet = true }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "show more options")
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun DissmissSheet(
    scope: CoroutineScope,
    sheetState: SheetState,
    setShowButtomSheet: (Boolean) -> Unit
) {
    scope.launch { sheetState.hide() }.invokeOnCompletion {
        if (!sheetState.isVisible) {
            setShowButtomSheet(false)
        }
    }
}

@Composable
fun GeneralHolder(
    modifier: Modifier = Modifier,
    itemName: String,
    secondaryInfo: String,
    onItemClick: () -> Unit,
    moreText: String = "",
    isMoreText: Boolean = false,
    endComposable: @Composable () -> Unit
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .clickable { onItemClick() }) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val firstLetter = itemName.firstOrNull().toString()
            CircleWithLetter(letter = firstLetter)
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .weight(1f)
            ) {
                Text(text = itemName, style = MaterialTheme.typography.titleMedium)
                Text(text = secondaryInfo, style = MaterialTheme.typography.bodyMedium)
                if (isMoreText)
                    Text(text = moreText, style = MaterialTheme.typography.bodyMedium)
            }
            endComposable()
        }

        Divider(Modifier.padding(start = 70.dp))
    }
}



@Composable
fun MyAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(imageVector = icon, contentDescription = "alert Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}
@Preview(showBackground = true)
@Composable
fun PreviewExerciseHolder() {
    ExerciseHolder(
        exerciseName = "dumbbell",
        bodyPart = "chest",
        onEdit = {},
        onDelete = {},
        showMore = true
    )
}

@Composable
fun ButtomSheetItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    text: String,
    onItemClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(bottom = 15.dp)
            .clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { }) {
            Icon(imageVector = imageVector, contentDescription = text)
        }
        Text(
            modifier = Modifier.padding(horizontal = 10.dp),
            text = text
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ButtomSheetItemPreview() {
    ButtomSheetItem(
        imageVector = Icons.Filled.Edit,
        text = "Edit name",
        onItemClick = {}
    )
}