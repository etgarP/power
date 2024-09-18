package com.example.power.data.repository

import com.example.power.data.room.Plan
import com.example.power.data.room.dao.PlanDao
import kotlinx.coroutines.flow.Flow

/**
 * manages the plan in the ui
 */
class PlanRepository (private val planDao: PlanDao) {
    // returns a live updated list of the plans
    fun getAllPlansStream(): Flow<List<Plan>> = planDao.getAllPlans()
    // returns a live updated plans
    fun getPlanStream(id: Int): Flow<Plan?> = planDao.getPlanById(id)
    // inserts a new plan
    suspend fun insertPlan(item: Plan) = planDao.insert(item)
    // deletes a plan
    suspend fun deletePlan(item: Plan) = planDao.delete(item)
    // updates a plan
    suspend fun updatePlan(item: Plan) = planDao.update(item)
    // gets a plan by its name
    suspend fun getPlanByName(planName: String): Plan? =
        planDao.getPlanByName(planName)
}