package com.example.power.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.power.data.room.Workout
import kotlinx.coroutines.flow.Flow

/**
 * stores the workout in the room database
 */
@Dao
interface WorkoutDao {

    /**
     * inserts a workout, on conflict ignores
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(workout: Workout)

    /**
     * updates a workout
     */
    @Update
    suspend fun update(workout: Workout)

    /**
     * deletes a workout
     */
    @Delete
    suspend fun delete(workout: Workout)

    /**
     * gets a flow of all workouts
     */
    @Query("SELECT * from workouts ORDER BY name ASC")
    fun getAllWorkouts(): Flow<List<Workout>>

    /**
     * gets a workout by id
     */
    @Query("SELECT * from workouts WHERE id = :id")
    fun getWorkoutById(id: Int): Workout?

    /**
     * gets a workout by name
     */
    @Query("SELECT * FROM workouts WHERE name = :workoutName")
    suspend fun getWorkoutByName(workoutName: String): Workout?

}