package com.example.power.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.power.ui.Screens
import com.example.power.ui.navBarItem

/**
 * a navigation bar with a save btn on the right
 */
@Composable
fun RightHandNavButton(
    onClick: () -> Unit,
    valid: Boolean = true,
    isActiveWorkout: Boolean = false
) {
    IconButton(onClick = {
        if (valid)
            onClick()
    }) {
        Icon(
            imageVector = if (isActiveWorkout) Icons.Filled.Done else Icons.Filled.Save,
            contentDescription = "Save/Update",
            tint = if (valid) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error
        )
    }
}

@Preview
@Composable
fun RightHandNavButtonPreview() {
    AppTopBar(title = "title", backFunction = { }) {
        RightHandNavButton(onClick = { })
    }
}

/**
 * the bottom navigation bar that holds the major screens
 */
@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    selectedItem: Int,
    setSelectedItem: (Int) -> Unit,
) {
    // the three main screens with their labels icons and routes
    val navBarItems = listOf(
        navBarItem(
            label = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            Screens.Home.route
        ),
        navBarItem(
            label = "History",
            selectedIcon = Icons.Filled.History,
            unselectedIcon = Icons.Outlined.History,
            Screens.History.route
        ),
        navBarItem(
            label = "Configure",
            selectedIcon = Icons.Filled.Tune,
            unselectedIcon = Icons.Outlined.Tune,
            Screens.Configure.route
        ),
    )
    // applying the buttons
    NavigationBar(modifier) {
        navBarItems.forEachIndexed { index, item ->
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer),
                icon = {
                    if (index == selectedItem)
                        Icon(imageVector = item.selectedIcon, contentDescription = item.label)
                    else
                        Icon(imageVector = item.unselectedIcon, contentDescription = item.label)
                },
                label = { Text(item.label) },
                selected = selectedItem == index,
                onClick = {
                    setSelectedItem(index)
                    onClick(item.route)
                },
            )
        }
    }
}

/**
 * a general top app bar with a possible back button and end button,
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    enableBack: Boolean = false,
    title: String,
    backFunction: () -> Unit,
    endIcon: @Composable () -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Column {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                if (enableBack)
                    IconButton(onClick = { backFunction() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
            },
            actions = {
                endIcon()
            },
            scrollBehavior = scrollBehavior,
        )
        HorizontalDivider()
    }

}

/**
 * a navigation bar with a back button and the current progress percentage
 */
@Composable
fun ProgressNavBar(
    modifier: Modifier = Modifier,
    currentProgress: Float,
    onBack: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { onBack() }) {
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