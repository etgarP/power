package com.example.power.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.power.data.Converters
import com.example.power.data.room.dao.ExerciseDao
import com.example.power.data.room.dao.InfoDao
import com.example.power.data.room.dao.PlanDao
import com.example.power.data.room.dao.WorkoutDao

/**
 * Database class with a singleton Instance object.
 */
@Database(
    entities = [Exercise::class, Workout::class, Plan::class, Info::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun planDao(): PlanDao
    abstract fun infoDao(): InfoDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}