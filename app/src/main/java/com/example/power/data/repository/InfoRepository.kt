package com.example.power.data.repository

import com.example.power.data.room.Info
import com.example.power.data.room.dao.InfoDao

class InfoRepository (private val infoDao: InfoDao) {
    suspend fun getInfo(): Info {
        // Try to get existing info by ID
        val existingInfo = infoDao.getInfoById("1")

        // If existing info is null, create a new one with ID 1
        return existingInfo?: Info(id = 1).also {
            // Insert the new info into the database
            infoDao.insert(it)
        }
    }
    suspend fun updateInfo(info: Info) {
        infoDao.update(info)
    }
}