package com.example.power.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.power.data.room.Info
import kotlinx.coroutines.flow.Flow

@Dao
interface InfoDao {
    @Update
    suspend fun update(info: Info)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfNotExists(info: Info)

    @Delete
    suspend fun delete(info: Info)

    @Query("SELECT * FROM info WHERE id = :id")
    fun getInfoById(id:String): Flow<Info?>
}