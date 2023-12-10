package com.example.power.data.repository

import com.example.power.data.room.Plan
import com.example.power.data.room.dao.PlanDao
import kotlinx.coroutines.flow.Flow

class PlanRepository (private val planDao: PlanDao) {
    fun getAllPlansStream(): Flow<List<Plan>> = planDao.getAllPlans()

    fun getPlanStream(id: Int): Flow<Plan?> = planDao.getPlanById(id)

    suspend fun insertPlan(item: Plan) = planDao.insert(item)

    suspend fun deletePlan(item: Plan) = planDao.delete(item)

    suspend fun updatePlan(item: Plan) = planDao.update(item)

    suspend fun getPlanByName(planName: String): Plan? =
        planDao.getPlanByName(planName)
}