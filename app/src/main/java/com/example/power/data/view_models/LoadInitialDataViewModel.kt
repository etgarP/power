package com.example.power.data.view_models

import PlanBuilder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.power.data.repository.InfoRepository
import com.example.power.data.repository.PlanRepository
import com.example.power.data.repository.WorkoutRepository
import com.example.power.data.repository.exercise.ExercisesRepository
import com.example.power.data.room.BodyType
import com.example.power.data.room.ExerciseType
import com.example.power.data.room.Plan
import com.example.power.data.room.PlanType
import kotlinx.coroutines.launch

class LoadInitialDataViewModel (
    private val exercisesRepository: ExercisesRepository,
    private val workoutRepository: WorkoutRepository,
    private val planRepository: PlanRepository,
    private val infoRepository: InfoRepository
): ViewModel() {
    var ready by mutableStateOf(false)
        private set
    init {
        viewModelScope.launch {
            infoRepository.getInfo().collect {
                if (it == null) {
                    viewModelScope.launch {
                        loadRepository()
                    }
                } else {
                    ready = true
                }
            }
        }
    }
    private fun loadRepository() {
        loadDumbbell3days()
        loadWomensDumbbellWorkout3Days()
    }

    private fun loadDumbbell3days() {
        // Step 1: Use PlanBuilder to define and add exercises
        val planBuilder = PlanBuilder()
        planBuilder
            .addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Squat (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Stiff Legged Deadlift (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.BACK, "Bent Over Row (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.CHEST, "Bench Press (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.SHOULDERS, "Lateral Raises (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.ARMS, "Standing Curl (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.ARMS, "Lying Extension (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Lunge (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Hamstring Curl (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Deadlift (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.SHOULDERS, "Military Press (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.CHEST, "Flys (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.ARMS, "Hammer Curl (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.ARMS, "Seated Extension (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Step Up (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Stiff Legged Deadlift (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.BACK, "One Arm Row (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.CHEST, "Reverse Grip Press (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.SHOULDERS, "Rear Delt Fly (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.ARMS, "Zottman Curl (Dumbbell)")
            .addExercise(ExerciseType.WEIGHT, BodyType.CHEST, "Close Grip Press (Dumbbell)")

        // Step 2: Create workout routines for each day using PlanBuilder
        val day1 = planBuilder.buildWorkout(
            "Day 1", listOf(
                "Weight" to 0,
                "Weight" to 1,
                "Weight" to 2,
                "Weight" to 3,
                "Weight" to 4,
                "Weight" to 5,
                "Weight" to 6
            )
        )

        val day2 = planBuilder.buildWorkout(
            "Day 2", listOf(
                "Weight" to 7,
                "Weight" to 8,
                "Weight" to 9,
                "Weight" to 10,
                "Weight" to 11,
                "Weight" to 12,
                "Weight" to 13
            )
        )

        val day3 = planBuilder.buildWorkout(
            "Day 3", listOf(
                "Weight" to 14,
                "Weight" to 15,
                "Weight" to 16,
                "Weight" to 17,
                "Weight" to 18,
                "Weight" to 19,
                "Weight" to 20
            )
        )

        // Step 3: Organize the workouts into a plan spanning 8 weeks
        val plan = Plan(
            name = "8-Week Dumbbell Program",
            weeks = 8,
            workouts = listOf(day1, day2, day3),
            planType = PlanType.DUMBBELLS
        )
        plan.startWeeks()

        // Step 4: Store exercises, workouts, and plan in repositories
        viewModelScope.launch {
            planBuilder.getExercises().forEach { exercisesRepository.insertExercise(it) }
            workoutRepository.insertWorkout(day1)
            workoutRepository.insertWorkout(day2)
            workoutRepository.insertWorkout(day3)
            planRepository.insertPlan(plan)
            ready = true
        }
    }

    private fun loadWomensDumbbellWorkout3Days() {
        // Initialize the PlanBuilder
        val planBuilder = PlanBuilder()

        // Step 1: Define the exercises
        // Add exercises to the PlanBuilder
        planBuilder.apply {
            addExercise(type = ExerciseType.REPS, body = BodyType.CORE, name = "Ab Crunch")
            addExercise(type = ExerciseType.REPS, body = BodyType.CORE, name = "Lying Leg Raise")
            addExercise(type = ExerciseType.REPS, body = BodyType.CORE, name = "Side Oblique Crunch (each side)")
            addExercise(type = ExerciseType.REPS, body = BodyType.LEGS, name = "Glute Kick Back")
            addExercise(type = ExerciseType.WEIGHT, body = BodyType.LEGS, name = "Dumbbell Romanian Deadlift")
            addExercise(type = ExerciseType.WEIGHT, body = BodyType.LEGS, name = "Reverse Lunge")
            addExercise(type = ExerciseType.WEIGHT, body = BodyType.LEGS, name = "Dumbbell Squat")
            addExercise(type = ExerciseType.WEIGHT, body = BodyType.LEGS, name = "Dumbbell Lunge (each side)")
            addExercise(type = ExerciseType.WEIGHT, body = BodyType.LEGS, name = "Dumbbell Lying Leg Curl (on the floor)")
            addExercise(type = ExerciseType.REPS, body = BodyType.LEGS, name = "Bodyweight Single Leg Deadlift")
            addExercise(type = ExerciseType.WEIGHT, body = BodyType.LEGS, name = "Seated Calf Raise")
            addExercise(type = ExerciseType.REPS, body = BodyType.LEGS, name = "Standing Calf Raise")
            addExercise(type = ExerciseType.WEIGHT, body = BodyType.CHEST, name = "Dumbbell Bench Press (on the floor)")
            addExercise(type = ExerciseType.WEIGHT, body = BodyType.BACK, name = "Bent-Over Dumbbell Row")
            addExercise(type = ExerciseType.WEIGHT, body = BodyType.CHEST, name = "Dumbbell Pullover")
            addExercise(type = ExerciseType.WEIGHT, body = BodyType.SHOULDERS, name = "Lateral Raise")
            addExercise(type = ExerciseType.WEIGHT, body = BodyType.ARMS, name = "Lying Dumbbell Extension")
            addExercise(type = ExerciseType.WEIGHT, body = BodyType.ARMS, name = "Hammer Dumbbell Curl")
        }

        // Step 2: Create workout routines for each day using the builder
        val day1 = planBuilder.buildWorkout(
            dayName = "Day 1 - Abs And Glutes",
            exerciseConfigs = listOf(
                "Reps" to 0, "Reps" to 1, "Reps" to 2, "Reps" to 3,
                "Weight" to 4, "Weight" to 5
            )
        )

        val day2 = planBuilder.buildWorkout(
            dayName = "Day 2 - Lower Body",
            exerciseConfigs = listOf(
                "Weight" to 6, "Weight" to 7, "Weight" to 8, "Weight" to 9,
                "Weight" to 10, "Reps" to 11
            )
        )

        val day3 = planBuilder.buildWorkout(
            dayName = "Day 3 - Upper Body",
            exerciseConfigs = listOf(
                "Weight" to 12, "Weight" to 13, "Weight" to 14,
                "Weight" to 15, "Weight" to 16, "Weight" to 17
            )
        )

        // Step 3: Organize the workouts into a plan spanning 8 weeks
        val plan = Plan(
            name = "8-Week Women's Dumbbell Program",
            weeks = 8,
            workouts = listOf(day1, day2, day3),
            planType = PlanType.DUMBBELLS
        )
        plan.startWeeks()

        // Save to repositories
        viewModelScope.launch {
            val exercisesList = planBuilder.getExercises()
            exercisesList.forEach { exercisesRepository.insertExercise(it) }
            workoutRepository.insertWorkout(day1)
            workoutRepository.insertWorkout(day2)
            workoutRepository.insertWorkout(day3)
            planRepository.insertPlan(plan)
            ready = true
        }
    }
}