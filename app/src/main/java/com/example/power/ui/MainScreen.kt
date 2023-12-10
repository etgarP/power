package com.example.power.ui

import android.content.res.Configuration
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RunCircle
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.power.R
import com.example.power.data.room.Exercise
import com.example.power.data.room.Workout
import com.example.power.ui.Plan.ChoosePlan
import com.example.power.ui.Plan.Plans
import com.example.power.ui.exercise.AddBtn
import com.example.power.ui.exercise.AddExercise
import com.example.power.ui.exercise.EditExercise
import com.example.power.ui.exercise.ExercisePage
import com.example.power.ui.home.Home
import com.example.power.ui.theme.PowerTheme
import com.example.power.ui.workout.AddPlan
import com.example.power.ui.workout.AddWorkout
import com.example.power.ui.workout.ChooseExercise
import com.example.power.ui.workout.EditPlan
import com.example.power.ui.workout.EditWorkout
import com.example.power.ui.workout.Workouts

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var selectedItem by remember { mutableStateOf(0) }
    var exerciseHome by remember { mutableStateOf(false) }
    var exerciseAdd by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
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
        },
        floatingActionButton = {
            val place by navController.currentBackStackEntryAsState()
            if (place?.destination?.route == Screens.Exercises.route)
                AddBtn(onAdd = { navController.navigate(ExerciseScreens.AddItem.route)})
            if (place?.destination?.route == Screens.Workouts.route)
                AddBtn(onAdd = { navController.navigate(WorkoutScreens.AddItem.route)})
            if (place?.destination?.route == Screens.Plans.route)
                AddBtn(onAdd = { navController.navigate(PlanScreens.AddItem.route)})
        },
    ) { paddingValues ->
        appNavHost(paddingValues = paddingValues, navController = navController,
            setSelectedItem = {selectedItem = it}) }
}

@Composable
fun appNavHost(paddingValues: PaddingValues, navController:
                NavHostController, setSelectedItem: (Int) -> Unit) {
    NavHost(
        navController = navController,
        startDestination = Screens.Home.route,
        modifier = Modifier.padding(paddingValues = paddingValues)) {
        /**
         * home screen
         */
        composable(Screens.Home.route) {
            setSelectedItem(0)
            Home()
        }
        /**
         * plan screens
         */
        composable(
            Screens.Plans.route,
            enterTransition = {
                when (initialState.destination.route) {
                    PlanScreens.AddItem.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    PlanScreens.EditItem.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    PlanScreens.AddItem.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    PlanScreens.EditItem.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    else -> null
                }
            }
        ) {
            setSelectedItem(1)
            Plans(onItemClick = { planName ->
                navController.navigate("${PlanScreens.EditItem.route}/$planName") })
        }
        composable(
            PlanScreens.AddItem.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Plans.route -> scaleIntoContainer(direction = Direction.INWARDS)
                    PlanScreens.ChooseWorkout.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Plans.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
                    PlanScreens.ChooseWorkout.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    else -> null
                }
            }
        ) { navBackResult ->
            setSelectedItem(1)
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
                    Screens.Plans.route -> scaleIntoContainer(direction = Direction.INWARDS)
                    PlanScreens.ChooseWorkout.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Plans.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
                    PlanScreens.ChooseWorkout.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    else -> null
                }
            }
        ) { navBackResult ->
            setSelectedItem(1)
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
            setSelectedItem(1)
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
            Screens.Workouts.route,
            enterTransition = {
                when (initialState.destination.route) {
                    WorkoutScreens.AddItem.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    WorkoutScreens.EditItem.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    WorkoutScreens.AddItem.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    WorkoutScreens.EditItem.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    else -> null
                }
            }
        ) {
            setSelectedItem(2)
            Workouts(onItemClick = { workoutName ->
                navController.navigate("${WorkoutScreens.EditItem.route}/$workoutName") })
        }
        composable(
            WorkoutScreens.AddItem.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Workouts.route -> scaleIntoContainer(direction = Direction.INWARDS)
                    WorkoutScreens.ChooseExercise.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Workouts.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
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
                    Screens.Workouts.route -> scaleIntoContainer(direction = Direction.INWARDS)
                    WorkoutScreens.ChooseExercise.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Workouts.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
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
            Screens.Exercises.route,
            enterTransition = {
                when (initialState.destination.route) {
                    ExerciseScreens.AddItem.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    ExerciseScreens.EditItem.route -> scaleIntoContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    ExerciseScreens.AddItem.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    ExerciseScreens.EditItem.route -> scaleOutOfContainer(direction = Direction.INWARDS)
                    else -> null
                }
            }
        ) {
            setSelectedItem(3)
            ExercisePage(
                onItemClick = { exerciseName ->
                navController.navigate("${ExerciseScreens.EditItem.route}/$exerciseName") }
            )
        }
        composable(
            ExerciseScreens.AddItem.route,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Exercises.route -> scaleIntoContainer(direction = Direction.INWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Exercises.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            }
        ) {
            setSelectedItem(3)
            AddExercise(onBack = { navController.popBackStack() })
        }
        composable(
            route = ExerciseScreens.EditItem.routeWithArgs,
            arguments = ExerciseScreens.EditItem.arguments,
            enterTransition = {
                when (initialState.destination.route) {
                    Screens.Exercises.route -> scaleIntoContainer(direction = Direction.INWARDS)
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    Screens.Exercises.route -> scaleOutOfContainer(direction = Direction.OUTWARDS)
                    else -> null
                }
            }
        ) { navBackStackEntry ->
            setSelectedItem(3)
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
    enableMenu: Boolean = false,
    title: String,
    backFunction: () -> Unit,
    enableToolTip: Boolean = false,
    toolTipMessage: String = "",
    bringUpSnack: (String) -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
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
            if (enableMenu)
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Localized description"
                    )
                }
            if(enableToolTip) {
                MyToolTip(
                    onClick = { bringUpSnack(toolTipMessage) }
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

@Composable
fun MyToolTip(
    onClick: () -> Unit,
) {
    IconButton(onClick = { onClick() }) {
        Icon(imageVector = Icons.Filled.TipsAndUpdates, contentDescription = "tip")
    }
}

@Composable
fun NavBar(
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
    selectedItem: Int,
    setSelectedItem: (Int) -> Unit,
) {
    val items = listOf("Home", "Plans", "Workouts", "Exercises")
    val myIcons = listOf(
        Icons.Filled.Home,
        Icons.Filled.CalendarToday,
        Icons.Filled.RunCircle,
    )
    val myRoutes =  listOf(Screens.Home.route, Screens.Plans.route, Screens.Workouts.route, Screens.Exercises.route)
    NavigationBar(modifier) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    if (index < 3)
                        Icon(myIcons[index], contentDescription = item)
                    else
                        Icon(painterResource(R.drawable.ic_fitness), contentDescription = item)
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = {
                    setSelectedItem(index)
                    onClick(myRoutes[index])
                },
            )
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MyApp(modifier: Modifier = Modifier) {
    PowerTheme {
        Surface(modifier) {
            MainScreen()
        }
    }
}