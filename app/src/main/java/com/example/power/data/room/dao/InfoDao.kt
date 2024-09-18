package com.example.power.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.power.data.room.Info
import kotlinx.coroutines.flow.Flow

/**
 * stores the persistent info about the user in the room database
 */
@Dao
interface InfoDao {

    /**
     * updates info
     */
    @Update
    suspend fun update(info: Info)

    /**
     * inserts inserts if they dont exist, on conflict ignores the request
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfNotExists(info: Info)

    /**
     * deletes and info
     */
    @Delete
    suspend fun delete(info: Info)

    /**
     * gets info by id
     */
    @Query("SELECT * FROM info WHERE id = :id")
    fun getInfoById(id:String): Flow<Info?>
}