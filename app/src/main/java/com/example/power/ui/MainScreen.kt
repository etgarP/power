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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.power.data.room.Exercise
import com.example.power.data.room.Workout
import com.example.power.data.viewmodels.AppViewModelProvider
import com.example.power.data.viewmodels.LoadInitialDataViewModel
import com.example.power.ui.History.History
import com.example.power.ui.components.BottomNavBar
import com.example.power.ui.configure.Configure
import com.example.power.ui.configure.Plan.ChooseWorkoutForPlan
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

/**
 * the main screen of the app.
 * holds the scaffold with the bottom bar and the navigation host
 */
@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var selectedItem by remember { mutableStateOf(0) }
    var inWorkout by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            // animation between main screens
            AnimatedVisibility(
                visible = !inWorkout,
                enter = slideIn(tween(100, easing = FastOutSlowInEasing)) {
                    IntOffset(0, +180)
                },
                exit = slideOut(tween(100, easing = FastOutSlowInEasing)) {
                    IntOffset(0, +180)
                }
            ) {
                // botton navigation bar between home history and configure
                BottomNavBar(
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
        // the navigation host
        AppNavHost(
            paddingValues = paddingValues,
            navController = navController,
            setSelectedItem = {selectedItem = it},
        ) {
            inWorkout = it
        }
    }
}

/**
 * nav host that controls the screen and moving between the, along with the animations
 * and logic needed to move between one another
 */
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
            Home (startWorkoutNoPlan = { workoutName -> // can start a workout (not from a plan)
                    navController.navigate("${WorkoutScreens.StartItem.route}/$workoutName")
                    setInWorkout(true)
            }) { workoutName: String, index: String -> // can start a workout from a plan
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
        composable( // for adding a new plan
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
        composable( // for editing a plan
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
        composable( // for adding a workout to a plan
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
            ChooseWorkoutForPlan(
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
        composable( // for adding a new workout
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
        composable( // for editing a workout
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
        composable( // for an active workout
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
        composable( // for an active workout that started as a plan
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
        composable( // for adding an exercise for a workout
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
        composable( // for adding a new exercise
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

// scan in and out animation to make using them shorter and easier
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

data class navBarItem (
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MyApp(modifier: Modifier = Modifier) {
    // asking permission to send notifications
    val postNotificationPermission=
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(key1 = true ){
        if(!postNotificationPermission.status.isGranted){
            postNotificationPermission.launchPermissionRequest()
        }
    }
    // loading the initial data to the app (only at the first lunch)
    val loadInitialDataViewModel: LoadInitialDataViewModel = viewModel(factory = AppViewModelProvider.Factory)
    PowerTheme {
        Surface(modifier) {
            MainScreen()
        }
    }
}