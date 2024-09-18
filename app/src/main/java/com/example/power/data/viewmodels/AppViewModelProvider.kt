package com.example.power.data.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.power.PowerApplication
import com.example.power.data.viewmodels.exercise.ExerciseEntryViewModel
import com.example.power.data.viewmodels.exercise.ExerciseViewModel
import com.example.power.data.viewmodels.plan.PlanEntryViewModel
import com.example.power.data.viewmodels.plan.PlanViewModel
import com.example.power.data.viewmodels.workout.WorkoutEntryViewModel
import com.example.power.data.viewmodels.workout.WorkoutViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {

        // Initializer for ItemEntryViewModel
        initializer {
            ExerciseEntryViewModel(powerApplication().container.exercisesRepository)
        }

        // Initializer for ExerciseViewModel
        initializer {
            ExerciseViewModel(
                powerApplication().container.exercisesRepository,
                powerApplication().container.workoutsRepository
            )
        }

        // Initializer for WorkoutViewModel
        initializer {
            WorkoutViewModel(
                powerApplication().container.workoutsRepository,
                powerApplication().container.plansRepository
            )
        }
        initializer {
            WorkoutEntryViewModel(
                powerApplication().container.workoutsRepository
            )
        }
        initializer {
            PlanViewModel(powerApplication().container.plansRepository)
        }
        initializer {
            PlanEntryViewModel(powerApplication().container.plansRepository)
        }
        initializer {
            InfoViewModel(powerApplication().container.infoRepository)
        }
        initializer {
            LoadInitialDataViewModel(
                powerApplication().container.exercisesRepository,
                powerApplication().container.workoutsRepository,
                powerApplication().container.plansRepository,
                powerApplication().container.infoRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.powerApplication(): PowerApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as PowerApplication)
