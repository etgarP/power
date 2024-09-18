package com.example.power.data.repository.exercise

import com.example.power.data.room.Exercise
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Exercise] from a given data source.
 */
interface ExercisesRepository {
    /**
     * Retrieve all the exercises from the the given data source.
     */
    fun getAllExercisesStream(): Flow<List<Exercise>>

    /**
     * Retrieve an exercise from the given data source that matches with the [id].
     */
    fun getExerciseStream(id: Int): Flow<Exercise?>

    /**
     * Insert exercise in the data source
     */
    suspend fun insertExercise(item: Exercise)

    /**
     * Delete exercise from the data source
     */
    suspend fun deleteExercise(item: Exercise)

    /**
     * Update exercise in the data source
     */
    suspend fun updateExercise(item: Exercise)

    /**
     * gets an exercise by its name
     */
    suspend fun getExerciseByName(exerciseName: String): Exercise?
}