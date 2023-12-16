package com.example.power.data.repository

import com.example.power.data.room.Workout
import com.example.power.data.room.dao.WorkoutDao
import kotlinx.coroutines.flow.Flow

class WorkoutRepository (private val workoutDao: WorkoutDao) {
    fun getAllWorkoutsStream(): Flow<List<Workout>> = workoutDao.getAllWorkouts()

    fun getWorkout(id: Int): Workout? = workoutDao.getWorkoutById(id)

    suspend fun insertWorkout(item: Workout) = workoutDao.insert(item)

    suspend fun deleteWorkout(item: Workout) = workoutDao.delete(item)

    suspend fun updateWorkout(item: Workout) = workoutDao.update(item)

    suspend fun getWorkoutByName(workoutName: String): Workout? =
       workoutDao.getWorkoutByName(workoutName)
}