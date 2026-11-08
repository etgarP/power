package com.example.power.data.viewmodels

import ExerciseConfig
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
import com.example.power.data.room.RepsExercise
import com.example.power.data.room.TimeExercise
import com.example.power.data.room.WeightExercise
import kotlinx.coroutines.launch


/**
 * Loading initial data to the app. Should be used only the first time the app
 * is opened.
 */
class LoadInitialDataViewModel(
    private val exercisesRepository: ExercisesRepository,
    private val workoutRepository: WorkoutRepository,
    private val planRepository: PlanRepository,
    private val infoRepository: InfoRepository
) : ViewModel() {

    /**
     * Says if the app is ready to be opened, which is when the repository loaded.
     * Not currently used, but can be used if loading takes longer.
     */
    var ready by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            infoRepository.getInfo().collect {
                if (it == null) {
                    viewModelScope.launch {
                        loadRepository()
                        ready = true
                    }
                } else {
                    ready = true
                }
            }
        }
    }

    /**
     * Loads plans to the repository, with their exercises and workouts.
     */
    private fun loadRepository() {
        loadDumbbell3days()
        loadWomensDumbbellWorkout3Days()
        loadThreeWorkouts()
    }

    private fun loadDumbbell3days() {
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

        val day1 = planBuilder.buildWorkout(
            "Day 1",
            listOf(
                ExerciseConfig("Weight", 0),
                ExerciseConfig("Weight", 1),
                ExerciseConfig("Weight", 2),
                ExerciseConfig("Weight", 3),
                ExerciseConfig("Weight", 4),
                ExerciseConfig("Weight", 5),
                ExerciseConfig("Weight", 6)
            )
        )

        val day2 = planBuilder.buildWorkout(
            "Day 2",
            listOf(
                ExerciseConfig("Weight", 7),
                ExerciseConfig("Weight", 8),
                ExerciseConfig("Weight", 9),
                ExerciseConfig("Weight", 10),
                ExerciseConfig("Weight", 11),
                ExerciseConfig("Weight", 12),
                ExerciseConfig("Weight", 13)
            )
        )

        val day3 = planBuilder.buildWorkout(
            "Day 3",
            listOf(
                ExerciseConfig("Weight", 14),
                ExerciseConfig("Weight", 15),
                ExerciseConfig("Weight", 16),
                ExerciseConfig("Weight", 17),
                ExerciseConfig("Weight", 18),
                ExerciseConfig("Weight", 19),
                ExerciseConfig("Weight", 20)
            )
        )

        val plan = Plan(
            name = "8-Week Dumbbell Program",
            weeks = 8,
            workouts = listOf(day1, day2, day3),
            planType = PlanType.DUMBBELLS
        )
        plan.startWeeks()

        viewModelScope.launch {
            planBuilder.getExercises().forEach {
                exercisesRepository.insertExercise(it)
            }
            workoutRepository.insertWorkout(day1)
            workoutRepository.insertWorkout(day2)
            workoutRepository.insertWorkout(day3)
            planRepository.insertPlan(plan)
            ready = true
        }
    }

    private fun loadWomensDumbbellWorkout3Days() {
        val planBuilder = PlanBuilder()

        planBuilder.apply {
            addExercise(ExerciseType.REPS, BodyType.CORE, "Ab Crunch")
            addExercise(ExerciseType.REPS, BodyType.CORE, "Lying Leg Raise")
            addExercise(ExerciseType.REPS, BodyType.CORE, "Side Oblique Crunch (each side)")
            addExercise(ExerciseType.REPS, BodyType.LEGS, "Glute Kick Back")
            addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Dumbbell Romanian Deadlift")
            addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Reverse Lunge")
            addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Dumbbell Squat")
            addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Dumbbell Lunge (each side)")
            addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Dumbbell Lying Leg Curl (on the floor)")
            addExercise(ExerciseType.REPS, BodyType.LEGS, "Bodyweight Single Leg Deadlift")
            addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Seated Calf Raise")
            addExercise(ExerciseType.REPS, BodyType.LEGS, "Standing Calf Raise")
            addExercise(ExerciseType.WEIGHT, BodyType.CHEST, "Dumbbell Bench Press (on the floor)")
            addExercise(ExerciseType.WEIGHT, BodyType.BACK, "Bent-Over Dumbbell Row")
            addExercise(ExerciseType.WEIGHT, BodyType.CHEST, "Dumbbell Pullover")
            addExercise(ExerciseType.WEIGHT, BodyType.SHOULDERS, "Lateral Raise")
            addExercise(ExerciseType.WEIGHT, BodyType.ARMS, "Lying Dumbbell Extension")
            addExercise(ExerciseType.WEIGHT, BodyType.ARMS, "Hammer Dumbbell Curl")
        }

        val day1 = planBuilder.buildWorkout(
            dayName = "Day 1 - Abs And Glutes",
            exerciseConfigs = listOf(
                ExerciseConfig("Reps", 0),
                ExerciseConfig("Reps", 1),
                ExerciseConfig("Reps", 2),
                ExerciseConfig("Reps", 3),
                ExerciseConfig("Weight", 4),
                ExerciseConfig("Weight", 5)
            )
        )

        val day2 = planBuilder.buildWorkout(
            dayName = "Day 2 - Lower Body",
            exerciseConfigs = listOf(
                ExerciseConfig("Weight", 6),
                ExerciseConfig("Weight", 7),
                ExerciseConfig("Weight", 8),
                ExerciseConfig("Reps", 9),
                ExerciseConfig("Weight", 10),
                ExerciseConfig("Reps", 11)
            )
        )

        val day3 = planBuilder.buildWorkout(
            dayName = "Day 3 - Upper Body",
            exerciseConfigs = listOf(
                ExerciseConfig("Weight", 12),
                ExerciseConfig("Weight", 13),
                ExerciseConfig("Weight", 14),
                ExerciseConfig("Weight", 15),
                ExerciseConfig("Weight", 16),
                ExerciseConfig("Weight", 17)
            )
        )

        val plan = Plan(
            name = "8-Week Women's Dumbbell Program",
            weeks = 8,
            workouts = listOf(day1, day2, day3),
            planType = PlanType.DUMBBELLS
        )
        plan.startWeeks()

        viewModelScope.launch {
            planBuilder.getExercises().forEach {
                exercisesRepository.insertExercise(it)
            }
            workoutRepository.insertWorkout(day1)
            workoutRepository.insertWorkout(day2)
            workoutRepository.insertWorkout(day3)
            planRepository.insertPlan(plan)
            ready = true
        }
    }

    /**
     * Loads the three recorded gym workouts as one plan.
     * Each exercise holder stores its individual set count and values.
     */
    private fun loadThreeWorkouts() {
        // Workout 1: 5 exercises, 16 sets.
        val planBuilder1 = PlanBuilder()

        planBuilder1
            .addExercise(ExerciseType.WEIGHT, BodyType.CHEST, "Bench Press")
            .addExercise(ExerciseType.REPS, BodyType.BACK, "Pull Up")
            .addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Romanian Deadlift")
            .addExercise(ExerciseType.WEIGHT, BodyType.SHOULDERS, "Lateral Raise")
            .addExercise(ExerciseType.REPS, BodyType.CORE, "Hanging Leg Raise")

        val workout1 = planBuilder1.buildWorkout(
            dayName = "Workout 1 - Chest, Back, Legs",
            exerciseConfigs = listOf(
                ExerciseConfig("Weight", 0, 4),
                ExerciseConfig("Reps", 1, 4),
                ExerciseConfig("Weight", 2, 3),
                ExerciseConfig("Weight", 3, 3),
                ExerciseConfig("Reps", 4, 2)
            )
        ).apply {
            (exercises[0] as WeightExercise).apply {
                weights = mutableListOf(40.0, 45.0, 50.0, 50.0)
                reps = mutableListOf(8, 8, 4, 4)
            }
            (exercises[1] as RepsExercise).apply {
                reps = mutableListOf(6, 4, 3, 4)
            }
            (exercises[2] as WeightExercise).apply {
                weights = mutableListOf(20.0, 25.0, 30.0)
                reps = mutableListOf(9, 9, 9)
            }
            (exercises[3] as WeightExercise).apply {
                weights = mutableListOf(8.0, 8.0, 8.0)
                reps = mutableListOf(16, 10, 8)
            }
            (exercises[4] as RepsExercise).apply {
                reps = mutableListOf(8, 8)
            }
        }

        // Workout 2: 6 exercises, 17 sets.
        val planBuilder2 = PlanBuilder()

        planBuilder2
            .addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Deadlift")
            .addExercise(ExerciseType.WEIGHT, BodyType.SHOULDERS, "Overhead Press")
            .addExercise(ExerciseType.WEIGHT, BodyType.BACK, "Bent-over Row")
            .addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Bulgarian Split Squat")
            .addExercise(ExerciseType.REPS, BodyType.ARMS, "Triceps Dip")
            .addExercise(ExerciseType.REPS, BodyType.CORE, "Ab Wheel")

        val workout2 = planBuilder2.buildWorkout(
            dayName = "Workout 2 - Legs, Shoulders, Back",
            exerciseConfigs = listOf(
                ExerciseConfig("Weight", 0, 4),
                ExerciseConfig("Weight", 1, 4),
                ExerciseConfig("Weight", 2, 4),
                ExerciseConfig("Weight", 3, 2),
                ExerciseConfig("Reps", 4, 3),
                ExerciseConfig("Reps", 5, 2)
            )
        ).apply {
            (exercises[0] as WeightExercise).apply {
                weights = mutableListOf(20.0, 25.0, 30.0, 30.0)
                reps = mutableListOf(8, 8, 8, 8)
            }
            (exercises[1] as WeightExercise).apply {
                weights = mutableListOf(30.0, 30.0, 30.0, 30.0)
                reps = mutableListOf(7, 7, 6, 5)
            }
            (exercises[2] as WeightExercise).apply {
                weights = mutableListOf(40.0, 40.0, 40.0, 40.0)
                reps = mutableListOf(8, 8, 8, 8)
            }
            (exercises[3] as WeightExercise).apply {
                weights = mutableListOf(13.5, 13.5)
                reps = mutableListOf(7, 7)
            }
            (exercises[4] as RepsExercise).apply {
                reps = mutableListOf(5, 5, 5)
            }
            (exercises[5] as RepsExercise).apply {
                reps = mutableListOf(10, 8)
            }
        }

        // Workout 3: 6 exercises, 19 sets.
        val planBuilder3 = PlanBuilder()

        planBuilder3
            .addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Front Squat")
            .addExercise(ExerciseType.WEIGHT, BodyType.CHEST, "Incline Bench Press")
            .addExercise(ExerciseType.REPS, BodyType.BACK, "Chin Up")
            .addExercise(ExerciseType.WEIGHT, BodyType.LEGS, "Romanian Deadlift")
            .addExercise(ExerciseType.WEIGHT, BodyType.ARMS, "Bicep Curl")
            .addExercise(ExerciseType.DURATION, BodyType.CORE, "Side Plank")

        val workout3 = planBuilder3.buildWorkout(
            dayName = "Workout 3 - Legs, Chest, Back",
            exerciseConfigs = listOf(
                ExerciseConfig("Weight", 0, 4),
                ExerciseConfig("Weight", 1, 4),
                ExerciseConfig("Reps", 2, 4),
                ExerciseConfig("Weight", 3, 3),
                ExerciseConfig("Weight", 4, 2),
                ExerciseConfig("Time", 5, 2)
            )
        ).apply {
            (exercises[0] as WeightExercise).apply {
                weights = mutableListOf(20.0, 25.0, 30.0, 40.0)
                reps = mutableListOf(8, 7, 7, 7)
            }
            (exercises[1] as WeightExercise).apply {
                weights = mutableListOf(35.0, 40.0, 45.0, 35.0)
                reps = mutableListOf(10, 10, 10, 10)
            }
            (exercises[2] as RepsExercise).apply {
                reps = mutableListOf(5, 5, 5, 5)
            }
            (exercises[3] as WeightExercise).apply {
                weights = mutableListOf(65.0, 65.0, 65.0)
                reps = mutableListOf(9, 9, 6)
            }
            (exercises[4] as WeightExercise).apply {
                weights = mutableListOf(11.0, 11.0)
                reps = mutableListOf(10, 9)
            }
            (exercises[5] as TimeExercise).apply {
                seconds = mutableListOf(60, 40)
            }
        }

        val plan = Plan(
            name = "Gym workout full body",
            weeks = 8,
            workouts = listOf(workout1, workout2, workout3),
            planType = PlanType.GYM
        )
        plan.startWeeks()

        viewModelScope.launch {
            val allExercises = listOf(
                planBuilder1.getExercises(),
                planBuilder2.getExercises(),
                planBuilder3.getExercises()
            ).flatten()

            allExercises.forEach {
                exercisesRepository.insertExercise(it)
            }

            workoutRepository.insertWorkout(workout1)
            workoutRepository.insertWorkout(workout2)
            workoutRepository.insertWorkout(workout3)
            planRepository.insertPlan(plan)
            ready = true
        }
    }
}