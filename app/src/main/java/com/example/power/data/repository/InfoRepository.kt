package com.example.power.data.repository

import com.example.power.data.room.Info
import com.example.power.data.room.dao.InfoDao
import kotlinx.coroutines.flow.Flow

class InfoRepository (private val infoDao: InfoDao) {
    fun getInfo(): Flow<Info?> = infoDao.getInfoById("1")
    suspend fun insertInfo() = infoDao.insertIfNotExists(Info(1))
    suspend fun deleteInfo() = infoDao.delete(Info(1))
    suspend fun updateInfo(info: Info) {
        infoDao.update(info)
    }
}