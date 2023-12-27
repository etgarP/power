package com.example.power.ui.configure.Plan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.data.room.PlanType
import com.example.power.data.room.planTypeToStringMap
import com.example.power.data.view_models.AppViewModelProvider
import com.example.power.data.view_models.plan.PlanViewModel
import com.example.power.ui.PlanScreens
import com.example.power.ui.SearchItem
import com.example.power.ui.configure.Plan.exercise.GeneralHolder
import com.example.power.ui.configure.Plan.exercise.MyAlertDialog
import com.example.power.ui.configure.Plan.workout.BottomSheetEditAndDelete

@Composable
fun Plans(
    modifier: Modifier = Modifier,
    onEdit: (String) -> Unit
) {
    Column {
        PlansPage(onItemClick = onEdit, modifier = modifier)
    }
}

@Composable
fun PlansPage(
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    val planViewModel: PlanViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val searchText by planViewModel.searchText.collectAsState()
    val plans by planViewModel.workouts.collectAsState()
    Column(modifier = modifier.fillMaxHeight()) {
        Spacer(modifier.heightIn(10.dp))
        SearchItem(searchVal = searchText, setVal = planViewModel::onSearchTextChange)
        LazyColumn() {
            items(plans) { plan ->
                val passesSearch = plan.doesMatchSearchQuery(searchText)
                AnimatedVisibility(visible = passesSearch) {
                    PlanHolder(
                        planName = plan.name,
                        numOfWorkouts = plan.workouts.size,
                        onEdit = { onItemClick(plan.name) },
                        onDelete = { exerciseName -> planViewModel.onDelete(exerciseName) },
                        typeOfPlan = plan.planType
                    )
                }
            }
        }
        if (plans.isEmpty())
            Text(
                text = "Click + to add a plan",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
    }

}


@Composable
fun PlanHolder(
    modifier: Modifier = Modifier,
    planName: String,
    numOfWorkouts: Int,
    typeOfPlan: PlanType,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit
) {
    var openAlertDialog by remember { mutableStateOf(false) }
    when {
        openAlertDialog -> {
            MyAlertDialog(
                onDismissRequest = { openAlertDialog = false },
                onConfirmation = {
                    onDelete(planName)
                    openAlertDialog = false
                },
                dialogTitle = "Delete $planName",
                dialogText = "This will delete this Plan permanently.",
                icon = Icons.Filled.Delete
            )
        }
    }
    var showBottomSheet by remember { mutableStateOf(false) }
    BottomSheetEditAndDelete(
        onEdit = onEdit,
        type = "Plan",
        setOpenAlertDialog = {openAlertDialog = true},
        setShowBottomSheet = { showBottomSheet = it },
        showBottomSheet = showBottomSheet
    )
    planTypeToStringMap[typeOfPlan]?.let {
        GeneralHolder(
        modifier = modifier,
        itemName = planName,
        secondaryInfo = "$numOfWorkouts Workouts A Week",
        onItemClick = onEdit,
        moreText = it,
        isMoreText = true
    ) {
        IconButton(onClick = { showBottomSheet = true }) {
            Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "show more options")
        }
    }
    }
}

@Preview(showBackground = true)
@Composable
fun holderPreview() {
    PlanHolder(
        planName = "plan",
        numOfWorkouts = 6,
        typeOfPlan = PlanType.BODYWEIGHT,
        onEdit = { /*TODO*/ },
        onDelete = {}
    )
}
@Composable
fun AddPlanBtn(modifier: Modifier = Modifier, onAdd: (String) -> Unit) {
    FloatingActionButton(modifier = modifier, onClick = { onAdd(PlanScreens.AddItem.route) }) {
        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
    }
}