package com.example.power.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * takes a list of options and creates a drop menu
 * value is the value of the box that will be displayed
 * on value change changed this value to the selected value
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropMenuOutlined(
    modifier: Modifier = Modifier,
    options: List<String>,
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .padding(top = 15.dp),
            readOnly = true,
            value = value,
            onValueChange = {onValueChange(it)},
            label = { Text(text = label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onValueChange(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

/**
 * takes a list of options and creates a drop menu
 * value is the value of the box that will be displayed
 * on value change changed this value to the selected value
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropMenu(
    modifier: Modifier = Modifier,
    options: List<String>,
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .padding(top = 11.dp),
            readOnly = true,
            value = value,
            onValueChange = {onValueChange(it)},
            label = { Text(text = label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(
            modifier = Modifier,
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    modifier = Modifier,
                    text = { Text(selectionOption) },
                    onClick = {
                        onValueChange(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
