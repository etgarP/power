package com.example.power.data.repository.exercise

import com.example.power.data.room.Exercise
import com.example.power.data.room.dao.ExerciseDao
import kotlinx.coroutines.flow.Flow

/**
 * same as exercise repository
 */
class OfflineExercisesRepository(private val exerciseDao: ExerciseDao) : ExercisesRepository {
    override fun getAllExercisesStream(): Flow<List<Exercise>> = exerciseDao.getAllExercises()

    override fun getExerciseStream(id: Int): Flow<Exercise?> = exerciseDao.getExerciseById(id)

    override suspend fun insertExercise(item: Exercise) = exerciseDao.insert(item)

    override suspend fun deleteExercise(item: Exercise) = exerciseDao.delete(item)

    override suspend fun updateExercise(item: Exercise) = exerciseDao.update(item)

    override suspend fun getExerciseByName(exerciseName: String): Exercise? =
        exerciseDao.getExerciseByName(exerciseName)
}
