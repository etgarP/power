package com.example.power

import android.content.Context
import com.example.power.data.repository.PlanRepository
import com.example.power.data.repository.WorkoutRepository
import com.example.power.data.repository.exercise.ExercisesRepository
import com.example.power.data.repository.exercise.OfflineExercisesRepository
import com.example.power.data.room.AppDatabase

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val exercisesRepository: ExercisesRepository
    val workoutsRepository: WorkoutRepository
    val plansRepository: PlanRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val exercisesRepository: ExercisesRepository by lazy {
        OfflineExercisesRepository(AppDatabase.getDatabase(context).exerciseDao())
    }
    override val workoutsRepository: WorkoutRepository by lazy {
        WorkoutRepository(AppDatabase.getDatabase(context).workoutDao())
    }
    override val plansRepository: PlanRepository by lazy {
        PlanRepository(AppDatabase.getDatabase(context).planDao())
    }
}
