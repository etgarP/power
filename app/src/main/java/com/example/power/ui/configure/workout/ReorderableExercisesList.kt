package com.example.power.ui.configure.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.power.data.CountdownTimer
import com.example.power.data.room.CardioExercise
import com.example.power.data.room.ExerciseHolder
import com.example.power.data.room.RepsExercise
import com.example.power.data.room.TimeExercise
import com.example.power.data.room.WeightExercise
import com.example.power.ui.PowerNotificationService
import com.example.power.ui.components.BottomSheetItem
import com.example.power.ui.components.GoodOutlinedButton
import com.example.power.ui.components.GoodTextField
import com.example.power.ui.components.OutlinedCard
import com.example.power.ui.components.dissmissSheet


/**
 * Can be either int or double MutableList
 */
sealed class DataList {
    abstract fun size(): Int
    abstract fun getValue(index: Int): String
    abstract fun setValue(index: Int, value: String)
    abstract fun addSet()
    abstract fun removeSet(index: Int)

    data class IntList(
        val values: MutableList<Int>
    ) : DataList() {
        override fun size() = values.size

        override fun getValue(index: Int) = values[index].toString()

        override fun setValue(index: Int, value: String) {
            values[index] = value.toIntOrNull() ?: 0
        }

        override fun addSet() {
            values.add(values.last())
        }

        override fun removeSet(index: Int) {
            values.removeAt(index)
        }
    }

    data class DoubleList(
        val values: MutableList<Double>
    ) : DataList() {
        override fun size() = values.size

        override fun getValue(index: Int) = values[index].toString()

        override fun setValue(index: Int, value: String) {
            values[index] = value.toDoubleOrNull() ?: 0.0
        }

        override fun addSet() {
            values.add(values.last())
        }

        override fun removeSet(index: Int) {
            values.removeAt(index)
        }
    }
}

/**
 * creates a different exercise composable with all of the info for different types of exercises
 */
@Composable
fun ExerciseHolderComposable(
    modifier: Modifier = Modifier,
    exerciseHolder: ExerciseHolder,
    setExerciseHolder: (ExerciseHolder) -> Unit,
    isDraggingThis: Boolean,
    onDismiss: () -> Unit,
    reorderable: Boolean,
    isActiveWorkout: Boolean,
    breakSet: Int,
    whenBreakChange: (String) -> Unit
) {
    var setsNum by remember { mutableIntStateOf(exerciseHolder.sets) }
    var breakTime by remember { mutableStateOf("$breakSet") }

    val onBreakChange: (String) -> Unit = {
        breakTime = it.toIntOrNull()?.coerceAtMost(3599)?.toString() ?: ""
        whenBreakChange(it)
    }

    @Composable
    fun GeneralExercise(
        firstCategory: DataList,
        secondCategory: DataList? = null,
        firstCategoryName: String,
        secondCategoryName: String? = null
    ) {
        // Forces the UI to refresh after modifying the underlying mutable lists.
        var refresh by remember { mutableIntStateOf(0) }

        GeneralExerciseComposable(
            modifier = modifier,
            isActiveWorkout = isActiveWorkout,
            exerciseName = exerciseHolder.exercise.name,
            isDraggingThis = isDraggingThis,
            onDismiss = onDismiss,
            setsNum = setsNum,
            isSecondCategory = secondCategory != null,
            firstCategoryName = firstCategoryName,
            secondCategoryName = secondCategoryName ?: "",
            firstCategory = firstCategory,
            secondCategory = secondCategory,
            refreshKey = refresh,

            atFirstCategoryChange = { index, value ->
                firstCategory.setValue(index, value)
                refresh++
                setExerciseHolder(exerciseHolder)
            },

            atSecondCategoryChange = { index, value ->
                secondCategory?.setValue(index, value)
                refresh++
                setExerciseHolder(exerciseHolder)
            },

            atAddSet = {
                firstCategory.addSet()
                secondCategory?.addSet()

                setsNum++
                exerciseHolder.sets = setsNum

                refresh++
                setExerciseHolder(exerciseHolder)
            },

            removeSetFromIndex = { index ->
                if (setsNum == 1) {
                    onDismiss()
                } else {
                    firstCategory.removeSet(index)
                    secondCategory?.removeSet(index)

                    setsNum--
                    exerciseHolder.sets = setsNum

                    refresh++
                    setExerciseHolder(exerciseHolder)
                }
            },

            reorderable = reorderable,
            breakTime = breakTime,
            onBreakChange = onBreakChange
        )
    }

    when (exerciseHolder) {
        is TimeExercise -> GeneralExercise(
            firstCategory = DataList.IntList(exerciseHolder.seconds),
            firstCategoryName = "Secs"
        )

        is RepsExercise -> GeneralExercise(
            firstCategory = DataList.IntList(exerciseHolder.reps),
            firstCategoryName = "Reps"
        )

        is CardioExercise -> GeneralExercise(
            firstCategory = DataList.IntList(exerciseHolder.km),
            firstCategoryName = "Km"
        )

        is WeightExercise -> GeneralExercise(
            firstCategory = DataList.DoubleList(exerciseHolder.weights),
            secondCategory = DataList.IntList(exerciseHolder.reps),
            firstCategoryName = "Weight",
            secondCategoryName = "Reps"
        )
    }

}

/**
 * a buttom sheet that allows to add or remove sets and set break times
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetInWorkout(
    onAddSet : () -> Unit,
    onRemoveSet : () -> Unit,
    onBreakChange : (String) -> Unit = {},
    breakTime: String,
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
                BottomSheetItem(
                    imageVector = Icons.Filled.Add,
                    text = "Add Set"
                ) {
                    dissmissSheet(scope, sheetState) { setShowBottomSheet(it) }
                    onAddSet()
                }
                BottomSheetItem(
                    imageVector = Icons.Filled.Remove,
                    text = "Remove Set",
                ) {
                    dissmissSheet(scope, sheetState) { setShowBottomSheet(it) }
                    onRemoveSet()
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "Set Break Time:",
                        modifier = Modifier.weight(1f)
                    )
                    GoodTextField(
                        value = breakTime,
                        onValueChange = { numString ->
                            onBreakChange(numString)
                        },
                        modifier = Modifier.width(60.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }
    }
}

/**
 * a more info buttom that on click opens bottom sheets
 */
@Composable
fun OnActiveExerciseButton(
    onAddSet: () -> Unit,
    onRemoveSet: () -> Unit,
    onBreakChange: (String) -> Unit,
    breakTime: String
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    IconButton(onClick = { showBottomSheet = true }) {
        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "more workout")
    }
    BottomSheetInWorkout(
        setShowBottomSheet = { showBottomSheet = it },
        showBottomSheet = showBottomSheet,
        onAddSet = onAddSet,
        onRemoveSet = onRemoveSet,
        onBreakChange = onBreakChange,
        breakTime = breakTime
    )
}

enum class DataType {
    INT,
    DOUBLE
}

/**
 * has the exercise header the body with all of the sets and bottom to add sets and set break time
 */
@Composable
fun GeneralExerciseComposable(
    modifier: Modifier = Modifier,
    isActiveWorkout: Boolean = false,
    exerciseName: String,
    isDraggingThis: Boolean,
    onDismiss: () -> Unit,
    setsNum: Int,
    isSecondCategory: Boolean = true,
    firstCategoryName: String,
    secondCategoryName: String = "",
    firstCategory: DataList,
    secondCategory: DataList? = null,
    refreshKey: Int = 0,
    atFirstCategoryChange: (Int, String) -> Unit,
    atSecondCategoryChange: (Int, String) -> Unit = { _, _ -> },
    atAddSet: () -> Unit,
    removeSetFromIndex: (Int) -> Unit,
    reorderable: Boolean,
    breakTime: String,
    onBreakChange: (String) -> Unit
) {
    var showBreak by remember { mutableStateOf(false) }
    val timer by remember { mutableStateOf(CountdownTimer(breakTime.toInt()))  }
    var currentTime by remember { mutableStateOf(timer.getFormattedTime()) }
    val isTime = firstCategoryName == "Secs" && !isSecondCategory
    val setCompletedStates = remember { List(setsNum) { false } }.toMutableList()

    OutlinedCard(
        padding = false,
        modifier = modifier,
        changeBackgroundColor = isDraggingThis,
        mainContent = {
            Column {
                // top part with the name and controls
                ExerciseHeader(exerciseName, reorderable, onDismiss, isActiveWorkout,
                    removeSetFromIndex, setsNum, atAddSet, onBreakChange, breakTime)
                // the body with the headers and sets with weights and such
                if (!reorderable) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    // the categories
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                    ) {
                        Text(
                            text = "Set",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                        )
                        Text(
                            text = firstCategoryName,
                            modifier = Modifier.widthIn(60.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        if (isSecondCategory) {
                            Text(
                                text = secondCategoryName,
                                modifier = Modifier.widthIn(60.dp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        if (isActiveWorkout) {
                            Text(
                                text = "",
                                modifier = Modifier.width(48.dp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.padding(4.dp))
                    val notificationService = PowerNotificationService(LocalContext.current)
                    for (i in 0 until setsNum) {
                        // the sets with weight km and such
                        SetsRow(
                            modifier = if (!isActiveWorkout) Modifier.padding(vertical = 4.dp)
                            else Modifier,
                            i = i,
                            atFirstCategoryChange = atFirstCategoryChange,
                            atSecondCategoryChange = atSecondCategoryChange,
                            firstValue = firstCategory.getValue(i),
                            secondValue = secondCategory?.getValue(i) ?: "",
                            isSecondCategory = isSecondCategory,
                            isActiveWorkout = isActiveWorkout,
                            isTime = isTime,
                            startNotification = {
                                notificationService.cancelAll()
                                notificationService.showBasicNotification()
                            },
                            onStart = { index ->
                                timer.loadTime(breakTime.toInt())
                                timer.start(
                                    onTick = {
                                        currentTime = it
                                        showBreak = true
                                    },
                                    onFinish = {
                                        showBreak = false
                                        notificationService.cancelAll()
                                        notificationService.showBasicNotification()
                                        timer.reset()
                                        setCompletedStates[index] = false
                                    }
                                )
                            }
                        )
                    }
                    if (!showBreak)
                        Spacer(modifier = Modifier.padding(4.dp))
                    BreaksButton(showBreak, currentTime) {
                        showBreak = false
                        timer.reset()
                    }
                }
            }
        })
    {}
}

/**
 * has the title and a more option or a delete btm
 */
@Composable
fun ExerciseHeader(
    exerciseName: String,
    reorderable: Boolean,
    onDismiss: () -> Unit,
    isActiveWorkout: Boolean,
    removeSetFromIndex: (Int) -> Unit,
    setsNum: Int,
    atAddSet: () -> Unit,
    onBreakChange: (String) -> Unit,
    breakTime: String
) {
    Row (verticalAlignment = Alignment.CenterVertically){
        Text(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .weight(1f),
            text = exerciseName,
            style = MaterialTheme.typography.titleMedium
        )
        if (reorderable)
            IconButton(onClick = { onDismiss() }) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "Close Exercise")
            }
        else {
            if (isActiveWorkout) {
                OnActiveExerciseButton(
                    onRemoveSet = { removeSetFromIndex(setsNum - 1) },
                    onAddSet = { atAddSet() },
                    onBreakChange = onBreakChange,
                    breakTime = breakTime
                )
            } else {
                IconButton(onClick = { removeSetFromIndex(setsNum - 1) }) {
                    Icon(imageVector = Icons.Filled.Remove, contentDescription = "Remove Set")
                }
                IconButton(onClick = { atAddSet() }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Set")
                }
            }

        }
    }
}

/**
 * a button that pops up when youre on break
 */
@Composable
fun BreaksButton(
    showBreak: Boolean,
    time: String,
    onCancelBreak: () -> Unit
) {
    AnimatedVisibility (
        visible = showBreak,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Row (
            horizontalArrangement = Arrangement.Center
        ){
            GoodOutlinedButton(text = "Break: $time left", onClick = onCancelBreak) {
                Spacer(modifier = Modifier.padding(3.dp))
                Icon(
                    imageVector = Icons.Filled.Stop,
                    contentDescription = "stop",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


/**
 * a set row with the weight or time or km or reps
 */
@Composable
fun SetsRow(
    modifier: Modifier = Modifier,
    i: Int,
    atFirstCategoryChange: (Int, String) -> Unit,
    atSecondCategoryChange: (Int, String) -> Unit,
    isSecondCategory: Boolean,
    firstValue: String,
    secondValue: String,
    isActiveWorkout: Boolean = false,
    isTime: Boolean = false,
    onStart: (Int) -> Unit,
    startNotification: () -> Unit
) {
    var checked by rememberSaveable { mutableStateOf(false) }

    val color by animateColorAsState(
        targetValue =
            if (checked) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface,
        label = "checked row color"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.background(color = color)
    ) {
        Text(
            text = "${i + 1}  ",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        )

        GoodTextField(
            value = firstValue,
            onValueChange = { atFirstCategoryChange(i, it) },
            modifier = Modifier.width(60.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.width(12.dp))

        if (isSecondCategory) {
            GoodTextField(
                value = secondValue,
                onValueChange = { atSecondCategoryChange(i, it) },
                modifier = Modifier.width(60.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.width(12.dp))
        }

        if (isActiveWorkout) {
            if (!isTime) {
                androidx.compose.material3.Checkbox(
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                        if (it) onStart(i)
                    }
                )
            } else {
                val seconds = firstValue.toIntOrNull() ?: 0
                val timer by remember {
                    mutableStateOf(CountdownTimer(seconds))
                }
                var workTime by remember {
                    mutableStateOf(timer.getFormattedTime())
                }

                timerForSet(
                    currentTime = workTime,
                    running = checked,
                    cancel = {
                        timer.reset()
                        checked = false
                    },
                    onStart = {
                        timer.loadTime(seconds)
                        checked = true
                        timer.start(
                            onTick = {
                                workTime = it
                            },
                            onFinish = {
                                startNotification()
                                timer.reset()
                                checked = false
                            }
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

/**
 * timer for the timed exercise
 * appears on the right of the set instead of the checkmark
 */
@Composable
fun timerForSet(
    onStart: () -> Unit = {},
    running: Boolean,
    currentTime: String,
    cancel: () -> Unit
) {
    var finished by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            if (!running) {
                if (finished) finished = false
                onStart()
            } else {
                cancel()
            }
        }) {
            Icon(
                imageVector =
                if (finished) Icons.Filled.RestartAlt
                else if (running) Icons.Filled.Stop
                else Icons.Filled.PlayArrow,
                contentDescription = "start set timer"
            )
        }
        if (running)
            Text(
                modifier = Modifier.padding(end = 7.dp),
                text = currentTime
            )
    }
}