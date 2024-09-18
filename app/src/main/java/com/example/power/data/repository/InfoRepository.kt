package com.example.power.data.repository

import com.example.power.data.room.Info
import com.example.power.data.room.dao.InfoDao
import kotlinx.coroutines.flow.Flow

/**
 * manages the basic info for the app user in the ui
 */
class InfoRepository (private val infoDao: InfoDao) {
    // get function
    fun getInfo(): Flow<Info?> = infoDao.getInfoById("1")
    // inserts a new info for first boot of the app
    suspend fun insertInfo() = infoDao.insertIfNotExists(Info(1))
    // deletes the info
    suspend fun deleteInfo() = infoDao.delete(Info(1))
    // updates the info
    suspend fun updateInfo(info: Info) {
        infoDao.update(info)
    }
}