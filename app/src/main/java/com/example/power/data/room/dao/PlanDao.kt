package com.example.power.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.power.data.room.Plan
import kotlinx.coroutines.flow.Flow

/**
 * stores the plan in the room database
 */
@Dao
interface PlanDao {

    /**
     * inserts a plan, on conflict ignores
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(plan: Plan)

    /**
     * updates a plan
     */
    @Update
    suspend fun update(plan: Plan)

    /**
     * deletes a plan
     */
    @Delete
    suspend fun delete(plan: Plan)

    /**
     * returns a flow of all plan ordered by name
     */
    @Query("SELECT * from plans ORDER BY name ASC")
    fun getAllPlans(): Flow<List<Plan>>

    /**
     * returns a flow of a plan by its id
     */
    @Query("SELECT * from plans WHERE id = :id")
    fun getPlanById(id: Int): Flow<Plan>

    /**
     * return a plan by its name
     */
    @Query("SELECT * FROM plans WHERE name = :planName")
    suspend fun getPlanByName(planName: String): Plan?

}