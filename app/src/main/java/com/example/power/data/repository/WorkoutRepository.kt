package com.example.power.data.repository

import com.example.power.data.room.Workout
import com.example.power.data.room.dao.WorkoutDao
import kotlinx.coroutines.flow.Flow

/**
 * manages the workouts in the ui
 */
class WorkoutRepository (private val workoutDao: WorkoutDao) {
    // returns a live updated list of the workouts
    fun getAllWorkoutsStream(): Flow<List<Workout>> = workoutDao.getAllWorkouts()
    // returns a workout by id
    fun getWorkout(id: Int): Workout? = workoutDao.getWorkoutById(id)
    // inserts a new workout
    suspend fun insertWorkout(item: Workout) = workoutDao.insert(item)
    // deletes a workout
    suspend fun deleteWorkout(item: Workout) = workoutDao.delete(item)
    // updates a workout
    suspend fun updateWorkout(item: Workout) = workoutDao.update(item)
    // gets a workout by name
    suspend fun getWorkoutByName(workoutName: String): Workout? =
       workoutDao.getWorkoutByName(workoutName)
}