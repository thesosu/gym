package pl.pollub.andrioid.gym.db.relationships

import androidx.room.Embedded
import androidx.room.Relation
import pl.pollub.andrioid.gym.db.entity.Workout
import pl.pollub.andrioid.gym.db.entity.WorkoutExercise

data class WorkoutWithWorkoutExerciseAndSets (
    @Embedded
    val workout: Workout,
    @Relation(
        entity = WorkoutExercise::class,
        parentColumn = "workout_id",
        entityColumn = "workout_id")
    val exercisesWithSets: List<WorkoutExerciseWithSets>
)