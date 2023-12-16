package com.example.power.data

import androidx.room.TypeConverter
import com.example.power.data.room.ExerciseHolder
import com.example.power.data.room.PlanType
import com.example.power.data.room.Week
import com.example.power.data.room.Workout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
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
    fun fromWorkoutList(workoutList: List<Workout>): String {
        val serializedWorkouts = workoutList.map { workout ->
            val serializedExercises = gson.toJson(workout.exercises)
            val serializedWorkout = gson.toJson(workout.copy(exercises = emptyList()))
            "{\"workout\":$serializedWorkout,\"exercises\":$serializedExercises}"
        }
        return gson.toJson(serializedWorkouts)
    }

    @TypeConverter
    fun toWorkoutList(workoutListString: String): List<Workout> {
        val serializedWorkouts = gson.fromJson<List<String>>(workoutListString, object : TypeToken<List<String>>() {}.type)
        val ret = serializedWorkouts.map { serialized ->
            val json = gson.fromJson(serialized, JsonElement::class.java).asJsonObject
            val workout = gson.fromJson(json.getAsJsonObject("workout"), Workout::class.java)
            val exercises: List<ExerciseHolder> = gson.fromJson(json.getAsJsonArray("exercises"), object : TypeToken<List<ExerciseHolder>>() {}.type)
            workout.copy(exercises = exercises)
        }
        return ret
    }
    // TypeConverter for List<Week>
    @TypeConverter
    fun fromWeekList(weekList: List<Week>?): String {
        val string: String =Gson().toJson(weekList)
        return string
    }

    @TypeConverter
    fun toWeekList(weekListString: String): List<Week> {
        val listType = object : TypeToken<List<Week>>() {}.type
        return Gson().fromJson(weekListString, listType)
    }
    // TypeConverter for PlanType
    @TypeConverter
    fun fromPlanType(planType: PlanType): String {
        return planType.toString()
    }

    @TypeConverter
    fun toPlanType(planTypeString: String): PlanType {
        return PlanType.valueOf(planTypeString)
    }

}