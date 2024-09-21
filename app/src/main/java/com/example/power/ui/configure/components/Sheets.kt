package com.example.power.ui.configure.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * function to dismiss an open sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
fun dissmissSheet(
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

/**
 * one item on a bottom sheet
 * has an image a text an on click function
 */
@Composable
fun BottomSheetItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    text: String,
    onItemClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = text,
            modifier = Modifier.padding(vertical = 20.dp).padding(start = 25.dp)
        )
        Text(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 15.dp),
            text = text
        )
    }
}


@Preview(showBackground = true)
@Composable
fun BottomSheetItemPreview() {
    BottomSheetItem(
        imageVector = Icons.Filled.Edit,
        text = "Edit name",
        onItemClick = {}
    )
}

/**
 * bottom sheet with options to delete and edit,
 * on click to delete activates a bottom sheet function
 */
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
                BottomSheetItem(
                    imageVector = Icons.Filled.Edit,
                    text = "Edit $type"
                ) {
                    dissmissSheet(scope, sheetState) { setShowBottomSheet(it) }
                    onEdit()
                }
                BottomSheetItem(
                    imageVector = Icons.Filled.Delete,
                    text = "Delete $type",
                ) {
                    dissmissSheet(scope, sheetState) { setShowBottomSheet(it) }
                    setOpenAlertDialog()
                }
            }
        }
    }
}