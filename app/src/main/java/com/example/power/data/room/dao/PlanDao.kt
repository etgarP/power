package com.example.power.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.power.data.room.Plan
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(plan: Plan)

    @Update
    suspend fun update(plan: Plan)

    @Delete
    suspend fun delete(plan: Plan)

    @Query("SELECT * from plans ORDER BY name ASC")
    fun getAllPlans(): Flow<List<Plan>>

    @Query("SELECT * from plans WHERE id = :id")
    fun getPlanById(id: Int): Flow<Plan>

    @Query("SELECT * FROM plans WHERE name = :planName")
    suspend fun getPlanByName(planName: String): Plan?

}