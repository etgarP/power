package com.example.power.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.power.data.room.Info

@Dao
interface InfoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(info: Info)

    @Update
    suspend fun update(info: Info)

    @Query("SELECT * FROM info WHERE id = :id")
    fun getInfoById(id:String): Info?
}