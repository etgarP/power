package com.example.power.ui.configure.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.rememberDismissState
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.power.data.CountdownTimer
import com.example.power.data.room.CardioExercise
import com.example.power.data.room.ExerciseHolder
import com.example.power.data.room.RepsExercise
import com.example.power.data.room.TimeExercise
import com.example.power.data.room.WeightExercise
import com.example.power.ui.GoodTextField
import com.example.power.ui.PowerNotificationService
import com.example.power.ui.configure.OutlinedCard
import com.example.power.ui.configure.Plan.exercise.ButtomSheetItem
import com.example.power.ui.configure.Plan.exercise.DissmissSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipableItem(
    modifier: Modifier,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                onDismiss()
            }
            true
        }
    )
    MySwipeToDismiss(
        dismissState = dismissState,
        modifier = modifier
            .padding(vertical = 1.dp)
    ) {
        content()
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MySwipeToDismiss(
    modifier: Modifier = Modifier,
    dismissState: DismissState,
    content: @Composable () -> Unit
) {
    SwipeToDismiss(
        state = dismissState,
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color = Color.Red
            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Delete
                DismissDirection.EndToStart -> Icons.Default.Delete
            }
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f, label = ""
            )
            Box(modifier = modifier) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(color)
                        .padding(horizontal = 20.dp),
                    contentAlignment = alignment
                ) {
                    Icon(
                        icon,
                        contentDescription = "Localized description",
                        modifier = Modifier.scale(scale)
                    )
                }
            }

        },
        modifier = modifier
    ) {
        content()
    }
}

data class Set (
    val first : Int,
    val second : Int
)

fun setToList(list1: List<Int>, list2: List<Int>?) : List<Set> {
    val list : MutableList<Set> = mutableListOf()
    for (i in list1.indices) {
        val set = list2?.let { Set(list1.elementAt(i), it.elementAt(i)) }
        if (set != null)
            list.add(set)
        else
            list.add(Set(list1.elementAt(i), 0))
    }
    return list
}

data class SetWight (
    val first : Double,
    val second : Int
)

fun setToListWeight(list1: List<Double>, list2: List<Int>?) : List<SetWight> {
    val list : MutableList<SetWight> = mutableListOf()
    for (i in list1.indices) {
        val set = list2?.let { SetWight(list1.elementAt(i), it.elementAt(i)) }
        if (set != null)
            list.add(set)
        else
            list.add(SetWight(list1.elementAt(i), 0))
    }
    return list
}

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
    var onBreakChange : (String) -> Unit = {
        breakTime = try {
            val change = it.toInt()
            val num = if (change >= 3600) 3599 else change
            "$num"
        } catch (e: Exception) {
            ""
        }
        whenBreakChange(it)
    }

    when (exerciseHolder) {
        is TimeExercise -> {
            var setList by remember { mutableStateOf(setToList(exerciseHolder.seconds, null)) }
            GeneralExerciseComposable(
                modifier = modifier,
                isActiveWorkout = isActiveWorkout,
                setsNum = setsNum,
                exerciseName = exerciseHolder.exercise.name,
                isDraggingThis = isDraggingThis,
                onDismiss = onDismiss,
                isSecondCategory = false,
                firstCategoryName = "Secs",
                atFirstCategoryChange = { index, string ->
                    try {
                        exerciseHolder.seconds[index] = string.toInt()
                    } catch (e: Exception) {
                        exerciseHolder.seconds[index] = 0
                    } finally {
                        setExerciseHolder(exerciseHolder)
                        setList = setToList(exerciseHolder.seconds, null)
                    }
                },
                atAddSet = {
                    exerciseHolder.seconds.add(exerciseHolder.seconds[setsNum-1])
                    setList = setToList(exerciseHolder.seconds, null)
                    exerciseHolder.sets += 1
                    setExerciseHolder(exerciseHolder)
                    setsNum += 1
                },
                removeSetFromIndex = { index ->
                    if (setsNum == 1) {
                        onDismiss()
                    } else {
                        exerciseHolder.seconds.removeAt(index)
                        setList = setToList(exerciseHolder.seconds, null)
                        exerciseHolder.sets -= 1
                        setExerciseHolder(exerciseHolder)
                        setsNum -= 1
                    }
                },
                reorderable = reorderable,
                sets = setList,
                breakTime = breakTime,
                onBreakChange = onBreakChange
            )
        }

        is RepsExercise -> {
            var setList by remember { mutableStateOf( setToList(exerciseHolder.reps, null)) }
            GeneralExerciseComposable(
                modifier = modifier,
                isActiveWorkout = isActiveWorkout,
                exerciseName = exerciseHolder.exercise.name,
                isDraggingThis = isDraggingThis,
                onDismiss = onDismiss,
                setsNum = setsNum,
                isSecondCategory = false,
                firstCategoryName = "Reps",
                sets = setList,
                atFirstCategoryChange = { index, string ->
                    try {
                        exerciseHolder.reps[index] = string.toInt()
                    } catch (e: Exception) {
                        exerciseHolder.reps[index] = 0
                    } finally {
                        setExerciseHolder(exerciseHolder)
                        setList = setToList(exerciseHolder.reps, null)
                    }
                },
                atAddSet = {
                    exerciseHolder.reps.add(exerciseHolder.reps[setsNum-1])
                    setList = setToList(exerciseHolder.reps, null)
                    exerciseHolder.sets += 1
                    setExerciseHolder(exerciseHolder)
                    setsNum += 1
                },
                removeSetFromIndex = { index ->
                    if (setsNum == 1) {
                        onDismiss()
                    } else {
                        exerciseHolder.reps.removeAt(index)
                        setList = setToList(exerciseHolder.reps, null)
                        exerciseHolder.sets -= 1
                        setExerciseHolder(exerciseHolder)
                        setsNum -= 1
                    }
                },
                reorderable = reorderable,
                breakTime = breakTime,
                onBreakChange = onBreakChange
            )
        }

        is CardioExercise -> {
            var setList by remember { mutableStateOf( setToList(exerciseHolder.seconds, exerciseHolder.km)) }
            GeneralExerciseComposable(
                modifier = modifier,
                isActiveWorkout = isActiveWorkout,
                exerciseName = exerciseHolder.exercise.name,
                isDraggingThis = isDraggingThis,
                onDismiss = onDismiss,
                setsNum = setsNum,
                firstCategoryName = "Secs",
                sets = setList,
                secondCategoryName = "Km",
                atFirstCategoryChange = { index, string ->
                    try {
                        exerciseHolder.seconds[index] = string.toInt()
                    } catch (e: Exception) {
                        exerciseHolder.seconds[index] = 0
                    } finally {
                        setExerciseHolder(exerciseHolder)
                        setList = setToList(exerciseHolder.seconds, exerciseHolder.km)
                    }
                },
                atSecondCategoryChange = { index, string ->
                    try {
                        exerciseHolder.km[index] = string.toInt()
                    } catch (e: Exception) {
                        exerciseHolder.km[index] = 0
                    } finally {
                        setExerciseHolder(exerciseHolder)
                        setList = setToList(exerciseHolder.seconds, exerciseHolder.km)
                    }
                },
                atAddSet = {
                    exerciseHolder.seconds.add(exerciseHolder.seconds[setsNum-1])
                    exerciseHolder.km.add(exerciseHolder.km[setsNum-1])
                    setList = setToList(exerciseHolder.seconds, exerciseHolder.km)
                    exerciseHolder.sets += 1
                    setExerciseHolder(exerciseHolder)
                    setsNum += 1
                },
                removeSetFromIndex = { index ->
                    if (setsNum == 1) {
                        onDismiss()
                    } else {
                        exerciseHolder.seconds.removeAt(index)
                        exerciseHolder.km.removeAt(index)
                        setList = setToList(exerciseHolder.seconds, exerciseHolder.km)
                        exerciseHolder.sets -= 1
                        setExerciseHolder(exerciseHolder)
                        setsNum -= 1
                    }
                },
                reorderable = reorderable,
                breakTime = breakTime,
                onBreakChange = onBreakChange
            )
        }

        is WeightExercise -> {
            var setList by remember { mutableStateOf(setToListWeight(exerciseHolder.weights, exerciseHolder.reps)) }
            val scope = rememberCoroutineScope()
            GeneralExerciseComposable(
                modifier = modifier,
                isActiveWorkout = isActiveWorkout,
                exerciseName = exerciseHolder.exercise.name,
                isDraggingThis = isDraggingThis,
                onDismiss = onDismiss,
                setsNum = setsNum,
                firstCategoryName = "Weight",
                setsWeight = setList,
                secondCategoryName = "Reps",
                atFirstCategoryChange = { index, string ->
                    scope.launch {
                        try {
                            exerciseHolder.weights[index] = string.toDouble()
                        } catch (e: Exception) {
                            if (string == "")
                                exerciseHolder.weights[index] = 0.0
                        } finally {
                            setExerciseHolder(exerciseHolder)
                            setList = setToListWeight(exerciseHolder.weights, exerciseHolder.reps)
                        }
                    }

                },
                atSecondCategoryChange = { index, string ->
                    try {
                        exerciseHolder.reps[index] = string.toInt()
                    } catch (e: Exception) {
                        exerciseHolder.reps[index] = 0
                    } finally {
                        setExerciseHolder(exerciseHolder)
                        setList = setToListWeight(exerciseHolder.weights, exerciseHolder.reps)
                    }
                },
                atAddSet = {
                    exerciseHolder.weights.add(exerciseHolder.weights[setsNum-1])
                    exerciseHolder.reps.add(exerciseHolder.reps[setsNum-1])
                    setList = setToListWeight(exerciseHolder.weights, exerciseHolder.reps)
                    exerciseHolder.sets += 1
                    setExerciseHolder(exerciseHolder)
                    setsNum += 1
                },
                removeSetFromIndex = { index ->
                    if (setsNum == 1) {
                        onDismiss()
                    } else {
                        exerciseHolder.weights.removeAt(index)
                        exerciseHolder.reps.removeAt(index)
                        setList = setToListWeight(exerciseHolder.weights, exerciseHolder.reps)
                        exerciseHolder.sets -= 1
                        setExerciseHolder(exerciseHolder)
                        setsNum -= 1
                    }
                },
                reorderable = reorderable,
                breakTime = breakTime,
                onBreakChange = onBreakChange
            )
        }

    }
}

@Composable
fun ButtonSheetBreakItem() {
    
}

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
                ButtomSheetItem(
                    imageVector = Icons.Filled.Add,
                    text = "Add Set"
                ) {
                    DissmissSheet(scope, sheetState) { setShowBottomSheet(it) }
                    onAddSet()
                }
                ButtomSheetItem(
                    imageVector = Icons.Filled.Remove,
                    text = "Remove Set",
                ) {
                    DissmissSheet(scope, sheetState) { setShowBottomSheet(it) }
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
    setsWeight: List<SetWight>? = null,
    sets: List<Set>? = null,
    secondCategoryName: String = "",
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
    var time by remember { mutableStateOf(timer.getFormattedTime()) }
    OutlinedCard(
        padding = false,
        modifier = modifier,
        changeBackgroundColor = isDraggingThis,
        mainContent = {
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
            AnimatedVisibility (!reorderable) {
                Column {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
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
                        SetsRow(
                            modifier = if (!isActiveWorkout) Modifier.padding(vertical = 4.dp)
                                else Modifier,
                            i = i,
                            atFirstCategoryChange = atFirstCategoryChange,
                            atSecondCategoryChange = atSecondCategoryChange,
                            firstValDouble = if (setsWeight != null) setsWeight[i].first else -1.0,
                            firstValInt = if (sets != null) sets[i].first else -1,
                            isSecondCategory = isSecondCategory,
                            secondVal = if (setsWeight != null) setsWeight[i].second
                                else if (sets != null) sets[i].second
                                else -1,
                            isActiveWorkout = isActiveWorkout
                        ) {
                            showBreak = true
                            timer.loadTime(breakTime.toInt())
                            timer.start(
                                onTick = { time = it },
                                onFinish = {
                                    showBreak = false
                                    notificationService.showBasicNotification()
                                })
                        }
                    }
                    if (!showBreak)
                        Spacer(modifier = Modifier.padding(4.dp))
                }
            }
            AnimatedVisibility (
                visible = showBreak,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Row (
                    horizontalArrangement = Arrangement.Center
                ){
                    GoodOutlinedButton(text = "Break: $time left", onClick = {
                        showBreak = false
                    }) {
                        Spacer(modifier = Modifier.padding(3.dp))
                        Icon(
                            imageVector = Icons.Filled.Stop,
                            contentDescription = "stop",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        })
    {}
}

@Composable
fun GoodOutlinedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    textColor: Color = MaterialTheme.colorScheme.primary,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    endComposable: @Composable () -> Unit = {}
) {
        OutlinedButton(
            modifier = modifier,
            onClick = onClick,
            border = BorderStroke(
                ButtonDefaults.OutlinedBorderSize,
                borderColor
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = backgroundColor
            )
        ) {
            Text(text = text, color = textColor)
            endComposable()
        }

}


@Composable
fun SetsRow(
    modifier: Modifier = Modifier,
    i: Int,
    atFirstCategoryChange: (Int, String) -> Unit,
    atSecondCategoryChange: (Int, String) -> Unit,
    isSecondCategory: Boolean,
    firstValDouble: Double = -1.0,
    firstValInt: Int = -1,
    secondVal: Int,
    isActiveWorkout: Boolean = false,
    onStart: () -> Unit
) {
    var firstDouble by remember {
        mutableStateOf(
            when (firstValDouble) {
                -1.0 -> if (firstValInt == 0) "" else "$firstValInt"
                else -> if (firstValDouble == 0.0) "" else "$firstValDouble"
            }
        )
    }
    var firstInt = when (firstValDouble) {
        -1.0 -> if (firstValInt == 0) "" else "$firstValInt"
        else -> if (firstValDouble == 0.0) "" else "$firstValDouble"
    }

    val second = if (secondVal == 0) "" else "$secondVal"
    var checked by remember { mutableStateOf(false) }
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
            text = "$i  ",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        )
        if (firstValDouble != -1.0)
            GoodTextField(
                value = firstDouble,
                onValueChange = {
                    atFirstCategoryChange(i, it)
                    firstDouble = it

                },
                modifier = Modifier.width(60.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        else
            GoodTextField(
                value = firstInt,
                onValueChange = {
                    atFirstCategoryChange(i, it)
                },
                modifier = Modifier.width(60.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        Spacer(modifier = Modifier.width(12.dp))
        if (isSecondCategory) {
            GoodTextField(
                value = second,
                onValueChange = { atSecondCategoryChange(i, it) },
                modifier = Modifier.width(60.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        if (isActiveWorkout) {
            androidx.compose.material3.Checkbox(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    if (it) onStart()
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExerciseHolder() {
    Column {
        GeneralExerciseComposable(
            modifier = Modifier,
            isActiveWorkout = false,
            exerciseName = "exerciseName",
            isDraggingThis = false,
            onDismiss = {},
            setsNum = 1,
            isSecondCategory = true,
            firstCategoryName = "Kg",
            sets = listOf(Set(1,1)),
            secondCategoryName = "Reps",
            atFirstCategoryChange = { _, _ ->},
            atAddSet = {},
            removeSetFromIndex = {},
            reorderable = false,
            breakTime = "60",
            onBreakChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewList() {

}