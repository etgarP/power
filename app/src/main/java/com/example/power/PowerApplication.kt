package com.example.power

import android.app.Application
import com.example.power.data.ExerciseHolderDeserializer
import com.example.power.data.room.ExerciseHolder
import com.example.power.data.room.Workout
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class PowerApplication : Application() {
    val gson: Gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(ExerciseHolder::class.java, ExerciseHolderDeserializer())
            .registerTypeAdapter(Workout::class.java, ExerciseHolderDeserializer())
            .create()
    }

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
