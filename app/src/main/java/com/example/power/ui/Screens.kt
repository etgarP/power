package com.example.power.ui

import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screens(val route : String) {
    object Home : Screens("home_route")
    object Plans : Screens("plans_route")
    object Workouts : Screens("workouts_route")
    object Exercises : Screens("exercises_route")
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
