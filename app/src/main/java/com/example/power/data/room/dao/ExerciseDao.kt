package com.example.power.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.power.data.room.Exercise
import kotlinx.coroutines.flow.Flow

/**
 * stores the workouts in the room database
 */
@Dao
interface ExerciseDao {

    /**
     * insters an exercise
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(exercise: Exercise)

    /**
     * updates an exercise
     */

    @Update
    suspend fun update(exercise: Exercise)

    /**
     * deletes an exercise
     */
    @Delete
    suspend fun delete(exercise: Exercise)

    /**
     * gets all exercises ordered by name
     */
    @Query("SELECT * from exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<Exercise>>

    /**
     * gets exercise by id
     */
    @Query("SELECT * from exercises WHERE id = :id")
    fun getExerciseById(id: Int): Flow<Exercise>

    /**
     * gets exercise by name
     */
    @Query("SELECT * FROM exercises WHERE name = :exerciseName")
    suspend fun getExerciseByName(exerciseName: String): Exercise?

}