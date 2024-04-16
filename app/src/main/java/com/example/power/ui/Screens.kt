package com.example.power.ui

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screens(val route : String) {
    object Home : Screens("home_route")
    object Configure : Screens("configure_route")
    object History : Screens("history_route")
}
sealed class ExerciseScreens(val route : String) {
    object EditItem : ExerciseScreens("edit_exercise") {
        const val argument = "exercise_name"
        val routeWithArgs = "${route}/{${argument}}"
        // defining an argument with type string, can define multiple
        val arguments = listOf(
            navArgument("exercise_name") { type = NavType.StringType }
        )
    }
    object AddItem : ExerciseScreens("add_exercise")
}

sealed class WorkoutScreens(val route : String) {
    object EditItem : WorkoutScreens("edit_workout") {
        const val argument = "workout_name"
        val routeWithArgs = "${route}/{${argument}}"
        // defining an argument with type string, can define multiple
        val arguments = listOf(
            navArgument("workout_name") { type = NavType.StringType }
        )
    }
    object StartItem : WorkoutScreens("start_workout") {
        const val argument = "workout_name"
        val routeWithArgs = "${route}/{${argument}}"
        // defining an argument with type string, can define multiple
        val arguments = listOf(
            navArgument("workout_name") { type = NavType.StringType }
        )
    }
    object StartPlanItem : WorkoutScreens("start_plan_workout") {
        const val argument1 = "workout_name"
        const val argument2 = "week_index"
        val routeWithArgs = "${route}/{${argument1}}/{${argument2}}"
        // defining an argument with type string, can define multiple
        val arguments = listOf(
            navArgument("workout_name") { type = NavType.StringType },
            navArgument("week_index") { type = NavType.IntType }
        )
    }
    object AddItem : WorkoutScreens("add_workout")
    object ChooseExercise : WorkoutScreens("choose_exercise")
}

sealed class PlanScreens(val route : String) {
    object EditItem : WorkoutScreens("edit_plan") {
        const val argument = "plan_name"
        val routeWithArgs = "${route}/{${argument}}"
        // defining an argument with type string, can define multiple
        val arguments = listOf(
            navArgument("plan_name") { type = NavType.StringType }
        )
    }
    object AddItem : WorkoutScreens("add_plan")
    object ChooseWorkout : WorkoutScreens("choose_workout")
}
