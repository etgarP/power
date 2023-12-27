package com.example.power

import android.app.NotificationManager
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
    val scope: Context
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
    override val scope: Context
        get() = context

    private val notificationManager=context.getSystemService(NotificationManager::class.java)
//    override fun breakOverNotification(){
//        val notification= NotificationCompat.Builder(context,"water_notification")
//            .setContentTitle("Break Over")
//            .setContentText("Get back to it :)")
////            .setSmallIcon(R.drawable.water_icon)
//            .setPriority(NotificationManager.IMPORTANCE_HIGH)
//            .setAutoCancel(true)
//            .build()
//
//        notificationManager.notify(
//            Random.nextInt(),
//            notification
//        )
//    }
}
