package com.example.power.data

import com.example.power.data.room.CardioExercise
import com.example.power.data.room.Exercise
import com.example.power.data.room.ExerciseHolder
import com.example.power.data.room.ExerciseType
import com.example.power.data.room.RepsExercise
import com.example.power.data.room.TimeExercise
import com.example.power.data.room.WeightExercise
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class ExerciseHolderDeserializer : JsonDeserializer<ExerciseHolder> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ExerciseHolder {
        val jsonObject = json.asJsonObject
        val exercise1 = jsonObject.getAsJsonObject("exercise")


        // Extract the exercise type information
        val exercise = Gson().fromJson(exercise1, Exercise::class.java)

        // Use a when statement or another logic to determine the ExerciseHolder type
        val exerciseHolderType = when (exercise.type) {
            ExerciseType.REPS -> RepsExercise::class.java
            ExerciseType.DURATION -> TimeExercise::class.java
            ExerciseType.CARDIO -> CardioExercise::class.java
            ExerciseType.WEIGHT -> WeightExercise::class.java
            else -> throw IllegalArgumentException("Unknown ExerciseHolder type: $exercise.type")
        }

        // Use the determined type to deserialize the ExerciseHolder
        return Gson().fromJson(json, exerciseHolderType)
    }
}
