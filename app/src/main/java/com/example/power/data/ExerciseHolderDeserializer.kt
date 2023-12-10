package com.example.power.data

import com.example.power.data.room.ExerciseHolder
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
        // Implement logic to determine the actual type and create an instance
        // You may use different Gson.fromJson calls based on your logic.
        return Gson().fromJson(json, WeightExercise::class.java)
    }
}