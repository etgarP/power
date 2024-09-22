package com.example.power.ui.configure.Plan

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.power.data.room.PlanType
import com.example.power.data.room.planTypeToStringMap
import com.example.power.data.viewmodels.AppViewModelProvider
import com.example.power.data.viewmodels.plan.PlanViewModel
import com.example.power.ui.components.BottomSheetEditAndDelete
import com.example.power.ui.components.CollapsedInfo
import com.example.power.ui.components.MyAlertDialog
import com.example.power.ui.components.SearchItem

/**
 * the page to show all the plans
 */
@Composable
fun Plans(
    modifier: Modifier = Modifier,
    onEdit: (String) -> Unit
) {
    Column {
        PlansPage(onItemClick = onEdit, modifier = modifier)
    }
}

/**
 * display plans, able to search them, edit and delete them
 */
@Composable
fun PlansPage(
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    val planViewModel: PlanViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val searchText by planViewModel.searchText.collectAsState()
    val plans by planViewModel.plans.collectAsState()
    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier.heightIn(10.dp))
        // search bar
        SearchItem(searchVal = searchText, setVal = planViewModel::onSearchTextChange)
        // viewing the plans
        LazyColumn() {
            items(plans) { plan ->
                val passesSearch = plan.doesMatchSearchQuery(searchText)
                AnimatedVisibility(visible = passesSearch) {
                    CollapsedPlanInfo(
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

/**
 * shows a collapsed plan with ability to delete and edit it
 */
@Composable
fun CollapsedPlanInfo(
    modifier: Modifier = Modifier,
    planName: String,
    numOfWorkouts: Int,
    typeOfPlan: PlanType,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit
) {
    // alert dialog for deleting
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
    // bottom sheet for options
    var showBottomSheet by remember { mutableStateOf(false) }
    BottomSheetEditAndDelete(
        onEdit = onEdit,
        type = "Plan",
        setOpenAlertDialog = {openAlertDialog = true},
        setShowBottomSheet = { showBottomSheet = it },
        showBottomSheet = showBottomSheet
    )
    // button to see more options
    planTypeToStringMap[typeOfPlan]?.let {
        CollapsedInfo(
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