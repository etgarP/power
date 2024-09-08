package com.example.power.data.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.Date


enum class ExerciseType {
    REPS,
    DURATION,
    CARDIO,
    WEIGHT;
}
enum class BodyType {
    CORE,
    ARMS,
    BACK,
    CHEST,
    LEGS,
    SHOULDERS,
    CARDIO,
    OTHER
}
enum class PlanType {
    CARDIO,
    GYM,
    BODYWEIGHT,
    DUMBBELLS
}
val planTypeToStringMap = mapOf(
    PlanType.CARDIO to "Cardio Plan",
    PlanType.GYM to "Gym Plan",
    PlanType.BODYWEIGHT to "Body-Weight Plan",
    PlanType.DUMBBELLS to "Dumbbells Plan",
)
val stringToPlanTypeMap = mapOf(
    "Cardio Plan" to PlanType.CARDIO,
    "Gym Plan" to PlanType.GYM,
    "Body-Weight Plan" to PlanType.BODYWEIGHT,
    "Dumbbells Plan" to PlanType.DUMBBELLS,
)
val exerciseTypeMap: Map<ExerciseType, String> = mapOf(
    ExerciseType.REPS to "Reps",
    ExerciseType.DURATION to "Duration",
    ExerciseType.CARDIO to "Cardio",
    ExerciseType.WEIGHT to "Weight"
)
val bodyTypeMap: Map<BodyType, String> = mapOf(
    BodyType.CORE to "Core",
    BodyType.ARMS to "Arms",
    BodyType.BACK to "Back",
    BodyType.CHEST to "Chest",
    BodyType.LEGS to "Legs",
    BodyType.SHOULDERS to "Shoulders",
    BodyType.CARDIO to "Cardio",
    BodyType.OTHER to "Other"
)
val exerciseTypeMapFromString: Map<String, ExerciseType> = mapOf(
    "Reps" to ExerciseType.REPS,
    "Duration" to ExerciseType.DURATION,
    "Cardio" to ExerciseType.CARDIO,
    "Weight" to ExerciseType.WEIGHT
)

val bodyTypeMapFromString: Map<String, BodyType> = mapOf(
    "Core" to BodyType.CORE,
    "Arms" to BodyType.ARMS,
    "Back" to BodyType.BACK,
    "Chest" to BodyType.CHEST,
    "Legs" to BodyType.LEGS,
    "Shoulders" to BodyType.SHOULDERS,
    "Cardio" to BodyType.CARDIO,
    "Other" to BodyType.OTHER
)

@Parcelize
@Entity(tableName = "exercises")
open class Exercise(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @SerializedName("type") var type: ExerciseType,
    @SerializedName("body") var body: BodyType,
    @SerializedName("name") var name: String = ""
) : Parcelable {
    fun doesMatchSearchQuery(query: String) : Boolean {
        return name.contains(query, ignoreCase = true)
    }
}
@Parcelize
open class ExerciseHolder(
    @Transient open var position: Int = 0,
    @Transient open var sets: Int = 1,
    @Transient open var breakTime: Int = 60,
    @Transient open var exercise: Exercise = Exercise(1, ExerciseType.WEIGHT, BodyType.BACK, "")
) : Parcelable
@Parcelize
data class TimeExercise(
    var seconds: MutableList<Int> = mutableListOf(1),
    override var sets: Int = 1,
    override var breakTime: Int = 60,
    override var exercise: Exercise,
    override var position: Int,
) : ExerciseHolder(sets = sets, exercise = exercise, position = position)
@Parcelize
data class RepsExercise(
    var reps: MutableList<Int> = mutableListOf(1),
    override var sets: Int = 1,
    override var breakTime: Int = 60,
    override var exercise: Exercise,
    override var position: Int,
    ) : ExerciseHolder(sets = sets, exercise = exercise, position = position)
@Parcelize
data class CardioExercise(
    var seconds: MutableList<Int> = mutableListOf(1),
    var km: MutableList<Int> = mutableListOf(1),
    override var sets: Int = 1,
    override var breakTime: Int = 60,
    override var position: Int,
    override var exercise: Exercise
) : ExerciseHolder(sets = sets, exercise = exercise, position = position)
@Parcelize
data class WeightExercise(
    var weights: MutableList<Double> = mutableListOf(1.0),
    var reps: MutableList<Int> = mutableListOf(1),
    override var sets: Int = 1,
    override var breakTime: Int = 60,
    override var position: Int,
    override var exercise: Exercise
) : ExerciseHolder(sets = sets, exercise = exercise, position = position)

@Parcelize
@Entity(tableName = "Workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var exercises: List<ExerciseHolder> = emptyList(),
    var numOfExercises: Int = exercises.size,
    var position: Int = 0,
) : Parcelable, Serializable {
    fun doesMatchSearchQuery(query: String) : Boolean {
        return name.contains(query, ignoreCase = true)
    }
}

@Entity
data class Week(
    var totalNumOfWorkouts: Int = 0,
    var numOfWorkoutsDone: Int = 0,
)

@Entity(tableName = "plans")
data class Plan(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var startingDate: Date = Date(),
    var weeks: Int = 0,
    var workouts: List<Workout> = emptyList(),
    var weeksList: List<Week> = emptyList(),
    var planType: PlanType
) {
    fun doesMatchSearchQuery(query: String) : Boolean {
        return name.contains(query, ignoreCase = true)
    }
    fun matchesFilter(minPerWeek: Int, maxPerWeek: Int, planType: PlanType) : Boolean {
        return workouts.size in minPerWeek..maxPerWeek && planType == this.planType
    }
    fun startWeeks() {
        val n = workouts.size
        for(i: Int in 1..weeks) {
            weeksList = weeksList + Week(
                totalNumOfWorkouts = n,
            )
        }
    }
}
data class HistoryItem(val name: String, val date: Date)

@Entity(tableName = "info")
data class Info(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val username: String = "User",
    val currentPlan: Plan? = null,
    val workoutHistory: MutableList<HistoryItem> = mutableListOf(),
    var planHistory: MutableList<HistoryItem> = mutableListOf(),
    var date: Date = Date()
)
