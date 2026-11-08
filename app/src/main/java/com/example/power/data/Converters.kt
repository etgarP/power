package com.example.power.data

import android.util.Log
import androidx.room.TypeConverter
import com.example.power.data.room.BodyType
import com.example.power.data.room.ExerciseHolder
import com.example.power.data.room.ExerciseType
import com.example.power.data.room.HistoryItem
import com.example.power.data.room.Plan
import com.example.power.data.room.PlanType
import com.example.power.data.room.Week
import com.example.power.data.room.Workout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.Collections.emptyList
import java.util.Date
import com.google.gson.JsonElement
import com.google.gson.JsonParser

/**
 * converts objects to jsons and back to store on to store on the repository and retrieve
 */
class Converters {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(ExerciseHolder::class.java, ExerciseHolderDeserializer())
        .create()
    private val serializationGson = Gson()

    @TypeConverter
    fun fromPlan(plan: Plan?): String? {
        if (plan == null) {
            return null
        }
        return Gson().toJson(plan)
    }

    @TypeConverter
    fun toPlan(planJson: String?): Plan? {
        if (planJson == null) {
            return null
        }
        val type = object : TypeToken<Plan>() {}.type
        return Gson().fromJson(planJson, type)
    }

    @TypeConverter
    fun fromJson(json: String?): MutableList<HistoryItem>? {
        if (json == null) {
            return emptyList()
        }
        val type = object : TypeToken<List<HistoryItem>>() {}.type
        return Gson().fromJson(json, object : TypeToken<MutableList<HistoryItem>>() {}.type)
    }

    @TypeConverter
    fun toJson(list: List<HistoryItem>?): String {
        return Gson().toJson(list)
    }

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
            val serializedExercises = serializationGson.toJson(
                workout.exercises.map { exercise ->
                    serializationGson.toJsonTree(exercise, exercise::class.java)
                }
            )
            val serializedWorkout = gson.toJson(workout.copy(exercises = emptyList()))
            "{\"workout\":$serializedWorkout,\"exercises\":$serializedExercises}"
        }
        return gson.toJson(serializedWorkouts)
    }

    @TypeConverter
    fun toWorkoutList(workoutListString: String): List<Workout> {
        val serializedWorkouts = gson.fromJson<List<JsonElement>>(
            workoutListString,
            object : TypeToken<List<JsonElement>>() {}.type
        )

        return serializedWorkouts.map { serialized ->
            val json = JsonParser.parseString(serialized.asString).asJsonObject

            val workout = gson.fromJson(
                json.getAsJsonObject("workout"),
                Workout::class.java
            )

            val exercises: List<ExerciseHolder> = gson.fromJson(
                json.getAsJsonArray("exercises"),
                object : TypeToken<List<ExerciseHolder>>() {}.type
            )

            workout.copy(exercises = exercises)
        }
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

    @TypeConverter
    fun fromExerciseType(value: ExerciseType): String = value.name

    @TypeConverter
    fun toExerciseType(value: String): ExerciseType =
        ExerciseType.valueOf(value)

    @TypeConverter
    fun fromBodyType(value: BodyType): String = value.name

    @TypeConverter
    fun toBodyType(value: String): BodyType =
        BodyType.valueOf(value)
}