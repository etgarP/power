package com.example.power.ui

import androidx.navigation.NavType
import androidx.navigation.navArgument

// the main routes
sealed class Screens(val route : String) {
    object Home : Screens("home_route")
    object Configure : Screens("configure_route")
    object History : Screens("history_route")
}

// defining the routes of the different screens:

sealed class ExerciseScreens(val route : String) {
    // to edit an exercise you need edit_exercise then the exercise name
    object EditItem : ExerciseScreens("edit_exercise") {
        const val argument = "exercise_name"
        val routeWithArgs = "${route}/{${argument}}"
        // exercise name needs to be a string
        val arguments = listOf(
            navArgument("exercise_name") { type = NavType.StringType }
        )
    }
    // to add an exercise you need add_exercise
    object AddItem : ExerciseScreens("add_exercise")
}

sealed class WorkoutScreens(val route : String) {
    // for editing a workout
    object EditItem : WorkoutScreens("edit_workout") {
        const val argument = "workout_name"
        val routeWithArgs = "${route}/{${argument}}"
        val arguments = listOf(
            navArgument("workout_name") { type = NavType.StringType }
        )
    }
    // for starting a workout
    object StartItem : WorkoutScreens("start_workout") {
        const val argument = "workout_name"
        val routeWithArgs = "${route}/{${argument}}"
        val arguments = listOf(
            navArgument("workout_name") { type = NavType.StringType }
        )
    }
    // for starting a workout from a plan
    object StartPlanItem : WorkoutScreens("start_plan_workout") {
        const val argument1 = "workout_name"
        const val argument2 = "week_index"
        // takes two arguments a string and an int, one is the workout name and
        // one is the index of the week
        val routeWithArgs = "${route}/{${argument1}}/{${argument2}}"
        val arguments = listOf(
            navArgument("workout_name") { type = NavType.StringType },
            navArgument("week_index") { type = NavType.IntType }
        )
    }
    // adding a workout
    object AddItem : WorkoutScreens("add_workout")
    // choosing an exercise to add to a workout
    object ChooseExercise : WorkoutScreens("choose_exercise")
}

sealed class PlanScreens(val route : String) {
    // editing a plan
    object EditItem : WorkoutScreens("edit_plan") {
        const val argument = "plan_name"
        val routeWithArgs = "${route}/{${argument}}"
        // defining an argument with type string, can define multiple
        val arguments = listOf(
            navArgument("plan_name") { type = NavType.StringType }
        )
    }
    // adding a plan
    object AddItem : WorkoutScreens("add_plan")
    // choosing a workout to add to a plan
    object ChooseWorkout : WorkoutScreens("choose_workout")
}
