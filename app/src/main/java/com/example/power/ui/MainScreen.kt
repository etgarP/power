package com.example.power.ui

import android.Manifest
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.power.data.room.Exercise
import com.example.power.data.room.Workout
import com.example.power.data.viewmodels.AppViewModelProvider
import com.example.power.data.viewmodels.LoadInitialDataViewModel
import com.example.power.ui.History.History
import com.example.power.ui.configure.Configure
import com.example.power.ui.configure.Plan.ChoosePlan
import com.example.power.ui.configure.Plan.exercise.AddExercise
import com.example.power.ui.configure.Plan.exercise.EditExercise
import com.example.power.ui.configure.Plan.workout.AddWorkout
import com.example.power.ui.configure.Plan.workout.ChooseExercise
import com.example.power.ui.configure.Plan.workout.EditWorkout
import com.example.power.ui.configure.workout.OnGoingWorkout
import com.example.power.ui.configure.workout.OnGoingWorkoutFromPlan
import com.example.power.ui.home.Home
import com.example.power.ui.theme.PowerTheme
import com.example.power.ui.workout.AddPlan
import com.example.power.ui.workout.EditPlan
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var selectedItem by remember { mutableStateOf(0) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var inWorkout by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = !inWorkout,
                enter = slideIn(tween(100, easing = FastOutSlowInEasing)) {
                    IntOffset(0, +180)
                },
                exit = slideOut(tween(100, easing = FastOutSlowInEasing)) {
                    IntOffset(0, +180)
                }
            ) {
                NavBar(
                    onClick = { inputRoute ->
                        navController.navigate(inputRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    selectedItem = selectedItem,
                    setSelectedItem = {num -> selectedItem = num}
                )
            }
        },
    ) { paddingValues ->
        AppNavHost(
            paddingValues = paddingValues,
            navController = navController,
            setSelectedItem = {selectedItem = it},
        ) {
            inWorkout = it
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppNavHost(
    paddingValues: PaddingValues,
    navController: NavHostController,
    setSelectedItem: (Int) -> Unit,
    setInWorkout: (Boolean) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Home.route,
        modifier = Modifier.padding(paddingValues = paddingValues)) {
        /**
         * home screen
         */
        composable(Screens.Home.route) {
            setSelectedItem(0)
            Home (startWorkoutNoPlan = { workoutName ->
                    navController.navigate("${WorkoutScreens.StartItem.route}/$workoutName")
                    setInWorkout(true)
            }) { workoutName: String, index: String ->
                navController.navigate("${WorkoutScreens.StartPlanItem.route}/$workoutName/$index")
                setInWorkout(true)
            }
        }
        /**
         * configure screen
         */
        composable(
            route = Screens.Configure.route,
            enterTransition = {
                when (initialState.destination.route) {
                    WorkoutScreens.AddItem.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    WorkoutScreens.EditItem.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    ExerciseScreens.AddItem.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    ExerciseScreens.EditItem.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    PlanScreens.AddItem.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    PlanScreens.EditItem.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    WorkoutScreens.AddItem.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    WorkoutScreens.EditItem.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    ExerciseScreens.AddItem.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    ExerciseScreens.EditItem.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    PlanScreens.AddItem.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    PlanScreens.EditItem.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    else -> null
                }
            }
        ) {
            setSelectedItem(2)
            var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
            Configure(
                editPlan = { planName ->
                    navController.navigate("${PlanScreens.EditItem.route}/$planName")
                },
                editExercise = { exerciseName ->
                    navController.navigate("${ExerciseScreens.EditItem.route}/$exerciseName")
                },
                editWorkout = { workoutName ->
                    navController.navigate("${WorkoutScreens.EditItem.route}/$workoutName")
                },
                startWorkout = { workoutName ->
                    navController.navigate("${WorkoutScreens.StartItem.route}/$workoutName")
                    setInWorkout(true)
                },
                navigate = navController::navigate,
                selectedTabIndex = selectedTabIndex,
                setSelectedTabIndex = { selectedTabIndex = it }
            )
        }

        /**
         * History Screen
         */
        composable(
            Screens.History.route,
        ) { navBackResult ->
            setSelectedItem(1)
            History() { workoutName ->
                navController.navigate("${WorkoutScreens.StartItem.route}/$workoutName")
                setInWorkout(true)
            }
        }

        /**
         * plan screens
         */

        composable(
            PlanScreens.AddItem.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Configure.route -> scaleIntoContainer(direction = Direction.INWARDS)
                    PlanScreens.ChooseWorkout.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Configure.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
                    PlanScreens.ChooseWorkout.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    else -> null
                }
            }
        ) { navBackResult ->
            setSelectedItem(2)
            AddPlan(
                onBack = { navController.popBackStack() },
                getMore = { navController.navigate(PlanScreens.ChooseWorkout.route) },
                getWorkout = {
                    val workout = navBackResult.savedStateHandle.get<Workout>("workout")
                    navBackResult.savedStateHandle["workout"] = null
                    workout
                }
            )
        }
        composable(
            route = PlanScreens.EditItem.routeWithArgs,
            arguments = PlanScreens.EditItem.arguments,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Configure.route -> scaleIntoContainer(direction = Direction.INWARDS)
                    PlanScreens.ChooseWorkout.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Configure.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
                    PlanScreens.ChooseWorkout.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    else -> null
                }
            }
        ) { navBackResult ->
            setSelectedItem(2)
            val planName =
                navBackResult.arguments?.getString(PlanScreens.EditItem.argument)
            EditPlan(
                planName = planName,
                onBack = { navController.popBackStack() },
                getMore = { navController.navigate(PlanScreens.ChooseWorkout.route) },
                getWorkout = {
                    val workout = navBackResult.savedStateHandle.get<Workout>("workout")
                    navBackResult.savedStateHandle["workout"] = null
                    workout
                }
            )
        }
        composable(
            PlanScreens.ChooseWorkout.route,
            enterTransition = {
                when (initialState.destination.route) {
                    PlanScreens.AddItem.route -> scaleIntoContainer(direction = Direction.INWARDS)
                    PlanScreens.EditItem.routeWithArgs -> scaleIntoContainer(direction = Direction.INWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    PlanScreens.AddItem.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
                    PlanScreens.EditItem.routeWithArgs -> scaleOutOfContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            }
        ) {
            setSelectedItem(2)
            ChoosePlan(
                onClick = { workout ->
                    navController.popBackStack()
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("workout", workout)
                },
                onBack = { navController.popBackStack() }
            )
        }

        /**
         * workout screens
         */
        composable(
            WorkoutScreens.AddItem.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Configure.route -> scaleIntoContainer(direction = Direction.INWARDS)
                    WorkoutScreens.ChooseExercise.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Configure.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
                    WorkoutScreens.ChooseExercise.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    else -> null
                }
            }
        ) { navBackResult ->
            setSelectedItem(2)
            AddWorkout(
                onBack = { navController.popBackStack() },
                getMore = { navController.navigate(WorkoutScreens.ChooseExercise.route) },
                getExercise = {
                    val exercise = navBackResult.savedStateHandle.get<Exercise>("exercise")
                    navBackResult.savedStateHandle["exercise"] = null
                    exercise
                }
            )
        }
        composable(
            route = WorkoutScreens.EditItem.routeWithArgs,
            arguments = WorkoutScreens.EditItem.arguments,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Configure.route -> scaleIntoContainer(direction = Direction.INWARDS)
                    WorkoutScreens.ChooseExercise.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Configure.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
                    WorkoutScreens.ChooseExercise.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    else -> null
                }
            }
        ) { navBackResult ->
            setSelectedItem(2)
            val workoutName =
                navBackResult.arguments?.getString(WorkoutScreens.EditItem.argument)
            EditWorkout(
                workoutName = workoutName,
                onBack = { navController.popBackStack() },
                getMore = { navController.navigate(WorkoutScreens.ChooseExercise.route) },
                getExercise = {
                    val exercise = navBackResult.savedStateHandle.get<Exercise>("exercise")
                    navBackResult.savedStateHandle["exercise"] = null
                    exercise
                }
            )
        }
        composable(
            route = WorkoutScreens.StartItem.routeWithArgs,
            arguments = WorkoutScreens.StartItem.arguments,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Configure.route -> scaleIntoContainer(direction = Direction.INWARDS)
//                    WorkoutScreens.ChooseExercise.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Configure.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
//                    WorkoutScreens.ChooseExercise.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    else -> null
                }
            }
        ) { navBackResult ->
            val workoutName =
                navBackResult.arguments?.getString(WorkoutScreens.StartItem.argument)
            OnGoingWorkout(
                workoutName = workoutName,
                onBack = {
                    navController.popBackStack()
                    setInWorkout(false)
                 },
                getMore = { navController.navigate(WorkoutScreens.ChooseExercise.route) },
                getExercise = {
                    val exercise = navBackResult.savedStateHandle.get<Exercise>("exercise")
                    navBackResult.savedStateHandle["exercise"] = null
                    exercise
                }
            )
        }
        composable(
            route = WorkoutScreens.StartPlanItem.routeWithArgs,
            arguments = WorkoutScreens.StartPlanItem.arguments,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Configure.route -> scaleIntoContainer(direction = Direction.INWARDS)
                    WorkoutScreens.ChooseExercise.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Configure.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
                    WorkoutScreens.ChooseExercise.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    else -> null
                }
            }
        ) { navBackResult ->
            val workoutName =
                navBackResult.arguments?.getString(WorkoutScreens.StartPlanItem.argument1)
            val index = navBackResult.arguments?.getInt(WorkoutScreens.StartPlanItem.argument2)
            OnGoingWorkoutFromPlan(
                workoutName = workoutName,
                index = index,
                onBack = {
                    navController.popBackStack()
                    setInWorkout(false)
                },
                getMore = { navController.navigate(WorkoutScreens.ChooseExercise.route) },
                getExercise = {
                    val exercise = navBackResult.savedStateHandle.get<Exercise>("exercise")
                    navBackResult.savedStateHandle["exercise"] = null
                    exercise
                }
            )
        }
        composable(
            WorkoutScreens.ChooseExercise.route,
            enterTransition = {
                when (initialState.destination.route) {
                    WorkoutScreens.AddItem.route -> scaleIntoContainer(direction = Direction.INWARDS)
                    WorkoutScreens.EditItem.routeWithArgs -> scaleIntoContainer(direction = Direction.INWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    WorkoutScreens.AddItem.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
                    WorkoutScreens.EditItem.routeWithArgs -> scaleOutOfContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            }
        ) {
            setSelectedItem(2)
            ChooseExercise(
                onClick = { exercise ->
                navController.popBackStack()
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("exercise", exercise)
                },
                onBack = { navController.popBackStack() }
            )
        }

        /**
         * Exercise screens
         */
        composable(
            ExerciseScreens.AddItem.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Configure.route -> scaleIntoContainer(direction = Direction.INWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Configure.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            }
        ) {
            setSelectedItem(2)
            AddExercise(onBack = { navController.popBackStack() })
        }
        composable(
            route = ExerciseScreens.EditItem.routeWithArgs,
            arguments = ExerciseScreens.EditItem.arguments,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Configure.route -> scaleIntoContainer(direction = Direction.INWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Configure.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            }
        ) { navBackStackEntry ->
            setSelectedItem(2)
            val exerciseName =
                navBackStackEntry.arguments?.getString(ExerciseScreens.EditItem.argument)
            EditExercise(exerciseName = exerciseName, onBack = { navController.popBackStack() })
        }
    }
}

enum class Direction {
    INWARDS,
    OUTWARDS
}

fun scaleIntoContainer(
    direction: Direction = Direction.INWARDS,
    initialScale: Float = if (direction == Direction.OUTWARDS) 0.9f else 1.1f
): EnterTransition {
    return scaleIn(
        animationSpec = tween(220, delayMillis = 90),
        initialScale = initialScale
    ) + fadeIn(animationSpec = tween(220, delayMillis = 90))
}

fun scaleOutOfContainer(
    direction: Direction = Direction.OUTWARDS,
    targetScale: Float = if (direction == Direction.INWARDS) 0.9f else 1.1f
): ExitTransition {
    return scaleOut(
        animationSpec = tween(
            durationMillis = 220,
            delayMillis = 90
        ), targetScale = targetScale
    ) + fadeOut(tween(delayMillis = 90))
}

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
        Divider()
    }

}

@Composable
fun MyToolTip(
    onClick: () -> Unit,
) {
    IconButton(onClick = { onClick() }) {
        Icon(imageVector = Icons.Filled.TipsAndUpdates, contentDescription = "tip")
    }
}

data class navBarItem (
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)

@Composable
fun NavBar(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    selectedItem: Int,
    setSelectedItem: (Int) -> Unit,
) {
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

@Composable
fun loadAnimation(
    modifier: Modifier = Modifier,
    circleSize: Dp = 25.dp,
    circleColor: Color = MaterialTheme.colorScheme.primary,
    spaceBetween: Dp = 10.dp,
    travelDistance: Dp = 20.dp
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Initial loading...")
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MyApp(modifier: Modifier = Modifier) {
    val postNotificationPermission=
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(key1 = true ){
        if(!postNotificationPermission.status.isGranted){
            postNotificationPermission.launchPermissionRequest()
        }
    }
    val loadInitialDataViewModel: LoadInitialDataViewModel = viewModel(factory = AppViewModelProvider.Factory)
    PowerTheme {
        Surface(modifier) {
            MainScreen()
        }
    }
}