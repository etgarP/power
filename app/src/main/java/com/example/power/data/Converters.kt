package com.example.power.data

import androidx.room.TypeConverter
import com.example.power.data.room.ExerciseHolder
import com.example.power.data.room.Week
import com.example.power.data.room.Workout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.Date

class Converters {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(ExerciseHolder::class.java, ExerciseHolderDeserializer())
        .create()

    @TypeConverter
    fun fromExerciseHolderList(exerciseHolders: List<ExerciseHolder>): String {
        return gson.toJson(exerciseHolders)
    }

    @TypeConverter
    fun toExerciseHolderList(json: String): List<ExerciseHolder> {
        val type = object : TypeToken<List<ExerciseHolder>>() {}.type
        return gson.fromJson(json, type)
    }
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
    // TypeConverter for List<Workout>
    @TypeConverter
    fun fromWorkoutList(workoutList: List<Workout>?): String {
        return Gson().toJson(workoutList)
    }

    @TypeConverter
    fun toWorkoutList(workoutListString: String): List<Workout> {
        val listType = object : TypeToken<List<Workout>>() {}.type
        return Gson().fromJson(workoutListString, listType)
    }
    // TypeConverter for List<Week>
    @TypeConverter
    fun fromWeekList(weekList: List<Week>?): String {
        return Gson().toJson(weekList)
    }

    @TypeConverter
    fun toWeekList(weekListString: String): List<Week> {
        val listType = object : TypeToken<List<Week>>() {}.type
        return Gson().fromJson(weekListString, listType)
    }
}