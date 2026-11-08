
import com.example.power.data.room.BodyType
import com.example.power.data.room.CardioExercise
import com.example.power.data.room.Exercise
import com.example.power.data.room.ExerciseType
import com.example.power.data.room.RepsExercise
import com.example.power.data.room.TimeExercise
import com.example.power.data.room.WeightExercise
import com.example.power.data.room.Workout

data class ExerciseConfig(
    val type: String,
    val exerciseIndex: Int,
    val sets: Int = 3
)

/**
 * A builder class to build plan exercises and workouts
 * to add to the app on first launch.
 */
class PlanBuilder {

    private val exercises = mutableListOf<Exercise>()

    // Add a new exercise to the list
    fun addExercise(type: ExerciseType, body: BodyType, name: String): PlanBuilder {
        exercises.add(Exercise(type = type, body = body, name = name))
        return this
    }

    // Get all the exercises
    fun getExercises(): List<Exercise> = exercises

    // Defaults
    private val defaultWeights = mutableListOf(1.0, 1.0, 1.0)
    private val defaultReps = mutableListOf(10, 10, 10)
    private val defaultSeconds = mutableListOf(60, 60, 60)
    private val defaultKm = mutableListOf(1, 1, 1)
    private val defaultSets = 3
    private val defaultBreakTime = 60
    private var position: Int = 0

    // Build a WeightExercise
    fun buildWeightExercise(
        position: Int,
        exerciseIndex: Int,
        sets: Int = defaultSets,
        reps: MutableList<Int> = defaultReps.toMutableList(),
        weights: MutableList<Double> = defaultWeights.toMutableList()
    ): WeightExercise {
        return WeightExercise(
            weights = weights,
            reps = reps,
            sets = sets,
            breakTime = defaultBreakTime,
            position = position,
            exercise = exercises[exerciseIndex]
        )
    }

    // Build a RepsExercise
    fun buildRepsExercise(
        position: Int,
        exerciseIndex: Int,
        sets: Int = defaultSets,
        reps: MutableList<Int> = defaultReps.toMutableList()
    ): RepsExercise {
        return RepsExercise(
            reps = reps,
            sets = sets,
            breakTime = defaultBreakTime,
            position = position,
            exercise = exercises[exerciseIndex]
        )
    }

    // Build a TimeExercise
    fun buildTimeExercise(
        position: Int,
        exerciseIndex: Int,
        sets: Int = defaultSets,
        seconds: MutableList<Int> = defaultSeconds.toMutableList()
    ): TimeExercise {
        return TimeExercise(
            seconds = seconds,
            sets = sets,
            breakTime = defaultBreakTime,
            position = position,
            exercise = exercises[exerciseIndex]
        )
    }

    // Build a CardioExercise
    fun buildCardioExercise(
        position: Int,
        exerciseIndex: Int,
        sets: Int = defaultSets,
        seconds: MutableList<Int> = defaultSeconds.toMutableList(),
        km: MutableList<Int> = defaultKm.toMutableList()
    ): CardioExercise {
        return CardioExercise(
            seconds = seconds,
            km = km,
            sets = sets,
            breakTime = defaultBreakTime,
            position = position,
            exercise = exercises[exerciseIndex]
        )
    }

    // Build workout by passing exercise configurations
    fun buildWorkout(
        dayName: String,
        exerciseConfigs: List<ExerciseConfig>
    ): Workout {
        val workoutExercises = exerciseConfigs.mapIndexed { index, config ->
            when (config.type) {
                "Weight" -> buildWeightExercise(
                    position = index,
                    exerciseIndex = config.exerciseIndex,
                    sets = config.sets
                )

                "Reps" -> buildRepsExercise(
                    position = index,
                    exerciseIndex = config.exerciseIndex,
                    sets = config.sets
                )

                "Time" -> buildTimeExercise(
                    position = index,
                    exerciseIndex = config.exerciseIndex,
                    sets = config.sets
                )

                "Cardio" -> buildCardioExercise(
                    position = index,
                    exerciseIndex = config.exerciseIndex,
                    sets = config.sets
                )

                else -> throw IllegalArgumentException(
                    "Unknown exercise type: ${config.type}"
                )
            }
        }

        val workout = Workout(
            name = dayName,
            exercises = workoutExercises,
            position = this.position
        )

        this.position += 1
        return workout
    }
}